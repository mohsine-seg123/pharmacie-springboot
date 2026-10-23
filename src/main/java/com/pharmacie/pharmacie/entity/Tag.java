package com.pharmacie.pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tag")
@Getter @Setter
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    @Column(length = 255)
    private String libelle;
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private java.util.Set<Produit> produits = new java.util.LinkedHashSet<>();
}
