package com.pharmacie.pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorie")
@Getter @Setter
public class Categorie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    @Column(length = 255)
    private String nom;
    @Size(max = 2000)
    @Column(length = 2000)
    private String description;
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "categorie")
    private java.util.List<Produit> produits = new java.util.ArrayList<>();
}
