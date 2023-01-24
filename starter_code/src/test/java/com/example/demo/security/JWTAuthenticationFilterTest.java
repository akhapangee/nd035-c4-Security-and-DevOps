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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

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
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
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
        byte[] jsonUserData = ow.writeValueAsBytes(SampleData.getSampleUser());

        ServletInputStream servletInputStream = new DelegatingServletInputStream(new ByteArrayInputStream(jsonUserData));

        when(httpServletRequest.getInputStream()).thenReturn(servletInputStream);

        Assertions.assertDoesNotThrow(() -> {
            jwtAuthenticationFilter.attemptAuthentication(httpServletRequest, httpServletResponse);
        });


    }


    @Test
    public void test_successfulAuthentication_and_Token_Generation() throws ServletException, IOException {
        org.springframework.security.core.userdetails.User user = new User("user", "password", Collections.emptyList());

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authentication.getPrincipal()).thenReturn(user);

        jwtAuthenticationFilter.successfulAuthentication(request, response, filterChain, authentication);

        Assertions.assertNotNull(response.getHeader("Authorization"));

    }

}
