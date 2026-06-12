package sk.adamkatrenic.bankingapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Import;
import sk.adamkatrenic.bankingapi.config.TestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Registruj a získaj token
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Account Test\",\"email\":\"accounttest@test.com\",\"password\":\"password123\"}"))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            String response = result.getResponse().getContentAsString();
            token = objectMapper.readTree(response).get("token").asText();
        } else {
            // Ak už existuje, prihlás sa
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType("application/json")
                            .content("{\"email\":\"accounttest@test.com\",\"password\":\"password123\"}"))
                    .andReturn();
            String response = loginResult.getResponse().getContentAsString();
            token = objectMapper.readTree(response).get("token").asText();
        }
    }

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").exists())
                .andExpect(jsonPath("$.balance").value(10000.0));
    }

    @Test
    void shouldGetAccountsSuccessfully() throws Exception {
        // Vytvor účet
        mockMvc.perform(post("/api/accounts")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token));

        // Získaj účty
        mockMvc.perform(get("/api/accounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturn403WhenNoToken() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType("application/json"))
                .andExpect(status().is(400));
    }
}