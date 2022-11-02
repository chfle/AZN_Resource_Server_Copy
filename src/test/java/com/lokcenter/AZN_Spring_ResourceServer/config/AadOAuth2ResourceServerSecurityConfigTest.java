package com.lokcenter.AZN_Spring_ResourceServer.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.services.MemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Access Control Tests
 *
 * @version 1.01 - 17-07-2022
 */

@SpringBootTest
@AutoConfigureMockMvc
class AadOAuth2ResourceServerSecurityConfigTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemService memService;

    private static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    @Test
    @DisplayName("CSRF should be disabled")
    @WithMockUser(authorities = {"SCOPE_UserApi.Write"})
    void csrf_should_be_disabled() throws Exception {
        // create object
        Map<String, Object> data = new HashMap<>();

        data.put("test", "test");

        mvc.perform(post("/dayplan")
                .contentType(APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(data))
                .header("Origin","http://localhost:8880"))
                .andExpect(status().isOk());
    }

   @Test
   @DisplayName("cors should fail if uri is wrong")
   @WithMockUser(authorities = {"SCOPE_UserApi.Write"})
   void cors_fail_with_wrong_uri() throws Exception {
       Map<String, Object> data = new HashMap<>();

       data.put("test", "test");

       mvc.perform(post("/dayplan")
                       .contentType(APPLICATION_JSON_UTF8)
                       .content(new ObjectMapper().writeValueAsString(data))
                       .header("Origin","http://localhost:7700"))
               .andExpect(status().isForbidden());

       mvc.perform(post("/dayplan")
                       .contentType(APPLICATION_JSON_UTF8)
                       .content(new ObjectMapper().writeValueAsString(data))
                       .header("Origin", "http://google.com"))
               .andExpect(status().isForbidden());

       mvc.perform(post("/dayplan")
                       .contentType(APPLICATION_JSON_UTF8)
                       .content(new ObjectMapper().writeValueAsString(data))
                       .header("Origin", "http://www.google.com"))
               .andExpect(status().isForbidden());

       mvc.perform(post("/dayplan")
                       .contentType(APPLICATION_JSON_UTF8)
                       .content(new ObjectMapper().writeValueAsString(data))
                       .header("Origin", "http://chfle.org"))
               .andExpect(status().isForbidden());
   }

   @Test
   @DisplayName("Request should not work with unauthenticated user")
   void request_with_unauthenticated_user_should_fail() throws Exception {
       Map<String, Object> data = new HashMap<>();

       data.put("test", "test");

       mvc.perform(post("/dayplan")
                       .contentType(APPLICATION_JSON_UTF8)
                       .content(new ObjectMapper().writeValueAsString(data))
                       .header("Origin","http://localhost:8880"))
               .andExpect(status().isUnauthorized());
   }
}