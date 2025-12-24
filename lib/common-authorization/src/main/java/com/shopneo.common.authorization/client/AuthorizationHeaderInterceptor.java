package com.shopneo.common.authorization.client;

import com.shopneo.common.authorization.config.CommonAuthorizationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Interceptor for propagating the Authorization header to outbound REST requests.
 * Only propagates to hosts matching the configured patterns.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthorizationHeaderInterceptor implements ClientHttpRequestInterceptor {

  private final CommonAuthorizationProperties properties;

  @Override
  public ClientHttpResponse intercept(HttpRequest request,
                                      byte[] body,
                                      ClientHttpRequestExecution execution) throws IOException {

    String host = request.getURI().getHost();

    if (shouldPropagate(host)) {
      String inboundAuthHeader = getCurrentRequestAuthorizationHeader();

      if (inboundAuthHeader != null && !inboundAuthHeader.isBlank()) {
        // Only set if not already present
        if (!request.getHeaders().containsHeader(properties.getPrincipalHeader())) {
          request.getHeaders().set(properties.getPrincipalHeader(), inboundAuthHeader);
          log.debug("Propagated {} header to host: {}", properties.getPrincipalHeader(), host);
        }
      }
    }

    return execution.execute(request, body);
  }

  /**
   * Checks if the authorization header should be propagated to the given host.
   *
   * @param host the target host
   * @return true if the host matches any of the configured patterns
   */
  private boolean shouldPropagate(String host) {
    if (host == null) {
      return false;
    }

    List<String> patterns = properties.getAuthorizationHeaderPropagationIncludedHostPatterns();

    if (patterns == null || patterns.isEmpty()) {
      return false;
    }

    for (String pattern : patterns) {
      if (PatternMatchUtils.simpleMatch(pattern, host)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Retrieves the Authorization header from the current inbound request.
   *
   * @return the authorization header value, or null if not available
   */
  private String getCurrentRequestAuthorizationHeader() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      log.debug("No request context available for header propagation");
      return null;
    }

    return attributes.getRequest().getHeader(properties.getPrincipalHeader());
  }
}