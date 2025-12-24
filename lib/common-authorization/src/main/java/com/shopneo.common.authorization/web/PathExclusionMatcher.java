package com.shopneo.common.authorization.web;

import com.shopneo.common.authorization.config.CommonAuthorizationProperties;
import com.shopneo.common.authorization.config.CommonAuthorizationProperties.ExcludedPathPattern;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Helper class to determine if a request path should be excluded from authorization
 * based on configured path patterns and HTTP methods.
 */
@RequiredArgsConstructor
public class PathExclusionMatcher {

  private final List<ExcludedPathPattern> excludedPathPatterns;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  /**
   * Creates a PathExclusionMatcher from CommonAuthorizationProperties.
   *
   * @param properties the authorization properties
   */
  public PathExclusionMatcher(CommonAuthorizationProperties properties) {
    this.excludedPathPatterns = properties.getAuthorizationExcludedPathPatterns();
  }

  /**
   * Checks if the given request should be excluded from authorization.
   *
   * @param request the HTTP servlet request
   * @return true if the request path and method match an exclusion pattern
   */
  public boolean isExcluded(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();

    if (excludedPathPatterns == null || excludedPathPatterns.isEmpty()) {
      return false;
    }

    for (ExcludedPathPattern pattern : excludedPathPatterns) {
      if (matchesPattern(path, method, pattern)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if the path and method match the given exclusion pattern.
   *
   * @param path    the request path
   * @param method  the HTTP method
   * @param pattern the exclusion pattern to check against
   * @return true if both path and method match
   */
  private boolean matchesPattern(String path, String method, ExcludedPathPattern pattern) {
    if (pattern.getPathPattern() == null) {
      return false;
    }

    boolean pathMatches = antPathMatcher.match(pattern.getPathPattern(), path);
    boolean methodMatches = pattern.getMethods().contains(method);

    return pathMatches && methodMatches;
  }
}