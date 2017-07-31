#!/bin/bash
set -eo pipefail

cd "$(dirname "$(readlink -f "$BASH_SOURCE")")"

versions=( "$@" )
if [ ${#versions[@]} -eq 0 ]; then
	versions=( */ )
fi
versions=( "${versions[@]%/}" )

declare -A debianSuite=(
	[9.2]='jessie'
	[9.3]='jessie'
	[9.4]='jessie'
	[9.5]='jessie'
	[9.6]='jessie'
	[10]='stretch'
)
declare -A alpineVersion=(
	[9.2]='3.5'
	[9.3]='3.5'
	[9.4]='3.5'
	[9.5]='3.5'
	[9.6]='3.5'
	[10]='3.6'
)

# https://www.mirrorservice.org/sites/ftp.ossp.org/pkg/lib/uuid/?C=M;O=D
osspUuidVersion='1.6.2'
osspUuidHash='11a615225baa5f8bb686824423f50e4427acd3f70d394765bdff32801f0fd5b0'

lastSuite=
travisEnv=
for version in "${versions[@]}"; do
	if [ "${debianSuite[$version]}" != "$lastSuite" ]; then
		lastSuite="${debianSuite[$version]}"
		packagesBase='http://apt.postgresql.org/pub/repos/apt/dists/'"${debianSuite[$version]}"'-pgdg'
		mainList="$(curl -fsSL "$packagesBase/main/binary-amd64/Packages.bz2" | bunzip2)"
	fi

	versionList="$(echo "$mainList"; curl -fsSL "$packagesBase/$version/binary-amd64/Packages.bz2" | bunzip2)"
	fullVersion="$(echo "$versionList" | awk -F ': ' '$1 == "Package" { pkg = $2 } $1 == "Version" && pkg == "postgresql-'"$version"'" { print $2; exit }' || true)"
	(
		set -x
		cp docker-entrypoint.sh "$version/"
		sed -e 's/%%PG_MAJOR%%/'"$version"'/g;' \
			-e 's/%%PG_VERSION%%/'"$fullVersion"'/g' \
			-e 's/%%DEBIAN_SUITE%%/'"${debianSuite[$version]}"'/g' \
			Dockerfile-debian.template > "$version/Dockerfile"
	)

	# TODO figure out what to do with odd version numbers here, like release candidates
	srcVersion="${fullVersion%%-*}"
	# change "10~beta1" to "10beta1" for ftp urls
	srcVersion="${srcVersion//\~/}"
	srcSha256="$(curl -fsSL "https://ftp.postgresql.org/pub/source/v${srcVersion}/postgresql-${srcVersion}.tar.bz2.sha256" | cut -d' ' -f1)"
	for variant in alpine; do
		if [ ! -d "$version/$variant" ]; then
			continue
		fi
		(
			set -x
			cp docker-entrypoint.sh "$version/$variant/"
			sed -i 's/gosu/su-exec/g' "$version/$variant/docker-entrypoint.sh"
			sed -e 's/%%PG_MAJOR%%/'"$version"'/g' \
				-e 's/%%PG_VERSION%%/'"$srcVersion"'/g' \
				-e 's/%%PG_SHA256%%/'"$srcSha256"'/g' \
				-e 's/%%ALPINE-VERSION%%/'"${alpineVersion[$version]}"'/g' \
				"Dockerfile-$variant.template" > "$version/$variant/Dockerfile"
			if [ "${alpineVersion[$version]}" != '3.5' ]; then
				# prove was moved out of the perl package and into perl-utils in 3.6
				# https://pkgs.alpinelinux.org/contents?file=prove&path=&name=&branch=&repo=&arch=x86_64
				sed -ri 's/(\s+perl)(\s+)/\1-utils\2/' "$version/$variant/Dockerfile"
			fi

			# TODO remove all this when 9.2 and 9.3 are EOL (2017-10-01 and 2018-10-01 -- from http://www.postgresql.org/support/versioning/)
			case "$version" in
				9.2|9.3)
					uuidConfigFlag='--with-ossp-uuid'
					sed -i \
						-e 's/%%OSSP_UUID_ENV_VARS%%/ENV OSSP_UUID_VERSION '"$osspUuidVersion"'\nENV OSSP_UUID_SHA256 '"$osspUuidHash"'\n/' \
						-e $'/%%INSTALL_OSSP_UUID%%/ {r ossp-uuid.template\n d}' \
						"$version/$variant/Dockerfile"

					# configure: WARNING: unrecognized options: --enable-tap-tests
					sed -i '/--enable-tap-tests/d' "$version/$variant/Dockerfile"
					;;

				*)
					uuidConfigFlag='--with-uuid=e2fs'
					sed -i \
						-e '/%%OSSP_UUID_ENV_VARS%%/d' \
						-e '/%%INSTALL_OSSP_UUID%%/d' \
						"$version/$variant/Dockerfile"
					;;
			esac
			sed -i 's/%%UUID_CONFIG_FLAG%%/'"$uuidConfigFlag"'/' "$version/$variant/Dockerfile"
		)
		travisEnv="\n  - VERSION=$version VARIANT=$variant$travisEnv"
	done

	travisEnv='\n  - VERSION='"$version$travisEnv"
done

travis="$(awk -v 'RS=\n\n' '$1 == "env:" { $0 = "env:'"$travisEnv"'" } { printf "%s%s", $0, RS }' .travis.yml)"
echo "$travis" > .travis.yml
