package sk.adamkatrenic.bankingapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String accountNumber;

    @BeforeEach
    void setUp() throws Exception {
        // Register
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"TX Test\",\"email\":\"txtest@test.com\",\"password\":\"password123\"}"))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        } else {
            MvcResult login = mockMvc.perform(post("/api/auth/login")
                            .contentType("application/json")
                            .content("{\"email\":\"txtest@test.com\",\"password\":\"password123\"}"))
                    .andReturn();
            token = objectMapper.readTree(login.getResponse().getContentAsString()).get("token").asText();
        }

        // Vytvor účet
        MvcResult accountResult = mockMvc.perform(post("/api/accounts")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        accountNumber = objectMapper.readTree(accountResult.getResponse().getContentAsString())
                .get("accountNumber").asText();
    }

    @Test
    void shouldDepositSuccessfully() throws Exception {
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"accountNumber\":\"" + accountNumber + "\",\"amount\":500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void shouldWithdrawSuccessfully() throws Exception {
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"accountNumber\":\"" + accountNumber + "\",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void shouldFailWhenInsufficientFunds() throws Exception {
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"accountNumber\":\"" + accountNumber + "\",\"amount\":999999}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetTransactionHistory() throws Exception {
        // Deposit first
        mockMvc.perform(post("/api/transactions/deposit")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content("{\"accountNumber\":\"" + accountNumber + "\",\"amount\":100}"));

        // Get history
        mockMvc.perform(get("/api/transactions/history/" + accountNumber)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}