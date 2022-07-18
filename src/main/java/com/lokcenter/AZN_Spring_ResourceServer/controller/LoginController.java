package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.sql.Repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.sql.User;
import com.lokcenter.AZN_Spring_ResourceServer.helper.JunitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * LoginController
 *
 * Save first login of a user
 *
 * @version 17-07-22
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    boolean postLogin(@RequestBody Map<String, Object> payload) {
        User user = new User();

        try {
            if (payload.containsKey("username") && payload.containsKey("firstLogin")) {

                user.setUsername((String) payload.get("username"));

                // convert back to a utils.date to use sql.date
                var dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                var utilsDate =dateFormat.parse((String)payload.get("firstLogin"));

                user.setFirstLogin(new java.sql.Date(utilsDate.getTime()));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        // only set if user does not exists
       if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
           userRepository.save(user);
       }

       return true;
    }
}