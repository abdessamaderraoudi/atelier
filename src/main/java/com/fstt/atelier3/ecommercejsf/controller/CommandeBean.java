package com.fstt.atelier3.ecommercejsf.controller;

import com.fstt.atelier3.ecommercejsf.model.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("commandeBean")
@RequestScoped
public class CommandeBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AuthBean authBean;

    @Inject
    private PanierBean panierBean;

    private String adresseLivraison;
    private List<Commande> historiqueCommandes = new ArrayList<>();

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public List<Commande> getHistoriqueCommandes() {
        return historiqueCommandes;
    }

    public void setHistoriqueCommandes(List<Commande> historiqueCommandes) {
        this.historiqueCommandes = historiqueCommandes;
    }

    @Transactional
    public String validerCommande() {
        Internaute user = authBean != null ? authBean.getCurrentUser() : null;
        if (user == null) return null;

        if (panierBean.getPanier() == null) {
            panierBean.loadPanier();
        }
        Panier panier = panierBean.getPanier();
        if (panier == null || panier.getLignes().isEmpty()) {
            return null;
        }

        Commande commande = new Commande(user);
        commande.setAdresseLivraison(adresseLivraison);

        // Copier les lignes du panier
        for (LignePanier lp : panier.getLignes()) {
            LigneCommande lc = LigneCommande.fromLignePanier(lp);
            if (lc != null) {
                commande.addLigne(lc);
            }
        }

        em.persist(commande);

        // Vider le panier
        panier.getLignes().clear();
        em.merge(panier);

        // Rafraîchir l’historique si nécessaire
        loadHistorique();
        return "/vues/confirmation.xhtml?faces-redirect=true";
    }

    public void loadHistorique() {
        Internaute user = authBean != null ? authBean.getCurrentUser() : null;
        if (user == null) {
            historiqueCommandes = new ArrayList<>();
            return;
        }
        historiqueCommandes = em.createQuery(
                        "select distinct c from Commande c left join fetch c.lignes where c.internaute = :user order by c.dateCommande desc",
                        Commande.class)
                .setParameter("user", user)
                .getResultList();
    }
}
