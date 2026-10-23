package com.pharmacie.pharmacie.controller.api;

import com.pharmacie.pharmacie.entity.Categorie;
import com.pharmacie.pharmacie.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieRestController {
    private final CategorieService service;
    public CategorieRestController(CategorieService service) { this.service = service; }
    @GetMapping public List<Categorie> list() { return service.findAll(); }
    @GetMapping("/{id}") public Categorie get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<Categorie> create(@Valid @RequestBody Categorie input) {
        Categorie saved = service.create(input);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }
    @PutMapping("/{id}") public Categorie update(@PathVariable Long id, @Valid @RequestBody Categorie input) { return service.update(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
