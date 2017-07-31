package com.bmd.ribbon.web;

import com.bmd.ribbon.service.PortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortController {

    @Autowired
    PortService portService;

    @RequestMapping(value = "/port")
    public String showPort(@RequestParam String name){
        return portService.showPort(name);
    }

}
