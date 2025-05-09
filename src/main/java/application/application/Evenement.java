package application.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Evenement {
	private int id;
    private int idCategorie;
    private int idOrganisateur;
    private String titre;
<<<<<<< HEAD
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
=======
    private String description;
    private LocalDate dateDebut;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String ville;               // ✅ Ajout de ville
    private String adresse;
    private Integer nbPlacesMax;
    private byte[] image;
    private String categorie;
    private LocalDate dateCreation;

    public Evenement(int id, String titre, String description,
                     LocalDate dateDebut, LocalTime heureDebut, LocalTime heureFin,
                     String ville, String adresse, Integer nbPlacesMax, byte[] image,
                     String categorie, LocalDate dateCreation) {
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.ville = ville;
        this.description = description;
<<<<<<< HEAD
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
=======
        this.dateDebut = dateDebut;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.ville = ville;            
        this.adresse = adresse;
        this.nbPlacesMax = nbPlacesMax;
        this.image = image;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
    }

    // ✅ Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public String getVille() { return ville; }              
    public String getAdresse() { return adresse; }
    public Integer getNbPlacesMax() { return nbPlacesMax; }
    public byte[] getImage() { return image; }
    public String getCategorie() { return categorie; }
    public LocalDate getDateCreation() { return dateCreation; }
    public LocalDate getDate() {
        return dateDebut;
    }


    // Pour compatibilité avec le code existant
    public LocalTime getHeure() { return heureDebut; }
}
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
