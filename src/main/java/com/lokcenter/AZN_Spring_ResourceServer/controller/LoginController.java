package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.testing.JunitHelper;
import com.lokcenter.AZN_Spring_ResourceServer.helper.NullType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
       Users user = new Users();;

        try {
            if (payload.containsKey("username") && payload.containsKey("roles")) {
                user.setUsername((String) payload.get("username"));

                // convert back to an utils.date to use sql.date
                Date currentDate = new Date();

                user.setFirstLogin(new java.sql.Date(currentDate.getTime()));

                // Add roles
                var roles = (ArrayList<LinkedHashMap<String, String>>) payload.get("roles");

                Map<String, NullType> rolesMap = new HashMap<>();

                for (var role: roles) {
                    rolesMap.put(role.get("authority"), new NullType());
                }

                user.setRoles(rolesMap);

                // try to insert user but check if not a junit test
                if (!JunitHelper.isJUnitTest()) {
                    userRepository.save(user);
                }
            } else {
               throw new Exception("Bad request");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }


       return new ResponseEntity<>(true, HttpStatus.OK);
    }
}