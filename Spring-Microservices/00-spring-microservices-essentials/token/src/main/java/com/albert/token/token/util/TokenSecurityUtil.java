package com.albert.token.token.util;

import java.text.ParseException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenSecurityUtil
{
    private TokenSecurityUtil() {}

    public static void setTokenInsideSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            String username = jwtClaimsSet.getSubject();
            if(username == null)
                throw new JOSEException("Subject missing from JWTClaimSet.");

            List<String> claimList = jwtClaimsSet.getStringListClaim("authorities");
            List<SimpleGrantedAuthority> authorities = claimList.stream().map(SimpleGrantedAuthority::new).toList();
            var token = new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(token);
        } 
        catch (ParseException | JOSEException e) {
            log.error("Failed to insert SignedJWT inside Security Context", e);
            SecurityContextHolder.clearContext();
        }
    }
}
