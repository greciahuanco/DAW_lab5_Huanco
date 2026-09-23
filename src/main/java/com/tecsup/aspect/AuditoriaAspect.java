package com.tecsup.aspect;

import com.tecsup.model.Producto;
import com.tecsup.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class AuditoriaAspect {

    @Autowired
    private AuditoriaService auditoriaService;

    @AfterReturning("execution(* com.tecsup.service.ProductoService.guardar(..))")
    public void auditarGuardar(JoinPoint joinPoint) {

        auditoriaService.registrar(
                "CREAR",
                joinPoint.getSignature().getName(),
                "Se registró un producto"
        );
    }

    @AfterReturning("execution(* com.tecsup.service.ProductoService.eliminar(..))")
    public void auditarEliminar(JoinPoint joinPoint) {

        Object[] argumentos = joinPoint.getArgs();
        Long id = (Long) argumentos[0];

        auditoriaService.registrar(
                "ELIMINAR",
                joinPoint.getSignature().getName(),
                "Se eliminó el producto con ID: " + id
        );
    }

    @AfterReturning("execution(* com.tecsup.service.ProductoService.actualizar(..))")
    public void auditarActualizar(JoinPoint joinPoint) {

        Object[] argumentos = joinPoint.getArgs();
        Producto producto = (Producto) argumentos[0];

        auditoriaService.registrar(
                "ACTUALIZAR",
                joinPoint.getSignature().getName(),
                "Se actualizó el producto con ID: " + producto.getId()
        );
    }

    @AfterReturning(
            pointcut = "execution(* com.tecsup.service.ProductoService.listar(..))",
            returning = "resultado"
    )
    public void auditarListar(JoinPoint joinPoint, Object resultado) {

        List<?> productos = (List<?>) resultado;

        auditoriaService.registrar(
                "LISTAR",
                joinPoint.getSignature().getName(),
                "Se listaron " + productos.size() + " productos"
        );
    }
}