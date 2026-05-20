package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {
    private final ApplicationContext applicationContext;
    private final PermissionsService permissionsService;

    @Around("@annotation(requirePermission)")
    public Object handlePermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal userPrincipal)) {
            throw new SecurityException("No authenticated user found");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Resource resourceAnno = signature.getMethod().getAnnotation(Resource.class);

        Object finalResource = null;

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

    private UUID findIdArgument(ProceedingJoinPoint joinPoint, MethodSignature signature, String idSourceName) {
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
                .orElseThrow(() -> new IllegalArgumentException("Could not find ID argument: " + idSourceName));
    }

    private Object findDtoArgument(ProceedingJoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(arg -> !(arg instanceof UUID))
                .findFirst()
                .orElse(null);
    }
}