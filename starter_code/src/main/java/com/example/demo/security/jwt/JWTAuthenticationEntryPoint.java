package com.example.demo.security.jwt;

import com.example.demo.security.response.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException, ServletException {
        log.error("Unauthorized error: {}", ex.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ResponseMessage body = new ResponseMessage();
        body.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        body.setError("Unauthorized");
        body.setMessage(ex.getMessage());
        body.setPath(request.getServletPath());

        ResponseMessage.writeServletResponse(request, response, body);
    }

}
