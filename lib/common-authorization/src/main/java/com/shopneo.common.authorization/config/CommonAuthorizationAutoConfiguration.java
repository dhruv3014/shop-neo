package com.shopneo.common.authorization.config;

import com.shopneo.common.authorization.client.AuthorizationHeaderInterceptor;
import com.shopneo.common.authorization.jwt.TokenValidator;
import com.shopneo.common.authorization.security.JwtAuthenticationFilter;
import com.shopneo.common.authorization.web.PathExclusionMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

/**
 * Auto-configuration for common authorization.
 * Provides JWT-based authentication filter, security filter chain, and header propagation.
 */
@Configuration
@EnableWebSecurity
@PropertySource(
    value = "classpath:common-authorization-application.yml",
    factory = YamlPropertySourceFactory.class)
@SuppressWarnings({"unused", "java:S4502"})
public class CommonAuthorizationAutoConfiguration {

  /**
   * Creates the path exclusion matcher bean.
   */
  @Bean
  public PathExclusionMatcher pathExclusionMatcher(CommonAuthorizationProperties properties) {
    return new PathExclusionMatcher(properties);
  }

  /**
   * Creates the token validator bean.
   * Only created if jwt-secret or jwt-public-key is configured.
   */
  @Bean
  @ConditionalOnExpression("'${common-authorization.jwt-secret:}' != '' or '${common-authorization.jwt-public-key:}' != ''")
  public TokenValidator tokenValidator(CommonAuthorizationProperties properties) {
    return new TokenValidator(properties);
  }

  @Bean
  @ConfigurationProperties("common-authorization")
  CommonAuthorizationProperties authorizationProperties() {
    return new CommonAuthorizationProperties();
  }

  /**
   * Creates the JWT authentication filter bean.
   */
  @Bean
  @ConditionalOnExpression("'${common-authorization.jwt-secret:}' != '' or '${common-authorization.jwt-public-key:}' != ''")
  public JwtAuthenticationFilter jwtAuthenticationFilter(TokenValidator tokenValidator,
                                                         CommonAuthorizationProperties properties,
                                                         PathExclusionMatcher pathExclusionMatcher) {
    return new JwtAuthenticationFilter(tokenValidator, properties, pathExclusionMatcher);
  }

  /**
   * Creates the authorization header interceptor bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public AuthorizationHeaderInterceptor authorizationHeaderInterceptor(CommonAuthorizationProperties properties) {
    return new AuthorizationHeaderInterceptor(properties);
  }

  /**
   * Provides a RestClient.Builder customizer that adds the authorization header interceptor.
   * Services can use this builder to create RestClient instances with automatic header propagation.
   */
  @Bean
  @ConditionalOnMissingBean(name = "authorizationRestClientBuilder")
  @ConditionalOnClass(RestClient.class)
  public RestClient.Builder authorizationRestClientBuilder(AuthorizationHeaderInterceptor interceptor) {
    return RestClient.builder()
        .requestInterceptor(interceptor);
  }

  /**
   * Creates the security filter chain with JWT authentication.
   * Can be overridden by defining a custom SecurityFilterChain bean.
   * Only created if jwt-secret or jwt-public-key is configured.
   */
  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  @ConditionalOnExpression("'${common-authorization.jwt-secret:}' != '' or '${common-authorization.jwt-public-key:}' != ''")
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 JwtAuthenticationFilter jwtFilter,
                                                 CommonAuthorizationProperties properties) throws Exception {
    http
        // Disable CSRF for stateless API
        .csrf(AbstractHttpConfigurer::disable)
        // Disable form login
        .formLogin(AbstractHttpConfigurer::disable)
        // Disable HTTP basic auth
        .httpBasic(AbstractHttpConfigurer::disable)
        // Disable logout
        .logout(AbstractHttpConfigurer::disable)
        // Stateless session management
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Configure authorization rules
        .authorizeHttpRequests(auth -> {
          // Permit all excluded paths
          if (properties.getAuthorizationExcludedPathPatterns() != null) {
            for (CommonAuthorizationProperties.ExcludedPathPattern pattern :
                properties.getAuthorizationExcludedPathPatterns()) {
              if (pattern.getPathPattern() != null) {
                auth.requestMatchers(pattern.getPathPattern()).permitAll();
              }
            }
          }
          // All other requests: depends on on-demand setting
//                    if (properties.isOnDemandAuthorization()) {
//                        auth.anyRequest().permitAll();
//                    } else {
          auth.anyRequest().authenticated();
//                    }
        })
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}