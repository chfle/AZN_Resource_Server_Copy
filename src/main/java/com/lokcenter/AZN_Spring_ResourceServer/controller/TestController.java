package com.lokcenter.AZN_Spring_ResourceServer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("test")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getHello(Authentication authentication) {
        System.out.println(authentication.toString());
        return "hello client...";
    }
}
