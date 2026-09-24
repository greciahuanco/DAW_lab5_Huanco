package com.tecsup.aspect;

import com.tecsup.exception.ForbiddenException;
import com.tecsup.exception.UnauthorizedException;
import com.tecsup.model.Producto;
import com.tecsup.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Aspect
@Component
public class AuditoriaAspect {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private HttpServletRequest request;

    // Usuarios y roles
    private final Map<String, String> usuarios = Map.of(
            "Grecia Huanco", "ADMIN",
            "Ana", "USER",
            "Luis", "USER"
    );

    // =========================
    // VALIDACIÓN
    // =========================

    private void validarUsuario() {

        String usuario = request.getHeader("Usuario");
        String rolHeader = request.getHeader("Rol");

        if (usuario == null || rolHeader == null) {
            throw new UnauthorizedException(
                    "Debe enviar Usuario y Rol"
            );
        }

        String rolReal = usuarios.get(usuario);

        if (rolReal == null || !rolReal.equals(rolHeader)) {
            throw new ForbiddenException(
                    "No tiene permisos"
            );
        }
    }

    private void validarRol(String... rolesPermitidos) {

        validarUsuario();

        String rol = request.getHeader("Rol");

        for (String permitido : rolesPermitidos) {
            if (permitido.equals(rol)) {
                return;
            }
        }

        throw new ForbiddenException(
                "Acceso denegado"
        );
    }

    // =========================
    // USUARIO
    // =========================

    private String obtenerUsuario() {

        String usuario = request.getHeader("Usuario");

        if (usuario == null) {
            return "ANONIMO";
        }

        String rol = usuarios.get(usuario);

        return (rol != null)
                ? usuario + " (" + rol + ")"
                : "DESCONOCIDO";
    }

    // =========================
    // CONTROL DE ACCESO
    // =========================

    @Before("execution(* com.tecsup.service.ProductoService.guardar(..))")
    public void validarCrear() {
        validarRol("ADMIN", "USER");
    }

    @Before("execution(* com.tecsup.service.ProductoService.eliminar(..))")
    public void validarEliminar() {
        validarRol("ADMIN");
    }

    @Before("execution(* com.tecsup.service.ProductoService.actualizar(..))")
    public void validarActualizar() {
        validarRol("ADMIN");
    }

    @Before("execution(* com.tecsup.service.ProductoService.listar(..))")
    public void validarListar() {
        validarRol("ADMIN", "USER");
    }

    // =========================
    // AUDITORÍA
    // =========================

    @AfterReturning("execution(* com.tecsup.service.ProductoService.guardar(..))")
    public void auditarGuardar(JoinPoint joinPoint) {

        auditoriaService.registrar(
                "CREAR",
                joinPoint.getSignature().getName(),
                "Se registró un producto",
                obtenerUsuario()
        );
    }

    @AfterReturning("execution(* com.tecsup.service.ProductoService.eliminar(..))")
    public void auditarEliminar(JoinPoint joinPoint) {

        Object[] argumentos = joinPoint.getArgs();
        Long id = (Long) argumentos[0];

        auditoriaService.registrar(
                "ELIMINAR",
                joinPoint.getSignature().getName(),
                "Se eliminó el producto con ID: " + id,
                obtenerUsuario()
        );
    }

    @AfterReturning("execution(* com.tecsup.service.ProductoService.actualizar(..))")
    public void auditarActualizar(JoinPoint joinPoint) {

        Object[] argumentos = joinPoint.getArgs();
        Producto producto = (Producto) argumentos[0];

        auditoriaService.registrar(
                "ACTUALIZAR",
                joinPoint.getSignature().getName(),
                "Se actualizó el producto con ID: " + producto.getId(),
                obtenerUsuario()
        );
    }

    @AfterReturning(
            pointcut = "execution(* com.tecsup.service.ProductoService.listar(..))",
            returning = "resultado"
    )
    public void auditarListar(
            JoinPoint joinPoint,
            Object resultado) {

        List<?> productos = (List<?>) resultado;

        auditoriaService.registrar(
                "LISTAR",
                joinPoint.getSignature().getName(),
                "Se listaron " + productos.size() + " productos",
                obtenerUsuario()
        );
    }
}