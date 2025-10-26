package com.fstt.atelier3.ecommercejsf.controller;

import com.fstt.atelier3.ecommercejsf.model.Produit;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("vitrineBean")
@RequestScoped
public class VitrineBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    private List<Produit> produits = new ArrayList<>();
    private Produit selectedProduit;

    @PostConstruct
    public void loadProduits() {
        produits = em.createQuery("select p from Produit p", Produit.class)
                .getResultList();
    }

    public String viewProduit(int id) {
        selectedProduit = em.find(Produit.class, (long) id);
        return null; // laisser la page gérer la navigation/affichage du détail
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public Produit getSelectedProduit() {
        return selectedProduit;
    }

    public void setSelectedProduit(Produit selectedProduit) {
        this.selectedProduit = selectedProduit;
    }
}
