package com.bmd.sbserv;

import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.annotation.Bean;

import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
//@EnableEurekaClient
@EnableDiscoveryClient
@RestController
@RefreshScope
@EnableHystrix
@EnableHystrixDashboard
public class Application {

	private static final Logger LOG = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Value("${data.test}")
	String test;
	@RequestMapping(value = "/test")
	public String showTest(){
		return test;
	}

	@RequestMapping("/info")
	public String showInfo(){
		LOG.log(Level.INFO, "micro-sbserv showInfo is being called");
		return "micro-sbserv showInfo is being called";
	}

	@RequestMapping("/port")
	@HystrixCommand(fallbackMethod = "showPortError")
	public String showPort(@RequestParam(value = "name") String name){
		LOG.log(Level.INFO, "micro-sbserv showPort is being called");
		return restTemplate.getForObject("http://micro-server:8762/port",String.class,name);
	}
	public String showPortError(String name) {
		return "micro-server showPort has error[name=" + name + "]!";
	}

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@Bean
	public AlwaysSampler defaultSampler(){
		return new AlwaysSampler();
	}
}
