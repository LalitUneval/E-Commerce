//package org.lalit.ecommercebackend.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.lalit.ecommercebackend.config.JwtConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.security.Key;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtTokenProvider {
//
//    private final JwtConfig jwtConfig;
//
//    @Autowired
//    public JwtTokenProvider(JwtConfig jwtConfig) {
//        this.jwtConfig = jwtConfig;
//    }
//
//    private Key key;
//
//    //this annotation told to spring hey after the injection of dependency
//    // after the creating the instance of this class
//    // use should create the instace of this class method
//    // we can this code into constructor also but some time
//    // we need to make the logic different form constructor
//    @PostConstruct
//    public void init(){
//        //simple it will give the key
//        // we didn't want to convert the key in the byte
//        // and also not need to generate it genearte by this line
//        this.key= Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
//    }
//
//    //Creation of token
//
//    // Generally we pass the use details right?
//    // but when the user  come for the login it will already authenticated
//    // and we have the userDetailsl in the getPrincipal()
//    //Authentication(it is interface and it have some method)
//    // is represent the current authenticated person
//    //
//    public String createToken(Authentication authentication){
//
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        //Getting the authorities form the userDetails
//        // inside the authorites we have one method getAuthorities which give the role
//        // so here we're extracting the role
//        // Roles is set of role_admin role_user (we separate them by "," role_admin,role_user)
//        String authorities = userDetails.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        // here we set the validatoin time
//
//        long now = (new Date()).getTime();
//        Date validation= new Date(now + jwtConfig.getExpiration());
//
//        return
//                Jwts.builder()
//                        .subject(userDetails.getUsername())
//                        .claim("auth",authorities)
//                        .issuedAt(new Date())
//                        .expiration(validation)
//                        .signWith(key, SignatureAlgorithm.HS512)
//                        .compact();
//    }
//
//
//    //after the creation of the token and the user pass it for the accessing other
//    //resource so we can authenticated
//    public Authentication getAuthentication( String token){
//.
//        //validating the token and get the claims
//        Claims claims = Jwts.parser()
//                .verifyWith((SecretKey) key)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get("auth").toString().split(",")).map(
//                        SimpleGrantedAuthority::new
//                ).toList();
//        //here password is blank because we are not used the password for the authentication
//        User principle = new User(claims.getSubject(),"",authorities);
//
//        //simple it was return authentication object
//        // UsernamePasswordAuthentication extend AbstractAuthenticationToken ,
//        // AbstractAuthenticationToken implements authentication
//
//        return  new UsernamePasswordAuthenticationToken(principle , token , authorities);
//    }
//
//    public boolean validateToken(String token){
//        try{
//            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
//            return true;
//        }
//        catch(JwtException | IllegalArgumentException e){
//            return false;
//        }
//    }
//    public String extractUsername(String token){
//        return Jwts.parser()
//                .verifyWith((SecretKey) key)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//}
//
///*
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtTokenProvider {
//
//
//
//    public String createToken(Authentication authentication) {
//
//
//
//
//                 String authorities = userDetails.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//
//
//
//
//            return Jwts.builder()
//                    .claims(claims)
//                    .subject(userDetails.getUsername())
//                    .issuedAt(new Date(System.currentTimeMillis()))
//                    .expiration(new Date(System.currentTimeMillis() + 5* 60 * 1000)) // 5 minutes
//                    .signWith(generateKey())
//                    .compact();
//
//
//    }
//
//    private SecretKey generateKey(){
//        byte [] decode=Decoders.BASE64.decode(getSecretKey());
//        return Keys.hmacShaKeyFor(decode);
//    }
//    private String getSecretKey(){
//        return "yMSC8Ta3Zo0pIDh0MVVrxghb46ks8pqyxfeuNOnSWlY=";
//    }
//
//    private Claims getClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(generateKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//   public String extractUsername(String token){
//        return  getClaims(token).getSubject();
//   }
//   public List<String> extractRoles(String token){
//        List<String> listOfRole =getClaims(token).get("roles",List.class);
//       System.out.println("roles is :"+listOfRole);
//        return listOfRole;
//   }
//   public boolean validateToken(UserDetails userDetails , String token){
//        return extractUsername(token).equals(userDetails.getUsername()) && !tokenExpired(token);
//   }
//   public boolean tokenExpired(String token){
//        return getClaims(token).getExpiration().before(new Date());
//   }
//}
//
//*/
//
//
