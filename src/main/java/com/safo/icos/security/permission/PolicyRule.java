package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import org.springframework.core.Ordered;

public interface PolicyRule extends Ordered {
  boolean supports(String action);

  PolicyResult check(IcosUserPrincipal user, String action, Object resource);

  @Override
  default int getOrder() {
    return 100;
  }
}
