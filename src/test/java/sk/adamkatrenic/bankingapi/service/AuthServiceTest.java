package sk.adamkatrenic.bankingapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.adamkatrenic.bankingapi.dto.LoginRequest;
import sk.adamkatrenic.bankingapi.dto.RegisterRequest;
import sk.adamkatrenic.bankingapi.entity.User;
import sk.adamkatrenic.bankingapi.repository.UserRepository;
import sk.adamkatrenic.bankingapi.security.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Adam Katrenic");
        registerRequest.setEmail("adam@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("adam@test.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("adam@test.com");
        testUser.setFullName("Adam Katrenic");
        testUser.setPassword("hashedPassword");
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userRepository.existsByEmail("adam@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken("adam@test.com")).thenReturn("jwt-token");

        String token = authService.register(registerRequest);

        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("adam@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        when(jwtUtil.generateToken("adam@test.com")).thenReturn("jwt-token");

        String token = authService.login(loginRequest);

        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldHashPasswordOnRegister() {
        when(userRepository.existsByEmail("adam@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken("adam@test.com")).thenReturn("jwt-token");

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, never()).save(argThat(u ->
                u.getPassword().equals("password123")
        ));
    }
}