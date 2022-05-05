package com.hashedin.csvapi.filter;


import com.hashedin.csvapi.errorHandling.ApplicationException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
@Component

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("x-auth-token");
        if (!token.equals("xyz")) {
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,"NOT AUTHORIZED");
        }

        chain.doFilter(request, response);
    }
}
