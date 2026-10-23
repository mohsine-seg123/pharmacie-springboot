package com.pharmacie.pharmacie.controller.api;

import com.pharmacie.pharmacie.entity.Fournisseur;
import com.pharmacie.pharmacie.service.FournisseurService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurRestController {
    private final FournisseurService service;
    public FournisseurRestController(FournisseurService service) { this.service = service; }
    @GetMapping public List<Fournisseur> list() { return service.findAll(); }
    @GetMapping("/{id}") public Fournisseur get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<Fournisseur> create(@Valid @RequestBody Fournisseur input) {
        Fournisseur saved = service.create(input);
        return ResponseEntity.created(URI.create("/api/fournisseurs/" + saved.getId())).body(saved);
    }
    @PutMapping("/{id}") public Fournisseur update(@PathVariable Long id, @Valid @RequestBody Fournisseur input) { return service.update(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
