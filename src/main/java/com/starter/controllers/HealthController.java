package com.starter.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final ImmutableMap<String, String> HEALTHY = ImmutableMap.of("response", "healthy");

    @RequestMapping(method = RequestMethod.GET)
    public ImmutableMap getHealth() {
        return HEALTHY;
    }

}
