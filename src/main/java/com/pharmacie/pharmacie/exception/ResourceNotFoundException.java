package com.pharmacie.pharmacie.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String ressource, Long id) { super(ressource + " introuvable : " + id); }
}
