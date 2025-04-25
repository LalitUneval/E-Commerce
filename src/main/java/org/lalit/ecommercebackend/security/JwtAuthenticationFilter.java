package org.lalit.ecommercebackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.validator.constraints.CodePointLength;
import org.lalit.ecommercebackend.config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    private final JwtTokenProvider2 jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(JwtConfig jwtConfig, JwtTokenProvider2 jwtTokenProvider) {
        this.jwtConfig = jwtConfig;

        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String jwt = getJwtFromRequest(request);
            System.out.println(jwt);

            if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)){
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                //know provding the authenticatoin this token
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }}catch(Exception ex){
            logger.error("Could not set user authentication in context holder "+ex);
        }
        filterChain.doFilter(request,response);
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
     //here StringUtils.hasText() method will check that
        // is string null , or empyt ,or contating whiteshape only if yes -> false
        // else false
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getPrefix() + " ")) {
            return bearerToken.substring(jwtConfig.getPrefix().length() + 1);
        }
        return null;
    }
}
