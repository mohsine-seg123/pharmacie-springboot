package com.pharmacie.pharmacie.service;

import com.pharmacie.pharmacie.entity.Tag;
import com.pharmacie.pharmacie.repository.*;
import com.pharmacie.pharmacie.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository repository;
    private final ProduitRepository produits;
    public TagService(TagRepository repository, ProduitRepository produits) {
        this.repository = repository; this.produits = produits;
    }
    public List<Tag> findAll() { return repository.findAll(); }
    public Tag findById(Long id) {
        if (id == null) throw new IllegalArgumentException("Identifiant Tag obligatoire");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }
    @Transactional
    public Tag create(Tag input) {
        Tag target = new Tag(); copy(input, target); return repository.save(target);
    }
    @Transactional
    public Tag update(Long id, Tag input) {
        Tag target = findById(id); copy(input, target); return repository.save(target);
    }
    @Transactional
    public void delete(Long id) {
        findById(id);
        if (produits.existsByTagsId(id))
            throw new ResourceConflictException("Tag utilisé(e) par un produit : suppression impossible");
        repository.deleteById(id);
    }
    private void copy(Tag input, Tag target) {
        target.setLibelle(input.getLibelle());
    }
}
