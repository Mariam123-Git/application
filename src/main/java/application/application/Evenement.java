package application.application;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String lieu;
    private String adresse;
    private Integer nbPlacesMax;
    private String imageUrl;
    private String categorie;
    private String statut;

    public Evenement(int id, String titre, String description, 
                    LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                    String lieu, String adresse, Integer nbPlacesMax, 
                    String imageUrl, String categorie, String statut) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.lieu = lieu;
        this.adresse = adresse;
        this.nbPlacesMax = nbPlacesMax;
        this.imageUrl = imageUrl;
        this.categorie = categorie;
        this.statut = statut;
    }

    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public String getLieu() { return lieu; }
    public String getAdresse() { return adresse; }
    public Integer getNbPlacesMax() { return nbPlacesMax; }
    public String getImageUrl() { return imageUrl; }
    public String getCategorie() { return categorie; }
    public String getStatut() { return statut; }
    
    // Pour compatibilité avec le code existant
    public LocalTime getHeure() { return heureDebut; }
}