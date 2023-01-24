package com.example.demo.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.when;

public class JWTAuthenticationVerificationFilterTest {

    public static final String EXPIRED_TOKEN =
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJha2hpbGVzaCIsImV4cCI6MTY3NDU3OTEyOX0.ico-mV8gzTii-N4Hzm1xzt0Pb21Kp6oarF3jNhicVYVysp4COClrCugLgEzvwvhnM-pHlldxDEUPb7lm86T6Uw";


    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Authentication authentication;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @InjectMocks
    private JWTAuthenticationVerificationFilter verificationFilter;


    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        verificationFilter = new JWTAuthenticationVerificationFilter(authenticationManager);

    }


    @Test
    public void test_doFilterInternal_getAuthentication_Expired_Token() throws ServletException, IOException {
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(EXPIRED_TOKEN);

        Assertions.assertThrows(TokenExpiredException.class, () -> {
            verificationFilter.doFilterInternal(request, response, filterChain);
        });

    }


}
