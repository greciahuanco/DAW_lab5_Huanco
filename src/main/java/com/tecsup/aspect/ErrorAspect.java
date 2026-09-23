package com.tecsup.aspect;

import com.tecsup.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorAspect {

    @Autowired
    private AuditoriaService auditoriaService;

    @AfterThrowing(
            pointcut = "execution(* com.tecsup.service.ProductoService.*(..))",
            throwing = "ex"
    )
    public void capturarError(JoinPoint joinPoint, Exception ex) {

        String metodo = joinPoint.getSignature().getName();

        auditoriaService.registrar(
                "ERROR",
                metodo,
                "Error: " + ex.getMessage()
        );

        System.out.println("ERROR AOP: " + ex.getMessage());
    }
}