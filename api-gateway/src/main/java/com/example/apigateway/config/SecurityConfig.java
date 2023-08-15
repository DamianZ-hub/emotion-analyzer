package com.example.apigateway.config;

import com.example.apigateway.filter.CustomPreAuthenticationFilter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig  {

    @Autowired
    CustomPreAuthenticationFilter customPreAuthenticationFilter;

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http, Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter) throws Exception {

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

       // http.cors(cors -> cors.configurationSource(corsConfigurationSource("http://localhost:8080")));

        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.hsts(hsts -> hsts.disable()));



        http
                .addFilterBefore(customPreAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                       // .pathMatchers("/ws", "/sockjs", "/actuator/health/readiness", "/actuator/health/liveness", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/actuator/health/readiness", "/actuator/health/liveness").permitAll()// Allow access to "/pathToIgnore" without authentication
                        .anyExchange().authenticated()// Require authentication for all other paths
                );

        return http.build();
    }


    private CorsConfigurationSource corsConfigurationSource(String... origins) {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(origins));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @RequiredArgsConstructor
    static class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<? extends GrantedAuthority>> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Collection<? extends GrantedAuthority> convert(Jwt jwt) {
            return Stream.of("$.realm_access.roles", "$.resource_access.*.roles").flatMap(claimPaths -> {
                        Object claim;
                        try {
                            claim = JsonPath.read(jwt.getClaims(), claimPaths);
                        } catch (PathNotFoundException e) {
                            claim = null;
                        }
                        if (claim == null) {
                            return Stream.empty();
                        }
                        if (claim instanceof String claimStr) {
                            return Stream.of(claimStr.split(","));
                        }
                        if (claim instanceof String[] claimArr) {
                            return Stream.of(claimArr);
                        }
                        if (Collection.class.isAssignableFrom(claim.getClass())) {
                            final var iter = ((Collection) claim).iterator();
                            if (!iter.hasNext()) {
                                return Stream.empty();
                            }
                            final var firstItem = iter.next();
                            if (firstItem instanceof String) {
                                return (Stream<String>) ((Collection) claim).stream();
                            }
                            if (Collection.class.isAssignableFrom(firstItem.getClass())) {
                                return (Stream<String>) ((Collection) claim).stream().flatMap(colItem -> ((Collection) colItem).stream()).map(String.class::cast);
                            }
                        }
                        return Stream.empty();
                    })
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast).toList();
        }
    }

    @Component
    @RequiredArgsConstructor
    static class SpringAddonsJwtAuthenticationConverter implements Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> {


        @Override
        public Mono<? extends AbstractAuthenticationToken> convert(Jwt source) {
            final var authorities = new JwtGrantedAuthoritiesConverter().convert(source);
            final String username = JsonPath.read(source.getClaims(), "preferred_username");
            return Mono.just(new JwtAuthenticationToken(source, authorities, username)) ;        }

        @Override
        public <U> Converter<Jwt, U> andThen(Converter<? super Mono<? extends AbstractAuthenticationToken>, ? extends U> after) {
            return Converter.super.andThen(after);
        }
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(){
        return new NimbusReactiveJwtDecoder(new ParseOnlyJWTProcessor());
    }

    private static class ParseOnlyJWTProcessor implements Converter<JWT, Mono<JWTClaimsSet>> {
        public Mono<JWTClaimsSet> convert(JWT jwt) {
            try {
                return Mono.just(jwt.getJWTClaimsSet());
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        }
    }


}