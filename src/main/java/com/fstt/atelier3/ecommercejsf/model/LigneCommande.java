package com.fstt.atelier3.ecommercejsf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "lignes_commande")
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    // Données figées du produit au moment de l’achat
    @Column(name = "produit_id")
    private Long produitId;

    @Column(name = "produit_nom", length = 150)
    private String produitNom;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantite = 1;

    public LigneCommande() {
    }

    public LigneCommande(Long produitId, String produitNom, BigDecimal prixUnitaire, Integer quantite) {
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
    }

    public static LigneCommande fromLignePanier(LignePanier lp) {
        if (lp == null || lp.getProduit() == null) return null;
        var p = lp.getProduit();
        return new LigneCommande(p.getId(), p.getNom(), p.getPrix(), lp.getQuantite());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getProduitNom() {
        return produitNom;
    }

    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getSousTotal() {
        if (prixUnitaire == null || quantite == null) return BigDecimal.ZERO;
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LigneCommande that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
