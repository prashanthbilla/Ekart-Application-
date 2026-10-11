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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Test
    void createUserReturnsBadRequestForMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"" + USERNAME + "\",\"password\":\"test-password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("First name is required"));
    }

    @Test
    void createUserReturnsBadRequestForBlankPassword() throws Exception {
        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"" + USERNAME + "\",\"password\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is required"));
    }

    @Test
    void createUserReturnsConflictForExistingUsername() throws Exception {
        createUser();

        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    void userEndpointsReturnNotFoundForUnknownUsername() throws Exception {
        createUser();
        String token = jwtService.generateToken(USERNAME);
        String updateBody = userJson().replace(USERNAME, "missing-user");

        mockMvc.perform(get("/user/v1").param("userName", "missing-user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/user/v1/getRole").param("userName", "missing-user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/user/v1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/user/v1").param("userId", "missing-user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void userGetListRoleUpdateAndDeleteEndpointsSucceed() throws Exception {
        createUser();
        String token = jwtService.generateToken(USERNAME);

        mockMvc.perform(get("/user/v1").param("userName", USERNAME)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userName").value(USERNAME));

        mockMvc.perform(get("/user/v1/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0][0].userName").value(USERNAME));

        mockMvc.perform(get("/user/v1/getRole").param("userName", USERNAME)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ROLE_USER"));

        mockMvc.perform(put("/user/v1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson().replace("\"firstName\":\"Jane\"", "\"firstName\":\"Janet\"")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").isNotEmpty());

        mockMvc.perform(delete("/user/v1").param("userId", USERNAME)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Deleted..."));

        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_info WHERE user_name = ?", Integer.class, USERNAME));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM kart_info WHERE user_id = ?", Integer.class, USERNAME));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wallet_info WHERE user_id = ?", Integer.class, USERNAME));
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
