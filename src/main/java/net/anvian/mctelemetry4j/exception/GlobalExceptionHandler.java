package net.anvian.mctelemetry4j.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError() {
        return ResponseEntity.internalServerError().body("Internal server error");
    }

    @ExceptionHandler(ExportExeption.class)
    public ResponseEntity<String> handleExportError() {
        return ResponseEntity.internalServerError().body("Export error");
    }
}
