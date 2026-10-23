package com.pharmacie.pharmacie.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice(basePackages = "com.pharmacie.pharmacie.controller.web")
public class WebExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ModelAndView missing(ResourceNotFoundException e) { return page(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler({ResourceConflictException.class, DataIntegrityViolationException.class})
    ModelAndView conflict(Exception e) { return page(HttpStatus.CONFLICT, "Cette donnée est encore utilisée par un produit."); }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ModelAndView invalid(Exception e) { return page(HttpStatus.BAD_REQUEST, "Paramètres invalides."); }
    private ModelAndView page(HttpStatus status, String message) {
        ModelAndView result = new ModelAndView("error"); result.setStatus(status); result.addObject("message", message); return result;
    }
}
