package com.pharmacie.pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "produit")
@Getter @Setter
public class Produit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 255)
    @Column(length = 255)
    private String nom;
    @Size(max = 2000)
    @Column(length = 2000)
    private String description;
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    @Column(precision = 12, scale = 2)
    private BigDecimal prix;
    @NotBlank
    @Size(max = 255)
    @Column(length = 255)
    private String codeBarre;
    @NotNull
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate dateExpiration;
    @NotNull @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;
    @NotNull @ManyToOne(optional = false)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;
    @Valid @NotNull @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "detail_id", unique = true, nullable = false)
    private DetailProduit detailProduit = new DetailProduit();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "produit_tag", joinColumns = @JoinColumn(name = "produit_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private java.util.Set<Tag> tags = new java.util.LinkedHashSet<>();
}
