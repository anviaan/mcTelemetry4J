package net.anvian.mctelemetry4j.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ModAlreadyExistsException.class)
    public ResponseEntity<String> handleModAlreadyExists() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Mod already exists");
    }

    @ExceptionHandler(ModNotFoundException.class)
    public ResponseEntity<String> handleModNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mod not found");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation() {
        return ResponseEntity.badRequest().body("Invalid request parameter");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleMissingResource(HttpServletRequest request) {
        if ("DELETE".equalsIgnoreCase(request.getMethod())
                && ("/data".equals(request.getRequestURI()) || "/telemetry/data".equals(request.getRequestURI()))) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ExportExeption.class)
    public ResponseEntity<String> handleExportError() {
        return ResponseEntity.internalServerError().body("Export error");
    }
}
