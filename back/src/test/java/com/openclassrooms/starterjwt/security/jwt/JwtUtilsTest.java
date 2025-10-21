package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private String jwtSecret = "testSecretKeyForJwtTokenGenerationAndValidation";
    private int jwtExpirationMs = 3600000; // 1 heure

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void testGenerateJwtToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotNull().isNotEmpty();

        String username = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void testGetUserNameFromJwtToken() {
        String username = "test@example.com";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        String token = "this.is.not.a.valid.jwt.token";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        String token = "";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_NullToken() {
        String token = null;
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_UnsupportedJwtException() {
        // Algo "none" = unsupported signature
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .compact(); // pas de signature

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_IllegalArgumentException() {
        String token = "";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testGenerateJwtToken_TokenContainsCorrectClaims() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(true)
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("john@example.com");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration().getTime()).isGreaterThan(claims.getIssuedAt().getTime());
    }
}
