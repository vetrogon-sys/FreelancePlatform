package org.example.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.dto.LoginDto;
import org.example.dto.RestResponse;
import org.example.security.jwt.JWTFilter;
import org.example.security.jwt.TokenProvider;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController() {
    }

    @PostMapping("/register")
    public RestResponse register(@RequestBody LoginDto login) {
        if (userService.save(login)) {
            return RestResponse.generateSuccessfulResponse("Saved");
        } else {
            return RestResponse.generateFailedResponse("This login already exists in the system");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTokenProvider(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public void setAuthenticationManagerBuilder(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AuthController)) return false;
        final AuthController other = (AuthController) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$userService = this.getUserService();
        final Object other$userService = other.getUserService();
        if (this$userService == null ? other$userService != null : !this$userService.equals(other$userService))
            return false;
        final Object this$tokenProvider = this.getTokenProvider();
        final Object other$tokenProvider = other.getTokenProvider();
        if (this$tokenProvider == null ? other$tokenProvider != null : !this$tokenProvider.equals(other$tokenProvider))
            return false;
        final Object this$authenticationManagerBuilder = this.getAuthenticationManagerBuilder();
        final Object other$authenticationManagerBuilder = other.getAuthenticationManagerBuilder();
        if (this$authenticationManagerBuilder == null ? other$authenticationManagerBuilder != null : !this$authenticationManagerBuilder.equals(other$authenticationManagerBuilder))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AuthController;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $userService = this.getUserService();
        result = result * PRIME + ($userService == null ? 43 : $userService.hashCode());
        final Object $tokenProvider = this.getTokenProvider();
        result = result * PRIME + ($tokenProvider == null ? 43 : $tokenProvider.hashCode());
        final Object $authenticationManagerBuilder = this.getAuthenticationManagerBuilder();
        result = result * PRIME + ($authenticationManagerBuilder == null ? 43 : $authenticationManagerBuilder.hashCode());
        return result;
    }

    public String toString() {
        return "AuthController(userService=" + this.getUserService() + ", tokenProvider=" + this.getTokenProvider() + ", authenticationManagerBuilder=" + this.getAuthenticationManagerBuilder() + ")";
    }

    public UserService getUserService() {
        return this.userService;
    }

    public TokenProvider getTokenProvider() {
        return this.tokenProvider;
    }

    public AuthenticationManagerBuilder getAuthenticationManagerBuilder() {
        return this.authenticationManagerBuilder;
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
