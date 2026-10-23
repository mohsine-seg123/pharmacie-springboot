package com.pharmacie.pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detail_produit")
@Getter @Setter
public class DetailProduit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 255)
    @Column(length = 255)
    private String fabricant;
    @Size(max = 255)
    @Column(length = 255)
    private String paysOrigine;
    @Size(max = 2000)
    @Column(length = 2000)
    private String composition;
    @Size(max = 255)
    @Column(length = 255)
    private String dosage;
    @Size(max = 2000)
    @Column(length = 2000)
    private String indications;
}
