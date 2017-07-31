package com.bmd.dlook;

import com.bmd.dlook.service.AquiredLockWorker;
import org.springframework.beans.factory.annotation.Autowired;

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

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CountDownLatch;

import com.bmd.dlook.service.lock.impl.RedisLocker;

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

	@Autowired
	RedisLocker distributedLocker;

	@RequestMapping(value = "/redlock")
	public String testRedlock() throws Exception{

		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(5);
		for (int i = 0; i < 5; ++i) { // create and start threads
			new Thread(new Worker(startSignal, doneSignal)).start();
		}
		startSignal.countDown(); // let all threads proceed
		doneSignal.await();
		System.out.println("All processors done. Shutdown connection");
		return "redlock";
	}

	class Worker implements Runnable {
		private final CountDownLatch startSignal;
		private final CountDownLatch doneSignal;

		Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
		}

		public void run() {
			try {
				startSignal.await();
				distributedLocker.lock("test",new AquiredLockWorker<Object>() {

					@Override
					public Object invokeAfterLockAquire() {
						doTask();
						return null;
					}

				});
			}catch (Exception e){

			}
		}

		void doTask() {
			System.out.println(Thread.currentThread().getName() + " start");
			Random random = new Random();
			int _int = random.nextInt(200);
			System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
			try {
				Thread.sleep(_int);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " end");
			doneSignal.countDown();
		}
	}
}
