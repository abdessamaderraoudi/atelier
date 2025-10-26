package com.fstt.atelier3.ecommercejsf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande = LocalDateTime.now();

    @Column(nullable = false, length = 50)
    private String statut = "En cours";

    @Column(name = "adresse_livraison", length = 255)
    private String adresseLivraison;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "internaute_id", nullable = false)
    private Internaute internaute;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande() {
    }

    public Commande(Internaute internaute) {
        this.internaute = internaute;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public Internaute getInternaute() {
        return internaute;
    }

    public void setInternaute(Internaute internaute) {
        this.internaute = internaute;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneCommande ligne) {
        if (ligne == null) return;
        lignes.add(ligne);
        ligne.setCommande(this);
    }

    public void removeLigne(LigneCommande ligne) {
        if (ligne == null) return;
        lignes.remove(ligne);
        ligne.setCommande(null);
    }

    public BigDecimal getTotal() {
        return lignes.stream()
                .map(LigneCommande::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande commande)) return false;
        return id != null && id.equals(commande.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
