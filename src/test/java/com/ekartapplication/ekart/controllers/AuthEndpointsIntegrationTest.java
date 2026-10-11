package com.ekartapplication.ekart.controllers;

import com.ekartapplication.ekart.config.UserInfoUserDetailsService;
import com.ekartapplication.ekart.service.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthEndpointsIntegrationTest {

    private static final String USERNAME = "jane-test";
    private static final String PASSWORD = "correct-test-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanTestRows() {
        jdbcTemplate.update("DELETE FROM kart_info WHERE user_id = ?", USERNAME);
        jdbcTemplate.update("DELETE FROM wallet_info WHERE user_id = ?", USERNAME);
        jdbcTemplate.update("DELETE FROM user_info WHERE user_name = ?", USERNAME);
    }

    @Test
    void createUserPersistsAccountAndInitializesWalletAndCart() throws Exception {
        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").isNotEmpty());

        String storedPassword = jdbcTemplate.queryForObject(
                "SELECT paassword FROM user_info WHERE user_name = ?", String.class, USERNAME);
        assertTrue(passwordEncoder.matches(PASSWORD, storedPassword));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wallet_info WHERE user_id = ?", Integer.class, USERNAME));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM kart_info WHERE user_id = ?", Integer.class, USERNAME));
    }

    @Test
    void authenticateCreatedUserReturnsValidJwt() throws Exception {
        createUser();
        assertTrue(passwordEncoder.matches(
                PASSWORD, userDetailsService.loadUserByUsername(USERNAME).getPassword()));

        String token = mockMvc.perform(post("/auth/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + USERNAME + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String jwt = objectMapper.readTree(token).get("token").asText();

        assertEquals(USERNAME, jwtService.extractUsername(jwt));
    }

    @Test
    void authenticateRejectsIncorrectPassword() throws Exception {
        createUser();

        mockMvc.perform(post("/auth/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + USERNAME + "\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void messageEndpointIsPublicAndReturnsSuccess() throws Exception {
        mockMvc.perform(get("/auth/v1/mes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value("Success"));
    }

    private void createUser() throws Exception {
        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson()))
                .andExpect(status().isOk());
    }

    private String userJson() {
        return "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Tester\","
                + "\"userName\":\"" + USERNAME + "\","
                + "\"password\":\"" + PASSWORD + "\","
                + "\"emailId\":\"jane.test@example.invalid\","
                + "\"mobileNum\":\"5550100\","
                + "\"houseNo\":\"10\","
                + "\"landmark\":\"Test Lane\","
                + "\"city\":\"Test City\","
                + "\"pincode\":\"12345\","
                + "\"role\":\"ROLE_USER\""
                + "}";
    }
}
