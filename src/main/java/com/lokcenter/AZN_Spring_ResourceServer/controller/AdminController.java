package com.lokcenter.AZN_Spring_ResourceServer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
    ResponseEntity<String> getUserData(Authentication auth, @RequestBody Map<String, Object> payload) throws Exception {
        // extract role
        if (payload.containsKey("role")) {
            String role = (String) payload.get("role");
            if (!role.equals("ROLE_Admin")) {
                // user should not be allowed!
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>("", HttpStatus.OK);
                // TODO: Get all users
                // TODO: GET Sick, Glaz, available vacation
                // TODO: Get requests
                // TODO: Get Zeitkonto
            }
        } else {
            return new ResponseEntity<>("", HttpStatus.CONFLICT);
        }
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
