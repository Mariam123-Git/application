package application.application;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {
    private int id;
    private String titre;
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
        this.id = id;
        this.titre = titre;
        this.description = description;
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
