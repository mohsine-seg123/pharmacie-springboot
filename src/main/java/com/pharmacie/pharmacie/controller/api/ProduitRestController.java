package com.pharmacie.pharmacie.controller.api;

import com.pharmacie.pharmacie.entity.Produit;
import com.pharmacie.pharmacie.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitRestController {
    private final ProduitService service;
    public ProduitRestController(ProduitService service) { this.service = service; }
    @GetMapping public List<Produit> list() { return service.findAll(); }
    @GetMapping("/{id}") public Produit get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<Produit> create(@Valid @RequestBody Produit input) {
        Produit saved = service.create(input);
        return ResponseEntity.created(URI.create("/api/produits/" + saved.getId())).body(saved);
    }
    @PutMapping("/{id}") public Produit update(@PathVariable Long id, @Valid @RequestBody Produit input) { return service.update(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
