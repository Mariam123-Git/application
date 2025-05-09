package application.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Evenement {
	private int id;
    private int idCategorie;
    private int idOrganisateur;
    private String titre;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String ville;
    private String adresse;
    private String description;
    private int nbPlacesMax;
    private byte[] image;
    private LocalDateTime dateCreation;
    
    // Champs additionnels non présents dans la table mais utiles pour l'affichage
    private String categorieNom;
    private String statutInscription;
    
    // Constructeurs
    public Evenement() {}
    
    public Evenement(int id, String titre, LocalDateTime dateDebut, LocalDateTime dateFin,
                    String ville, String description, String categorieNom) {
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.ville = ville;
        this.description = description;
        this.categorieNom = categorieNom;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdCategorie() {
        return idCategorie;
    }
    
    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }
    
    public int getIdOrganisateur() {
        return idOrganisateur;
    }
    
    public void setIdOrganisateur(int idOrganisateur) {
        this.idOrganisateur = idOrganisateur;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getNbPlacesMax() {
        return nbPlacesMax;
    }
    
    public void setNbPlacesMax(int nbPlacesMax) {
        this.nbPlacesMax = nbPlacesMax;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public String getCategorieNom() {
        return categorieNom;
    }
    
    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }
    
    public String getStatutInscription() {
        return statutInscription;
    }
    
    public void setStatutInscription(String statutInscription) {
        this.statutInscription = statutInscription;
    }
    
    // Méthodes utilitaires pour l'affichage
    public String getJour() {
        return String.valueOf(dateDebut.getDayOfMonth());
    }
    
    public String getMois() {
        // Retourne l'abréviation du mois en français
        String[] moisFr = {"JAN", "FÉV", "MAR", "AVR", "MAI", "JUIN", "JUIL", "AOÛ", "SEP", "OCT", "NOV", "DÉC"};
        return moisFr[dateDebut.getMonthValue() - 1];
    }
    
    public String getHeureFormatted() {
        return String.format("%02d:%02d", dateDebut.getHour(), dateDebut.getMinute());
    }
    
    public String getVilleAndDateFormatted() {
        return String.format("%s • %d %s • %s", 
                ville, 
                dateDebut.getDayOfMonth(), 
                getMois(), 
                getHeureFormatted());
    }}