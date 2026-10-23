package com.pharmacie.pharmacie.controller.api;

import com.pharmacie.pharmacie.entity.Tag;
import com.pharmacie.pharmacie.service.TagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {
    private final TagService service;
    public TagRestController(TagService service) { this.service = service; }
    @GetMapping public List<Tag> list() { return service.findAll(); }
    @GetMapping("/{id}") public Tag get(@PathVariable Long id) { return service.findById(id); }
    @PostMapping public ResponseEntity<Tag> create(@Valid @RequestBody Tag input) {
        Tag saved = service.create(input);
        return ResponseEntity.created(URI.create("/api/tags/" + saved.getId())).body(saved);
    }
    @PutMapping("/{id}") public Tag update(@PathVariable Long id, @Valid @RequestBody Tag input) { return service.update(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
