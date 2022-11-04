package com.lokcenter.AZN_Spring_ResourceServer.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Admin Controller
 * @version 1.0 04-11-2022
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    /**
     * Get all user data needed for the admin panel
     * @return json reprenstation of data
     */
    @GetMapping
    String getUserData() {
        // TODO: check if the request is made from an admin
        // TODO: Get all users
        // TODO: GET Sick, Glaz, available vacation
        // TODO: Get requests
        // TODO: Get Zeitkonto

        return "";
    }

    /**
     * Get YearPlan Data by individual users
     * @param userId User id from the requested user
     *
     * @return json data from an user
     */
    @GetMapping( "/years")
    String getYearsPlanInfoByUser(@RequestParam(name = "userid", required = true) String userId) {
        return "";
    }

    /**
     * update user data by user
     * @param userId User id from the requested user
     *
     * @return boolean
     */
    @PutMapping("/userdata")
    Boolean changeUserData(@RequestParam(name = "userid") String userId) {
        return true;
    }

    /**
     * Get all requests by User
     * @param userId User id from the requested user
     *
     * @return json data from an user
     */
    @GetMapping("/requests")
    String getRequestsByUser(@RequestParam(name = "userId") String userId) {
        return "";
    }
}
