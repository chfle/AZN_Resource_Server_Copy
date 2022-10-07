package com.lokcenter.AZN_Spring_ResourceServer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequestMapping("/overview")
public class OverviewController {
    @ResponseBody
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getOverviewByMonth(
            @RequestParam(name = "firstday", required = true) String firstDay,
            @RequestParam(name = "lastday", required = true) String lastDay,
            @RequestParam(name = "role", required = true) String role,
            @RequestParam(name = "userId", required = false) String userId) {

        // TODO: Get Role
        // TODO: Query Tables and pack it into one JSON BLOB

        return "";
    }
}
