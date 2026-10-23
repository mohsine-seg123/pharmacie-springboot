package com.pharmacie.pharmacie.service;

import com.pharmacie.pharmacie.entity.*;
import com.pharmacie.pharmacie.repository.ProduitRepository;
import com.pharmacie.pharmacie.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ProduitService {
    private final ProduitRepository repository;
    private final CategorieService categories;
    private final FournisseurService fournisseurs;
    private final TagService tags;
    public ProduitService(ProduitRepository repository, CategorieService categories, FournisseurService fournisseurs, TagService tags) {
        this.repository = repository; this.categories = categories; this.fournisseurs = fournisseurs; this.tags = tags;
    }
    public List<Produit> findAll() { return repository.findAll(); }
    public Produit findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }
    @Transactional
    public Produit create(Produit input) { return save(new Produit(), input); }
    @Transactional
    public Produit update(Long id, Produit input) { return save(findById(id), input); }
    @Transactional
    public void delete(Long id) { repository.delete(findById(id)); }
    private Produit save(Produit target, Produit input) {
        if (input.getCategorie() == null || input.getFournisseur() == null || input.getDetailProduit() == null)
            throw new IllegalArgumentException("Catégorie, fournisseur et détails obligatoires");
        Categorie categorie = categories.findById(input.getCategorie().getId());
        Fournisseur fournisseur = fournisseurs.findById(input.getFournisseur().getId());
        Set<Tag> selected = new LinkedHashSet<>();
        if (input.getTags() != null) for (Tag tag : input.getTags()) {
            if (tag == null) throw new IllegalArgumentException("Tag invalide");
            selected.add(tags.findById(tag.getId()));
        }
        target.setNom(input.getNom()); target.setDescription(input.getDescription()); target.setPrix(input.getPrix());
        target.setCodeBarre(input.getCodeBarre()); target.setDateExpiration(input.getDateExpiration());
        target.setCategorie(categorie); target.setFournisseur(fournisseur);
        target.getTags().clear(); target.getTags().addAll(selected);
        // Conserver l'identité des détails lors d'un PUT, sans accepter un ID externe.
        DetailProduit detail = target.getDetailProduit();
        if (detail == null) { detail = new DetailProduit(); target.setDetailProduit(detail); }
        DetailProduit source = input.getDetailProduit();
        detail.setFabricant(source.getFabricant()); detail.setPaysOrigine(source.getPaysOrigine());
        detail.setComposition(source.getComposition()); detail.setDosage(source.getDosage()); detail.setIndications(source.getIndications());
        return repository.save(target);
    }
}
