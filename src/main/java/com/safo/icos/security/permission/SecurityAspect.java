package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that intercepts methods annotated with {@link RequirePermission}
 * and enforces access control via {@link PermissionsService}.
 * <p>
 * If the method is also annotated with {@link Resource}, the corresponding
 * entity is loaded from the database and passed as the "old state"
 * in a {@link CompositeResource} for before/after policy evaluation.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

  private final ApplicationContext applicationContext;
  private final PermissionsService permissionsService;

  /**
   * Around advice for {@link RequirePermission}-annotated methods.
   * Extracts the authenticated user, resolves the target resource,
   * delegates to {@link PermissionsService#assertCan}, and proceeds
   * if access is granted.
   *
   * @param joinPoint         the intercepted method invocation
   * @param requirePermission the annotation instance
   * @return the result of the intercepted method
   * @throws Throwable if the method throws, or if access is denied
   */
  @Around("@annotation(requirePermission)")
  public Object handlePermission(ProceedingJoinPoint joinPoint,
                                 RequirePermission requirePermission) throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
            || !(authentication.getPrincipal() instanceof IcosUserPrincipal userPrincipal)) {
      throw new SecurityException("No authenticated user found");
    }

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Resource resourceAnno = signature.getMethod().getAnnotation(Resource.class);

    final Object finalResource;

    if (resourceAnno != null) {
      UUID id = findIdArgument(joinPoint, signature, resourceAnno.idSource());
      Repositories repositories = new Repositories(applicationContext);
      Object oldEntity = findEntityById(repositories, resourceAnno.type(), id);
      Object dto = findDtoArgument(joinPoint);
      finalResource = new CompositeResource<>(oldEntity, dto, id);
    } else {
      finalResource = findDtoArgument(joinPoint);
    }

    permissionsService.assertCan(userPrincipal, requirePermission.value(), finalResource);
    return joinPoint.proceed();
  }

  @SuppressWarnings("unchecked")
  private Object findEntityById(Repositories repositories, Class<?> entityType, UUID id) {
    return repositories.getRepositoryFor(entityType)
            .map(repo -> {
              JpaRepository<Object, UUID> jpaRepository = (JpaRepository<Object, UUID>) repo;
              return jpaRepository.findById(id).orElse(null);
            })
            .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + id));
  }

  private UUID findIdArgument(ProceedingJoinPoint joinPoint, MethodSignature signature,
                              String idSourceName) {
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < parameterNames.length; i++) {
      if (parameterNames[i].equals(idSourceName) && args[i] instanceof UUID uuid) {
        return uuid;
      }
    }

    return Arrays.stream(args)
            .filter(UUID.class::isInstance)
            .map(UUID.class::cast)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                    "Could not find ID argument: " + idSourceName));
  }

  private Object findDtoArgument(ProceedingJoinPoint joinPoint) {
    return Arrays.stream(joinPoint.getArgs())
            .filter(arg -> arg != null && !(arg instanceof UUID))
            .findFirst()
            .orElse(null);
  }
}
