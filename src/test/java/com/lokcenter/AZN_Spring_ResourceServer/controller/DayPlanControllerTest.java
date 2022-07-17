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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DayPlanControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);


    @Test
    @DisplayName("/dayplan - post should only work with SCOPE_UserApi.Write permission")
    @WithMockUser(authorities = {"SCOPE_UserApi.Write"})
    void dayplanPost_should_only_allow_write_permission() throws Exception {
        Map<String, Object> data = new HashMap<>();

        data.put("test", "test");

        mvc.perform(post("/dayplan")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/dayplan - post should not work with SCOPE_UserApi.Read permission")
    @WithMockUser(authorities = {"SCOPE_UserApi.Read"})
    void dayplanPost_should_not_work_with_read_permission() throws Exception {
        Map<String, Object> data = new HashMap<>();

        data.put("test", "test");

        mvc.perform(post("/dayplan")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Origin","http://localhost:8880"))
                .andExpect(status().isForbidden());
    }
}