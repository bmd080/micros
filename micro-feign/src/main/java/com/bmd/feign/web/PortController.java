package com.bmd.feign.web;

import com.bmd.feign.service.PortScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortController {

    @Autowired
    PortScheduleService portScheduleService;

    @RequestMapping(value = "/port",method = RequestMethod.GET)
    public String showPort(@RequestParam String name){
        return portScheduleService.showPort(name);
    }
}
