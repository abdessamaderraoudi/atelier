package com.fstt.atelier3.ecommercejsf.controller;

import com.fstt.atelier3.ecommercejsf.model.Internaute;
import com.fstt.atelier3.ecommercejsf.model.LignePanier;
import com.fstt.atelier3.ecommercejsf.model.Panier;
import com.fstt.atelier3.ecommercejsf.model.Produit;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Named("panierBean")
@SessionScoped
public class PanierBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AuthBean authBean;

    private Panier panier;

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    @Transactional
    public void loadPanier() {
        Internaute user = authBean != null ? authBean.getCurrentUser() : null;
        if (user == null) {
            this.panier = null;
            return;
        }
        try {
            this.panier = em.createQuery(
                            "select p from Panier p where p.internaute = :user",
                            Panier.class)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            // ... (votre code pour créer un nouveau panier)
            Panier p = new Panier();
            p.setInternaute(user);
            em.persist(p);
            this.panier = p;
        }
    }

    @Transactional
    public String ajouterAuPanier(Produit produit) {
        if (produit == null) return null;
        if (panier == null) loadPanier();
        if (panier == null) return null;

        Optional<LignePanier> ligneExistante = panier.getLignes().stream()
                .filter(lp -> lp.getProduit() != null && produit.getId() != null && produit.getId().equals(lp.getProduit().getId()))
                .findFirst();
        if (ligneExistante.isPresent()) {
            LignePanier lp = ligneExistante.get();
            lp.setQuantite(lp.getQuantite() + 1);
        } else {
            LignePanier lp = new LignePanier(produit, 1);
            panier.addLigne(lp);
        }
        em.merge(panier);
        return null;
    }

    @Transactional
    public String supprimerDuPanier(LignePanier ligne) {
        if (panier == null || ligne == null) return null;
        panier.removeLigne(ligne);
        em.merge(panier);
        return null;
    }

    @Transactional
    public String modifierQuantite(LignePanier ligne) {
        if (panier == null || ligne == null) return null;
        if (ligne.getQuantite() == null || ligne.getQuantite() <= 0) {
            panier.removeLigne(ligne);
        } else {
            // Rien d’autre, la ligne est déjà mise à jour par le binding UI
        }
        em.merge(panier);
        return null;
    }
}
