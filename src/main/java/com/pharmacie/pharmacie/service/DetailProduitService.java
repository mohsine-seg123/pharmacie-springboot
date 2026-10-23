package com.pharmacie.pharmacie.service;

import com.pharmacie.pharmacie.entity.DetailProduit;
import com.pharmacie.pharmacie.repository.*;
import com.pharmacie.pharmacie.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DetailProduitService {
    private final DetailProduitRepository repository;
    private final ProduitRepository produits;
    public DetailProduitService(DetailProduitRepository repository, ProduitRepository produits) {
        this.repository = repository; this.produits = produits;
    }
    public List<DetailProduit> findAll() { return repository.findAll(); }
    public DetailProduit findById(Long id) {
        if (id == null) throw new IllegalArgumentException("Identifiant DetailProduit obligatoire");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DetailProduit", id));
    }
    @Transactional
    public DetailProduit create(DetailProduit input) {
        DetailProduit target = new DetailProduit(); copy(input, target); return repository.save(target);
    }
    @Transactional
    public DetailProduit update(Long id, DetailProduit input) {
        DetailProduit target = findById(id); copy(input, target); return repository.save(target);
    }
    @Transactional
    public void delete(Long id) {
        findById(id);
        if (produits.existsByDetailProduitId(id))
            throw new ResourceConflictException("DetailProduit utilisé(e) par un produit : suppression impossible");
        repository.deleteById(id);
    }
    private void copy(DetailProduit input, DetailProduit target) {
        target.setFabricant(input.getFabricant());
        target.setPaysOrigine(input.getPaysOrigine());
        target.setComposition(input.getComposition());
        target.setDosage(input.getDosage());
        target.setIndications(input.getIndications());
    }
}
