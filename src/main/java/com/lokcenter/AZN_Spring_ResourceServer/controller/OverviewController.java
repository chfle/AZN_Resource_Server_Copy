package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
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
            @RequestParam(name = "firstday", required = false) String firstDay,
            @RequestParam(name = "lastday", required = false) String lastDay,
            @RequestParam(name = "role", required = true) String role,
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "month", required = false) String month) throws JsonProcessingException {

        // TODO: Get Role
        // TODO: Query Tables and pack it into one JSON BLOB



        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(new DayPlanData());
    }
}
