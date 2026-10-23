package com.pharmacie.pharmacie.controller.api;

import com.pharmacie.pharmacie.entity.DetailProduit;
import com.pharmacie.pharmacie.service.DetailProduitService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/details")
public class DetailProduitRestController {
    private final DetailProduitService service;
    public DetailProduitRestController(DetailProduitService service) { this.service = service; }
    @GetMapping public List<DetailProduit> list() { return service.findAll(); }
    @GetMapping("/{id}") public DetailProduit get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<DetailProduit> create(@Valid @RequestBody DetailProduit input) {
        DetailProduit saved = service.create(input);
        return ResponseEntity.created(URI.create("/api/details/" + saved.getId())).body(saved);
    }
    @PutMapping("/{id}") public DetailProduit update(@PathVariable Long id, @Valid @RequestBody DetailProduit input) { return service.update(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
