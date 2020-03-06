package com.example.springpublisher.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFilter extends
        OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain)
          throws ServletException, IOException {
    filterChain.doFilter(request, response);

    if (response.getStatus() == 302){
      System.out.println("hola" + response.getHeader("Location"));
    }
  }
}
