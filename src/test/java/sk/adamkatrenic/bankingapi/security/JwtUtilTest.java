package sk.adamkatrenic.bankingapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly12345");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtil.generateToken("test@test.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtUtil.generateToken("test@test.com");
        String email = jwtUtil.extractEmail(token);
        assertEquals("test@test.com", email);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken("test@test.com");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void shouldInvalidateTokenForWrongEmail() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.string"));
    }
}