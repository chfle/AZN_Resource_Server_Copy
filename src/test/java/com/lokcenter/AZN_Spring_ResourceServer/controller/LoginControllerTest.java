package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);


    @Test
    @DisplayName("/login - post should only work with SCOPE_UserApi.Write permission")
    @WithMockUser(authorities = {"SCOPE_UserApi.Write"})
    void loginPost_should_only_allow_write_permission() throws Exception {
        Map<String, Object> data = new HashMap<>();

        data.put("test", "test");

        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/login - post should not work with SCOPE_UserApi.Read permission")
    @WithMockUser(authorities = {"SCOPE_UserApi.Read"})
    void loginPost_should_not_work_with_read_permission() throws Exception {
        Map<String, Object> data = new HashMap<>();

        data.put("test", "test");

        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/login - post should work with good and bad payload")
    @WithMockUser(authorities = {"SCOPE_UserApi.Write"})
    void loginPost_with_good_and_bad_payload() throws Exception{
        // Test with no payload
        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(new HashMap<String, Object>()))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isBadRequest());

        // Test with wrong payload
        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper()
                                .writeValueAsString(Map.of("password", "23jlkjewr", "username", "chris")))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isBadRequest());

        // test with right payload
        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper()
                                .writeValueAsString(Map.of("firstLogin", "18/Jul/2022", "username", "chris")))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isOk());

        // test with more payload as expected
        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        Map.of(
                                                "firstLogin", "18/Jul/2022",
                                                "username", "chris",
                                                "password", "chris")))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isOk());

        // test with wrong formatted payload
        mvc.perform(post("/login")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper()
                                // date is wrong
                                .writeValueAsString(Map.of("firstLogin", "18/43/20", "username", "chris")))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isBadRequest());
    }
}