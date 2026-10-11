package com.ekartapplication.ekart.service.user;

import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserCreationRollbackIntegrationTest {

    private static final String USERNAME = "rollback-test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private KartRepository kartRepository;

    @BeforeEach
    void resetTestDataAndRepositoryMocks() {
        jdbcTemplate.update("DELETE FROM user_info WHERE user_name = ?", USERNAME);
        when(walletRepository.addMoneyToWalletInsert(any())).thenReturn(true);
        when(kartRepository.addOneProductToKart(any())).thenReturn(true);
    }

    @Test
    void walletInitializationFailureRollsBackUserInsert() throws Exception {
        when(walletRepository.addMoneyToWalletInsert(any())).thenReturn(false);

        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson()))
                .andExpect(status().isInternalServerError());

        assertEquals(0, userCount());
    }

    @Test
    void cartInitializationFailureRollsBackUserInsert() throws Exception {
        when(kartRepository.addOneProductToKart(any())).thenReturn(false);

        mockMvc.perform(post("/user/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson()))
                .andExpect(status().isInternalServerError());

        assertEquals(0, userCount());
    }

    private int userCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_info WHERE user_name = ?", Integer.class, USERNAME);
    }

    private String userJson() {
        return "{"
                + "\"firstName\":\"Rollback\","
                + "\"lastName\":\"Tester\","
                + "\"userName\":\"" + USERNAME + "\","
                + "\"password\":\"correct-test-password\","
                + "\"emailId\":\"rollback@example.invalid\","
                + "\"mobileNum\":\"5550100\","
                + "\"houseNo\":\"10\","
                + "\"landmark\":\"Test Lane\","
                + "\"city\":\"Test City\","
                + "\"pincode\":\"12345\","
                + "\"role\":\"ROLE_USER\""
                + "}";
    }
}
