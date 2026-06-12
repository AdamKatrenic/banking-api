package sk.adamkatrenic.bankingapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import sk.adamkatrenic.bankingapi.config.TestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Test User\",\"email\":\"newuser@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() throws Exception {
        // Registruj prvý krát
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("{\"fullName\":\"Test User\",\"email\":\"duplicate@test.com\",\"password\":\"password123\"}"));

        // Registruj druhý krát s rovnakým emailom
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"fullName\":\"Test User\",\"email\":\"duplicate@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Najprv registruj
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("{\"fullName\":\"Login Test\",\"email\":\"logintest@test.com\",\"password\":\"password123\"}"));

        // Potom prihlás
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"logintest@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"logintest@test.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest());
    }
}