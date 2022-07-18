package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.sql.Repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.sql.User;
import com.lokcenter.AZN_Spring_ResourceServer.helper.JunitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<Boolean> postLogin(@RequestBody Map<String, Object> payload) {
        User user = new User();

        try {
            if (payload.containsKey("username") && payload.containsKey("firstLogin")) {

                System.out.println(payload.get("firstLogin"));
                user.setUsername((String) payload.get("username"));

                // convert back to an utils.date to use sql.date
                var dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                var utilsDate =dateFormat.parse((String)payload.get("firstLogin"));

                user.setFirstLogin(new java.sql.Date(utilsDate.getTime()));
            } else {
               throw new Exception("Bad request");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }

        // check if not junit
        if (!JunitHelper.isJUnitTest())  {
            // only set if user does not exist
            if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
                userRepository.save(user);
            }
        }

       return new ResponseEntity<>(true, HttpStatus.OK);
    }
}