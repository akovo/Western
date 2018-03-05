package com.starter.security.endpoint;

import com.starter.security.auth.jwt.extractor.TokenExtractor;
import com.starter.security.auth.jwt.verifier.TokenVerifier;
import com.starter.security.config.JwtSettings;
import com.starter.security.config.WebSecurityConfig;
import com.starter.security.exceptions.InvalidJwtToken;
import com.starter.security.model.token.JwtToken;
import com.starter.security.model.token.JwtTokenFactory;
import com.starter.security.model.token.RawAccessJwtToken;
import com.starter.security.model.token.RefreshToken;
import com.starter.security.user.CustomUserDetailsService;
import com.starter.security.user.UserContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class RefreshTokenEndpoint {

    private final JwtTokenFactory tokenFactory;
    private final CustomUserDetailsService userDetailsService;
    private final TokenVerifier tokenVerifier;
    private final TokenExtractor tokenExtractor;

    @Autowired
    public RefreshTokenEndpoint(JwtTokenFactory tokenFactory, CustomUserDetailsService userDetailsService, TokenVerifier tokenVerifier, @Qualifier("jwtHeaderTokenExtractor") TokenExtractor tokenExtractor) {
        this.tokenFactory = tokenFactory;
        this.userDetailsService = userDetailsService;
        this.tokenVerifier = tokenVerifier;
        this.tokenExtractor = tokenExtractor;
    }

    @RequestMapping(value = "/api/auth/token", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        val tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.JWT_TOKEN_HEADER_PARAM));

        val rawToken = new RawAccessJwtToken(tokenPayload);
        val refreshToken = RefreshToken.create(rawToken, JwtSettings.getTokenSigningKey()).orElseThrow(InvalidJwtToken::new);

        val jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }

        val subject = refreshToken.getSubject();
        val userDetails = userDetailsService.loadUserByUsername(subject);

        if (CollectionUtils.isEmpty(userDetails.getAuthorities())) {
            throw new InvalidJwtToken();
        }

        val userContext = UserContext.create(userDetails.getUsername(), userDetails.getAuthorities());
        return tokenFactory.createAccessJwtToken(userContext);
    }

}
