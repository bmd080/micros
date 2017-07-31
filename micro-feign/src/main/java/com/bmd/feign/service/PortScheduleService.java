package com.bmd.feign.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "micro-server",fallback = PortHystrixScheduleService.class)
public interface PortScheduleService {
    @RequestMapping(value = "/port",method = RequestMethod.GET)
    String showPort(@RequestParam(value = "name") String name);
}
