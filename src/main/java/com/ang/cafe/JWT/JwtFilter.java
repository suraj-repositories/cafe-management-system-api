package com.ang.cafe.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomerUserDetailService customerUserDetailService;

	private String userName = null;
	private Claims claims;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
			filterChain.doFilter(request, response);
		} else {
			String authrizationHeader = request.getHeader("Authorization");
			String token = null;

			if (authrizationHeader != null && authrizationHeader.startsWith("Bearer ")) {
				token = authrizationHeader.substring(authrizationHeader.lastIndexOf(" ") + 1);
				
				userName = jwtUtil.extractUsername(token);
				claims = jwtUtil.extractAllClaims(token);
				
			}

			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = customerUserDetailService.loadUserByUsername(userName);
				
				if (jwtUtil.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					
				}
			}
			filterChain.doFilter(request, response);
		}

	}

	public boolean isAdmin() throws Exception{
		log.info("Claims : {} | {}", claims, (String) claims.get("role"));
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}
	public boolean isUser() throws Exception{
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}
	
	public String getCurrentUser() {
		return userName;
	}
	
}
