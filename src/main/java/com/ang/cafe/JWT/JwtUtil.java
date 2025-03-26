package com.ang.cafe.JWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

	private String SECRET = "357638792D423F4428472B213213HGJG54654GHGJ46545GGFHG5464B6250655368566D597133743677397A2443264629";
	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	public <T> T extractClaims(String token, Function<Claims, T> claimsRosolver) {

		final Claims claims = extractAllClaims(token);
		return claimsRosolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {

		return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		return createToken(claims, username);
	}

	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder()
				.claims(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
				.compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
