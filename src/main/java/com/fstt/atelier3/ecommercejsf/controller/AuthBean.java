package com.fstt.atelier3.ecommercejsf.controller;

import com.fstt.atelier3.ecommercejsf.model.Internaute;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private Internaute currentUser;

    @PersistenceContext
    private EntityManager em;

    public String login() {
        if (email == null || password == null) {
            return null;
        }
        try {
            Internaute user = em.createQuery(
                            "select i from Internaute i where i.email = :email and i.motDePasse = :pwd",
                            Internaute.class)
                    .setParameter("email", email)
                    .setParameter("pwd", password)
                    .getSingleResult();
            this.currentUser = user;
            // Charger le panier après login sans créer d’injection circulaire
            try {
                CDI.current().select(PanierBean.class).get().loadPanier();
            } catch (Exception ignored) {
            }
            return null; // laisser la navigation de la page décider
        } catch (NoResultException e) {
            this.currentUser = null;
            return null;
        }
    }

    public String logout() {
        this.currentUser = null;
        this.email = null;
        this.password = null;
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null && fc.getExternalContext() != null) {
                fc.getExternalContext().invalidateSession();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Transactional
    public String register() {
        if (email == null || password == null) {
            return null;
        }
        // Vérifier si email déjà utilisé
        Long count = em.createQuery("select count(i) from Internaute i where i.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        if (count != null && count > 0) {
            return null;
        }
        Internaute user = new Internaute(email, password);
        em.persist(user);
        this.currentUser = user;
        try {
            CDI.current().select(PanierBean.class).get().loadPanier();
        } catch (Exception ignored) {
        }
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Internaute getCurrentUser() {
        return currentUser;
    }
}
