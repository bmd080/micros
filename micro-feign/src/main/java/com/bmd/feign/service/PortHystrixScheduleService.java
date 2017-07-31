package com.bmd.feign.service;

import org.springframework.stereotype.Component;

@Component
public class PortHystrixScheduleService implements PortScheduleService {
    @Override
    public String showPort(String name) {
        return "sorry "+name;
    }
}
