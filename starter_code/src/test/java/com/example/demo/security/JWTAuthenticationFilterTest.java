package com.example.demo.security;

import com.example.demo.samples.SampleData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.when;

public class JWTAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager);
    }

    @Test
    public void test_attemptAuthentication() throws IOException {
        ObjectMapper ow = new ObjectMapper();
        System.out.println(ow.writeValueAsString(SampleData.getSampleUser()));
        byte[] jsonUserData = ow.writeValueAsBytes(SampleData.getSampleUser());

        ServletInputStream servletInputStream = new DelegatingServletInputStream(
                new ByteArrayInputStream(jsonUserData));

        when(request.getInputStream()).thenReturn(servletInputStream);

        Assertions.assertDoesNotThrow(() -> {
            jwtAuthenticationFilter.attemptAuthentication(request, response);
        });

    }


    @Test
    @WithUserDetails
    public void test_successfulAuthentication() throws ServletException, IOException {
        org.springframework.security.core.userdetails.User user = new User("user", "password", Collections.emptyList());

        when(authentication.getPrincipal()).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> {
            jwtAuthenticationFilter.successfulAuthentication(request, response, filterChain, authentication);
        });

    }

}
