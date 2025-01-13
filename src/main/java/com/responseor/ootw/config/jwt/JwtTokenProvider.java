package com.responseor.ootw.config.jwt;

import com.responseor.ootw.service.auth.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 (7일)
    private final long tokenValidTime = 7 * 24 * 60 * 60 * 1000L;

    private final UserDetailServiceImpl userDetailsServiceImpl;

    /**
     * SecretKey 초기화
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * JWT 토큰 생성
     *
     * @param userPk 유저 고유 식별자
     * @param roles  유저의 역할
     * @return JWT 토큰 문자열
     */
    public String generateToken(Long userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userPk)); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key/value 쌍으로 저장됩니다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘
                // signature 에 들어갈 secret 값 세팅
                .compact();
    }
    /**
     * 토큰에서 인증 정보 조회
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(this.getUserUuid(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰에서 사용자 식별자(PK) 추출
     *
     * @param token JWT 토큰
     * @return 사용자 식별자
     */
    public String getUserUuid(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();

    }

    /**
     * 토큰에서 사용자 식별자(PK) 추출 (현재 로그인 한 회원 기준으로)
     *
     * @param request HttpServletRequest
     * @return 사용자 식별자
     */    public Long getUserUuidLoginUser(HttpServletRequest request) {
        String token = this.resolveToken(request);

        return Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }

    /**
     * JWT 토큰의 유효성 및 만료 확인
     *
     * @param jwtToken JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Request Header에서 Authorization 헤더 값 추출
     *
     * @param request HttpServletRequest
     * @return JWT 토큰
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
