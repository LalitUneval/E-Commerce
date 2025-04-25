package org.lalit.ecommercebackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.lalit.ecommercebackend.config.JwtConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider2 {

    private final JwtConfig jwtConfig;

    public JwtTokenProvider2(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    //create token
    public String createToken(Authentication authentication){


        //getPrincipal has the detail of user like name,password etc.,
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //Converts a Collection<? extends GrantedAuthority> into a comma-separated String.
        //Example: if roles are [ROLE_USER, ROLE_ADMIN], the result will be "ROLE_USER,ROLE_ADMIN".
        String authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("auth",authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 5 * 60 * 1000))
                .signWith(generateKey())
                .compact();

    }
    private SecretKey generateKey(){
        byte [] decode =Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }
    private String getSecretKey(){
        return "yMSC8Ta3Zo0pIDh0MVVrxghb46ks8pqyxfeuNOnSWlY=";
    }

    Authentication getAuthentication(String token){
        Claims claims = Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        //Takes the comma-separated roles string from the JWT claim (e.g., "ROLE_USER,ROLE_ADMIN").
        //Splits it into an array of roles.
        //Maps each role string back into a SimpleGrantedAuthority.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(",")).map(
                        SimpleGrantedAuthority::new
                ).toList();
        User principle = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principle,token,authorities);
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public String extractUsername(String token){
        return Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }


}
