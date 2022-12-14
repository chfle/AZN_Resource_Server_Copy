package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.MessagesRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Message Table related requests
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PutMapping("true")
    Boolean messageWasRead(@RequestParam(name = "messageId", required = true) String messageId, Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();

        String name = jwt.getClaim("unique_name");

        // get userId;
        Optional<Users> user = userRepository.findByUsername(name);

        if (user.isPresent()) {
            messagesRepository.setRead(true, Long.parseLong(messageId), user.get().getUserId());
            return true;
        }


        return false;
    }
}
