package com.example.demo.security.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class ResponseMessage {
    private int status;
    private String error;
    private String message;
    private String path;

    public static void writeServletResponse(HttpServletRequest request, HttpServletResponse response, ResponseMessage messageBody) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), messageBody);
    }
}
