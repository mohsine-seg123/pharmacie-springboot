package com.pharmacie.pharmacie.service;

import com.pharmacie.pharmacie.entity.Fournisseur;
import com.pharmacie.pharmacie.repository.*;
import com.pharmacie.pharmacie.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FournisseurService {
    private final FournisseurRepository repository;
    private final ProduitRepository produits;
    public FournisseurService(FournisseurRepository repository, ProduitRepository produits) {
        this.repository = repository; this.produits = produits;
    }
    public List<Fournisseur> findAll() { return repository.findAll(); }
    public Fournisseur findById(Long id) {
        if (id == null) throw new IllegalArgumentException("Identifiant Fournisseur obligatoire");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
    }
    @Transactional
    public Fournisseur create(Fournisseur input) {
        Fournisseur target = new Fournisseur(); copy(input, target); return repository.save(target);
    }
    @Transactional
    public Fournisseur update(Long id, Fournisseur input) {
        Fournisseur target = findById(id); copy(input, target); return repository.save(target);
    }
    @Transactional
    public void delete(Long id) {
        findById(id);
        if (produits.existsByFournisseurId(id))
            throw new ResourceConflictException("Fournisseur utilisé(e) par un produit : suppression impossible");
        repository.deleteById(id);
    }
    private void copy(Fournisseur input, Fournisseur target) {
        target.setNom(input.getNom());
        target.setContact(input.getContact());
        target.setAdresse(input.getAdresse());
    }
}
