package com.fstt.atelier3.ecommercejsf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "paniers")
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internaute_id", nullable = false, unique = true)
    private Internaute internaute;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LignePanier> lignes;

    public Panier() {
    }

    public Panier(Internaute internaute) {
        this.internaute = internaute;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Internaute getInternaute() {
        return internaute;
    }

    public void setInternaute(Internaute internaute) {
        this.internaute = internaute;
        if (internaute != null && internaute.getPanier() != this) {
            internaute.setPanier(this);
        }
    }

    public List<LignePanier> getLignes() {
        return lignes;
    }

    public void setLignes(List<LignePanier> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LignePanier ligne) {
        if (ligne == null) return;
        lignes.add(ligne);
        ligne.setPanier(this);
    }

    public void removeLigne(LignePanier ligne) {
        if (ligne == null) return;
        lignes.remove(ligne);
        ligne.setPanier(null);
    }

    public BigDecimal getTotal() {
        return lignes.stream()
                .map(LignePanier::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Panier panier)) return false;
        return id != null && id.equals(panier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
