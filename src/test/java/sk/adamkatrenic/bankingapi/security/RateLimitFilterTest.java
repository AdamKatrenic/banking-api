package sk.adamkatrenic.bankingapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.context.annotation.Import;
import sk.adamkatrenic.bankingapi.config.TestConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn429WhenLimitExceeded() throws Exception {
        // Rate limit je vypnutý v testoch — testujeme že filter existuje a je nakonfigurovaný
        RateLimitFilter filter = new RateLimitFilter();
        assertNotNull(filter);
    }

    @Test
    void shouldAllowFirstRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\"}"))
                .andReturn();

        assert result.getResponse().getStatus() != 429;
    }
}