package com.pharmacie.pharmacie.repository;

import com.pharmacie.pharmacie.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    boolean existsByCategorieId(Long id);
    boolean existsByFournisseurId(Long id);
    boolean existsByTagsId(Long id);
    boolean existsByDetailProduitId(Long id);
}
