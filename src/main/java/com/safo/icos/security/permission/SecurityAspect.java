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
        
        if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal)) {
            throw new SecurityException("No authenticated user found");
        }
        
        IcosUserPrincipal userPrincipal = (IcosUserPrincipal) authentication.getPrincipal();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Resource resourceAnno = signature.getMethod().getAnnotation(Resource.class);

        Object finalResource = null;

        if (resourceAnno != null) {
            UUID id = findIdArgument(joinPoint, signature, resourceAnno.idSource());

            Repositories repositories = new Repositories(applicationContext);
            Object oldEntity = repositories.getRepositoryFor(resourceAnno.type())
                    .flatMap(repo -> ((JpaRepository<Object, UUID>) repo).findById(id))
                    .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + id));

            Object dto = findDtoArgument(joinPoint);

            finalResource = new CompositeResource<>(oldEntity, dto, id);
        } else {
            finalResource = findDtoArgument(joinPoint);
        }

        permissionsService.assertCan(userPrincipal, requirePermission.value(), finalResource);

        return joinPoint.proceed();
    }

    private UUID findIdArgument(ProceedingJoinPoint joinPoint, MethodSignature signature, String idSourceName) {
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(idSourceName) && args[i] instanceof UUID) {
                return (UUID) args[i];
            }
        }

        return Arrays.stream(args)
                .filter(arg -> arg instanceof UUID)
                .map(arg -> (UUID) arg)
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