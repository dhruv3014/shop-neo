package com.shopneo.common.authorization.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration properties for common authorization.
 * Maps to the "common-authorization" prefix in common-authorization-application.yml.
 */
@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@ConfigurationProperties(prefix = "common-authorization")
public class CommonAuthorizationProperties {

  /**
   * Request header that should be used for getting principal.
   * Default: "Authorization"
   */
  private String principalHeader = "Authorization";

  /**
   * Specifies if authorization is on-demand (only if request contains principal header),
   * or mandatory (request must contain principal header).
   * Default: true (on-demand)
   */
  private boolean onDemandAuthorization = true;

  /**
   * URL of the authorization service for centralized authorization checks.
   */
  private String authorizationServiceUrl;

  /**
   * URL path patterns for which authorization should be skipped.
   * pathPattern: Ant-style pattern
   * methods: empty means all HTTP methods
   */
  private List<ExcludedPathPattern> authorizationExcludedPathPatterns = Collections.emptyList();

  /**
   * URL hosts for which REST client Authorization header should be set (Ant-style patterns).
   */
  private List<String> authorizationHeaderPropagationIncludedHostPatterns = Collections.emptyList();

  /**
   * Whether to allow customer tokens (vs only internal/service tokens).
   * Default: false
   */
  private boolean allowCustomerToken = false;

  /**
   * JWT secret for HMAC signature validation.
   * Used when asymmetric keys are not configured.
   */
  private String jwtSecret;

  /**
   * JWT public key (PEM format) for RSA/EC signature validation.
   * Recommended for multi-service environments.
   */
  private String jwtPublicKey;

  /**
   * Prefix to add to roles when creating Spring Security authorities.
   * Default: "ROLE_"
   */
  private String rolePrefix = "ROLE_";

  /**
   * Excluded path pattern configuration.
   */
  @Data
  @NoArgsConstructor
  @Builder(toBuilder = true)
  @AllArgsConstructor
  public static class ExcludedPathPattern {

    private static final Set<String> DEFAULT_METHODS =
        Arrays.stream(HttpMethod.values())
            .map(HttpMethod::name)
            .collect(Collectors.toSet());

    /**
     * Ant-style path pattern to exclude from authorization.
     */
    private String pathPattern;

    /**
     * HTTP methods to exclude. Empty means all methods.
     */
    @Builder.Default
    private Set<String> methods = Collections.emptySet();

    /**
     * Returns the methods to check against.
     * If methods is empty, returns all HTTP methods.
     */
    public Set<String> getMethods() {
      return methods == null || methods.isEmpty() ? DEFAULT_METHODS : methods;
    }
  }
}