package org.lalit.ecommercebackend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {


    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;
}
