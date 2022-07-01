package com.lokcenter.AZN_Spring_ResourceServer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Dayplan endpoint
 *
 * @version 1.01 - 01-07-2022
 */

@RestController
@CrossOrigin("http://localhost:8880")
@RequestMapping("/dayplan")
@PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
public class DayPlanController {
    @PostMapping()
    boolean postDayPlan(@RequestBody Map<String, Object> data) {
        System.out.println(data.toString());
        return true;
    }
}
