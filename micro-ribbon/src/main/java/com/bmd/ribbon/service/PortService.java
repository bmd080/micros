package com.bmd.ribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PortService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "showPortError")
    public String showPort(String name) {
        return restTemplate.getForObject("http://micro-server/port?name="+name,String.class);
    }

    public String showPortError(String name) {
        return "hi,"+name+",sorry,error!";
    }
}
