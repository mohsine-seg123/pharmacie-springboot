package com.pharmacie.pharmacie.service;

import com.pharmacie.pharmacie.entity.Categorie;
import com.pharmacie.pharmacie.repository.*;
import com.pharmacie.pharmacie.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategorieService {
    private final CategorieRepository repository;
    private final ProduitRepository produits;
    public CategorieService(CategorieRepository repository, ProduitRepository produits) {
        this.repository = repository; this.produits = produits;
    }
    public List<Categorie> findAll() { return repository.findAll(); }
    public Categorie findById(Long id) {
        if (id == null) throw new IllegalArgumentException("Identifiant Categorie obligatoire");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categorie", id));
    }
    @Transactional
    public Categorie create(Categorie input) {
        Categorie target = new Categorie(); copy(input, target); return repository.save(target);
    }
    @Transactional
    public Categorie update(Long id, Categorie input) {
        Categorie target = findById(id); copy(input, target); return repository.save(target);
    }
    @Transactional
    public void delete(Long id) {
        findById(id);
        if (produits.existsByCategorieId(id))
            throw new ResourceConflictException("Categorie utilisé(e) par un produit : suppression impossible");
        repository.deleteById(id);
    }
    private void copy(Categorie input, Categorie target) {
        target.setNom(input.getNom());
        target.setDescription(input.getDescription());
    }
}
