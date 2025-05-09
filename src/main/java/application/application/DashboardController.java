package application.application;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Contrôleur pour le tableau de bord de gestion d'événements
 */
public class DashboardController implements Initializable {

    // Éléments de l'en-tête
    @FXML private Circle profileCircle;
    @FXML private Label lblAdminName;
    @FXML private Label lblAdminRole;
    @FXML private Button btnNotifications;
    @FXML private Button btnLogout;
    
    // Éléments du menu latéral
    @FXML private Button btnDashboard;
    @FXML private Button btnEvents;
    @FXML private Button btnCreateEvent;
    @FXML private Button btnParticipants;
    @FXML private Button btnCommunications;
    @FXML private Button btnReports;
    @FXML private Button btnCategories;
    @FXML private Button btnSettings;
    @FXML private Button btnHelp;
    
    // Éléments de filtrage et date
    @FXML private Label lblCurrentDate;
    @FXML private ComboBox<String> cbPeriode;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private Button btnRefresh;
    
    // Cartes de statistiques
    @FXML private Label lblUpcomingEvents;
    @FXML private Label lblEventsVariation;
    @FXML private Label lblTotalParticipants;
    @FXML private Label lblParticipantsVariation;
    @FXML private Label lblAverageFillingRate;
    @FXML private Label lblFillingRateVariation;
    @FXML private Label lblTotalCommunications;
    @FXML private Label lblCommunicationsVariation;
    
    // Graphiques
    @FXML private LineChart<String, Number> chartRegistrationTrend;
    @FXML private PieChart chartEventStatus;
    @FXML private BarChart<String, Number> chartEventsByCategory;
    @FXML private BarChart<String, Number> chartParticipationByDay;
    
    // Tableau des événements à venir
    @FXML private ComboBox<String> cbFilterEvents;
    @FXML private Button btnExportEvents;
    @FXML private Button btnAddEvent;
    @FXML private TableView<Event> tableUpcomingEvents;
    @FXML private TableColumn<Event, Integer> colEventId;
    @FXML private TableColumn<Event, String> colEventTitle;
    @FXML private TableColumn<Event, LocalDate> colEventDate;
    @FXML private TableColumn<Event, String> colEventLocation;
    @FXML private TableColumn<Event, String> colEventCategory;
    @FXML private TableColumn<Event, Integer> colEventCapacity;
    @FXML private TableColumn<Event, Integer> colEventRegistrations;
    @FXML private TableColumn<Event, Double> colEventFillRate;
    @FXML private TableColumn<Event, String> colEventStatus;
    @FXML private TableColumn<Event, Void> colEventActions;
    
    // Pagination
    @FXML private Label lblPageInfo;
    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;
    
    // Indicateur de chargement
    @FXML private StackPane loadingPane;
    
    // Variables de l'application
    private int currentPage = 1;
    private int totalPages = 5;
    private ObservableList<Event> eventsList = FXCollections.observableArrayList();
    
    /**
     * Classe modèle pour les événements
     */
    public static class Event {
        private Integer id;
        private String title;
        private LocalDate date;
        private String location;
        private String category;
        private Integer capacity;
        private Integer registrations;
        private Double fillRate;
        private String status;
        
        public Event(Integer id, String title, LocalDate date, String location, String category,
                    Integer capacity, Integer registrations, Double fillRate, String status) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.location = location;
            this.category = category;
            this.capacity = capacity;
            this.registrations = registrations;
            this.fillRate = fillRate;
            this.status = status;
        }
        
        // Getters
        public Integer getId() { return id; }
        public String getTitle() { return title; }
        public LocalDate getDate() { return date; }
        public String getLocation() { return location; }
        public String getCategory() { return category; }
        public Integer getCapacity() { return capacity; }
        public Integer getRegistrations() { return registrations; }
        public Double getFillRate() { return fillRate; }
        public String getStatus() { return status; }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la date actuelle
        lblCurrentDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        
        // Initialiser les données utilisateur
        initUserData();
        
        // Initialiser les combobox
        initComboBoxes();
        
        // Initialiser les statistiques
        initStatistics();
        
        // Initialiser les graphiques
        initCharts();
        
        // Initialiser le tableau des événements
        initEventTable();
        
        // Mettre à jour les informations de pagination
        updatePaginationInfo();
    }
    
    /**
     * Initialise les données de l'utilisateur
     */
    private void initUserData() {
        lblAdminName.setText("Admin User");
        lblAdminRole.setText("Super Admin");
    }
    
    /**
     * Initialise les combobox
     */
    private void initComboBoxes() {
        // Initialiser le combo de période
        ObservableList<String> periodes = FXCollections.observableArrayList(
            "Aujourd'hui", "Cette semaine", "Ce mois", "Ce trimestre", "Cette année", "Personnalisée"
        );
        cbPeriode.setItems(periodes);
        cbPeriode.setValue("Cette semaine");
        
        // Initialiser le combo de filtrage des événements
        ObservableList<String> statuts = FXCollections.observableArrayList(
            "Tous", "À venir", "En cours", "Terminé", "Annulé"
        );
        cbFilterEvents.setItems(statuts);
        cbFilterEvents.setValue("Tous");
        
        // Ajouter les écouteurs pour mettre à jour les données lors du changement
        cbPeriode.setOnAction(event -> updateDashboardData());
        cbFilterEvents.setOnAction(event -> filterEvents());
        
        // Date pickers
        dateDebut.setValue(LocalDate.now().minusDays(7));
        dateFin.setValue(LocalDate.now().plusDays(30));
        
        dateDebut.setOnAction(event -> updateDashboardData());
        dateFin.setOnAction(event -> updateDashboardData());
    }
    
    /**
     * Initialise les statistiques du tableau de bord
     */
    private void initStatistics() {
        // Simuler quelques données pour les cartes de statistiques
        lblUpcomingEvents.setText("12");
        lblEventsVariation.setText("+12% depuis le mois dernier");
        
        lblTotalParticipants.setText("358");
        lblParticipantsVariation.setText("+8% depuis le mois dernier");
        
        lblAverageFillingRate.setText("75%");
        lblFillingRateVariation.setText("+5% depuis le mois dernier");
        
        lblTotalCommunications.setText("48");
        lblCommunicationsVariation.setText("+15% depuis le mois dernier");
    }
    
    /**
     * Initialise les graphiques
     */
    private void initCharts() {
        // Graphique des tendances d'inscription
        XYChart.Series<String, Number> registrationSeries = new XYChart.Series<>();
        registrationSeries.setName("Inscriptions");
        registrationSeries.getData().add(new XYChart.Data<>("Jan", 150));
        registrationSeries.getData().add(new XYChart.Data<>("Fév", 200));
        registrationSeries.getData().add(new XYChart.Data<>("Mar", 250));
        registrationSeries.getData().add(new XYChart.Data<>("Avr", 300));
        registrationSeries.getData().add(new XYChart.Data<>("Mai", 320));
        registrationSeries.getData().add(new XYChart.Data<>("Juin", 380));
        
        chartRegistrationTrend.getData().add(registrationSeries);
        
        // Graphique camembert des statuts
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("À venir", 65),
            new PieChart.Data("En cours", 15),
            new PieChart.Data("Terminés", 15),
            new PieChart.Data("Annulés", 5)
        );
        chartEventStatus.setData(pieChartData);
        chartEventStatus.setLegendVisible(true);
        
        // Graphique des événements par catégorie
        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Nombre d'événements");
        categorySeries.getData().add(new XYChart.Data<>("Conférences", 10));
        categorySeries.getData().add(new XYChart.Data<>("Séminaires", 8));
        categorySeries.getData().add(new XYChart.Data<>("Ateliers", 15));
        categorySeries.getData().add(new XYChart.Data<>("Expositions", 6));
        categorySeries.getData().add(new XYChart.Data<>("Galas", 4));
        
        chartEventsByCategory.getData().add(categorySeries);
        
        // Graphique de participation par jour de semaine
        XYChart.Series<String, Number> participationSeries = new XYChart.Series<>();
        participationSeries.setName("Taux de participation (%)");
        participationSeries.getData().add(new XYChart.Data<>("Lundi", 65));
        participationSeries.getData().add(new XYChart.Data<>("Mardi", 70));
        participationSeries.getData().add(new XYChart.Data<>("Mercredi", 85));
        participationSeries.getData().add(new XYChart.Data<>("Jeudi", 78));
        participationSeries.getData().add(new XYChart.Data<>("Vendredi", 90));
        participationSeries.getData().add(new XYChart.Data<>("Samedi", 95));
        participationSeries.getData().add(new XYChart.Data<>("Dimanche", 75));
        
        chartParticipationByDay.getData().add(participationSeries);
    }
    
    /**
     * Initialise le tableau des événements
     */
    private void initEventTable() {
        // Configurer les colonnes
        colEventId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEventTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colEventDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colEventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colEventCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colEventCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colEventRegistrations.setCellValueFactory(new PropertyValueFactory<>("registrations"));
        colEventFillRate.setCellValueFactory(new PropertyValueFactory<>("fillRate"));
        colEventStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Formatter la colonne de date
        colEventDate.setCellFactory(column -> new javafx.scene.control.TableCell<Event, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        
        // Formatter la colonne de taux de remplissage
        colEventFillRate.setCellFactory(column -> new javafx.scene.control.TableCell<Event, Double>() {
            @Override
            protected void updateItem(Double fillRate, boolean empty) {
                super.updateItem(fillRate, empty);
                if (empty || fillRate == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", fillRate));
                }
            }
        });
        
        // Configurer la colonne d'actions
        initActionsColumn();
        
        // Charger les données de test
        loadSampleData();
        
        // Appliquer les données au tableau
        tableUpcomingEvents.setItems(eventsList);
    }
    
    /**
     * Initialise la colonne d'actions avec des boutons
     */
    private void initActionsColumn() {
        colEventActions.setCellFactory(param -> new javafx.scene.control.TableCell<Event, Void>() {
            private final Button viewButton = new Button("");
            private final Button editButton = new Button("");
            private final Button deleteButton = new Button("");
            
            {
                // Configurer les boutons avec des icônes
                FontAwesomeIconView viewIcon = new FontAwesomeIconView();
                viewIcon.setGlyphName("EYE");
                viewIcon.setSize("14");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button-small");
                
                FontAwesomeIconView editIcon = new FontAwesomeIconView();
                editIcon.setGlyphName("PENCIL");
                editIcon.setSize("14");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button-small");
                
                FontAwesomeIconView deleteIcon = new FontAwesomeIconView();
                deleteIcon.setGlyphName("TRASH");
                deleteIcon.setSize("14");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("action-button-small");
                
                // Ajouter les actions
                viewButton.setOnAction(event -> {
                    Event data = getTableView().getItems().get(getIndex());
                    handleViewEvent(data);
                });
                
                editButton.setOnAction(event -> {
                    Event data = getTableView().getItems().get(getIndex());
                    handleEditEvent(data);
                });
                
                deleteButton.setOnAction(event -> {
                    Event data = getTableView().getItems().get(getIndex());
                    handleDeleteEvent(data);
                });
            }
            
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttonsContainer = new javafx.scene.layout.HBox(5);
                    buttonsContainer.getChildren().addAll(viewButton, editButton, deleteButton);
                    setGraphic(buttonsContainer);
                }
            }
        });
    }
    
    /**
     * Charge des données de test pour le tableau des événements
     */
    private void loadSampleData() {
        eventsList.clear();
        
        // Ajouter quelques événements de test
        eventsList.add(new Event(1, "Conférence annuelle", LocalDate.now().plusDays(5), 
                "Paris", "Conférence", 200, 180, 90.0, "À venir"));
        eventsList.add(new Event(2, "Atelier de développement", LocalDate.now().plusDays(10), 
                "Lyon", "Atelier", 30, 25, 83.3, "À venir"));
        eventsList.add(new Event(3, "Séminaire marketing", LocalDate.now().plusDays(15), 
                "Marseille", "Séminaire", 50, 35, 70.0, "À venir"));
        eventsList.add(new Event(4, "Exposition d'art", LocalDate.now().plusDays(20), 
                "Bordeaux", "Exposition", 100, 60, 60.0, "À venir"));
        eventsList.add(new Event(5, "Gala de charité", LocalDate.now().plusDays(25), 
                "Lille", "Gala", 150, 120, 80.0, "À venir"));
        eventsList.add(new Event(6, "Forum des associations", LocalDate.now().plusDays(30), 
                "Toulouse", "Forum", 80, 50, 62.5, "À venir"));
        eventsList.add(new Event(7, "Colloque scientifique", LocalDate.now().plusDays(35), 
                "Nice", "Colloque", 120, 95, 79.2, "À venir"));
        eventsList.add(new Event(8, "Soirée de networking", LocalDate.now().plusDays(40), 
                "Strasbourg", "Networking", 75, 60, 80.0, "À venir"));
    }
    
    /**
     * Met à jour l'affichage des informations de pagination
     */
    private void updatePaginationInfo() {
        lblPageInfo.setText("Page " + currentPage + " de " + totalPages);
        btnPrevPage.setDisable(currentPage <= 1);
        btnNextPage.setDisable(currentPage >= totalPages);
    }
    
    /**
     * Met à jour les données du tableau de bord en fonction des filtres
     */
    private void updateDashboardData() {
        // Afficher l'indicateur de chargement
        loadingPane.setVisible(true);
        
        // Simuler un chargement asynchrone
        new Thread(() -> {
            try {
                // Simuler un temps de traitement
                Thread.sleep(1000);
                
                // Mettre à jour l'UI dans le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    // Mettre à jour les statistiques et graphiques selon les dates sélectionnées
                    updateStatistics();
                    updateCharts();
                    loadSampleData(); // Recharger les données des événements
                    
                    // Masquer l'indicateur de chargement
                    loadingPane.setVisible(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Met à jour les statistiques selon les filtres
     */
    private void updateStatistics() {
        // Cette méthode serait normalement utilisée pour charger les données réelles
        // Ici, nous simulons simplement quelques mises à jour
        
        // Déterminer les variations en fonction de la période
        String periode = cbPeriode.getValue();
        if ("Cette semaine".equals(periode)) { 
            lblEventsVariation.setText("+12% depuis le mois dernier");
            lblParticipantsVariation.setText("+8% depuis le mois dernier");
            lblFillingRateVariation.setText("+5% depuis le mois dernier");
            lblCommunicationsVariation.setText("+15% depuis le mois dernier");
        } else if ("Ce mois".equals(periode)) {
            lblEventsVariation.setText("+8% depuis le mois dernier");
            lblParticipantsVariation.setText("+5% depuis le mois dernier");
            lblFillingRateVariation.setText("+3% depuis le mois dernier");
            lblCommunicationsVariation.setText("+10% depuis le mois dernier");
        } else if ("Personnalisée".equals(periode)) {
            // Calcul basé sur les dates sélectionnées
            LocalDate debut = dateDebut.getValue();
            LocalDate fin = dateFin.getValue();
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(debut, fin);
            
            if (daysBetween <= 7) {
                lblEventsVariation.setText("+5% depuis la période précédente");
            } else if (daysBetween <= 30) {
                lblEventsVariation.setText("+8% depuis la période précédente");
            } else {
                lblEventsVariation.setText("+12% depuis la période précédente");
            }
        }
    }
    
    /**
     * Met à jour les graphiques selon les filtres
     */
    private void updateCharts() {
        // Cette méthode serait normalement utilisée pour charger les données réelles
        // Ici, nous gardons les mêmes graphiques pour simplifier
    }
    
    /**
     * Filtre les événements selon le statut sélectionné
     */
    private void filterEvents() {
        String filter = cbFilterEvents.getValue();
        
        if ("Tous".equals(filter)) {
            // Pas de filtrage, afficher tous les événements
            tableUpcomingEvents.setItems(eventsList);
        } else {
            // Filtrer selon le statut
            ObservableList<Event> filteredList = FXCollections.observableArrayList();
            for (Event event : eventsList) {
                if (event.getStatus().equals(filter)) {
                    filteredList.add(event);
                }
            }
            tableUpcomingEvents.setItems(filteredList);
        }
    }
    
    // Gestionnaires d'événements pour les boutons du menu
    @FXML
    private void handleDashboard(ActionEvent event) {
        System.out.println("Dashboard clicked");
    }

    @FXML
    private void handleEvents(ActionEvent event) {
        System.out.println("Events clicked");
    }

    @FXML
    private void handleCreateEvent(ActionEvent event) {
        System.out.println("Create Event clicked");
    }

    @FXML
    private void handleParticipants(ActionEvent event) {
        System.out.println("Participants clicked");
    }

    @FXML
    private void handleCommunications(ActionEvent event) {
        System.out.println("Communications clicked");
    }

    @FXML
    private void handleReports(ActionEvent event) {
        System.out.println("Reports clicked");
    }

    @FXML
    private void handleCategories(ActionEvent event) {
        System.out.println("Categories clicked");
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        System.out.println("Settings clicked");
    }

    @FXML
    private void handleHelp(ActionEvent event) {
        System.out.println("Help clicked");
    }

 // Fonction pour gérer la déconnexion
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
        // Afficher une boîte de dialogue de confirmation
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION,
            "Êtes-vous sûr de vouloir vous déconnecter ?",
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );
        alert.setTitle("Confirmation de déconnexion");
        alert.setHeaderText("Déconnexion");
        
        // Gérer la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.YES) {
                // Fermer la session et rediriger vers la page de connexion
                try {
                    // Rediriger vers la page de connexion
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/vues/login.fxml")
                    );
                    javafx.scene.Parent root = loader.load();
                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    
                    // Obtenir la scène courante et changer son contenu
                    javafx.stage.Stage currentStage = (javafx.stage.Stage) btnLogout.getScene().getWindow();
                    currentStage.setScene(scene);
                    currentStage.setTitle("Connexion - Système de Gestion d'Événements");
                    currentStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors du chargement de la page de connexion : " + e.getMessage());
                }
            }
        });
    }
    
 // Fonction pour rafraîchir les données du tableau de bord
    @FXML
    private void handleRefresh(ActionEvent event) {
        // Afficher l'indicateur de chargement
        loadingPane.setVisible(true);
        
        // Créer et lancer une tâche en arrière-plan
        javafx.concurrent.Task<Void> refreshTask = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simuler un temps de chargement (à remplacer par les appels réels à la BD)
                Thread.sleep(1500);
                return null;
            }
        };
        
        // Ajouter un gestionnaire de succès
        refreshTask.setOnSucceeded(e -> {
            // Mettre à jour les statistiques
            updateStatistics();
            
            // Mettre à jour les graphiques
            updateCharts();
            
            // Recharger les données du tableau
            loadSampleData();
            
            // Appliquer les filtres actuels
            filterEvents();
            
            // Mettre à jour le message de la date actuelle
            lblCurrentDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            
            // Masquer l'indicateur de chargement
            loadingPane.setVisible(false);
        });
        
        // Lancer la tâche dans un thread séparé
        new Thread(refreshTask).start();
    }


 // Fonction pour exporter les événements au format CSV
    @FXML
    private void handleExportEvents(ActionEvent event) {
        System.out.println("Export Events clicked");
        
        // Créer un sélecteur de fichier
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Exporter les événements");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("evenements_export_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");
        
        // Afficher la boîte de dialogue de sauvegarde
        javafx.stage.Window window = btnExportEvents.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(window);
        
        if (file != null) {
            // Afficher l'indicateur de chargement
            loadingPane.setVisible(true);
            
            // Créer une tâche d'exportation en arrière-plan
            javafx.concurrent.Task<Boolean> exportTask = new javafx.concurrent.Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        // Créer un fichier CSV
                        java.io.FileWriter writer = new java.io.FileWriter(file);
                        
                        // Écrire l'en-tête
                        writer.append("ID,Titre,Date,Lieu,Catégorie,Capacité,Inscriptions,Taux de remplissage,Statut\n");
                        
                        // Écrire les données
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        for (Event event : tableUpcomingEvents.getItems()) {
                            writer.append(String.valueOf(event.getId())).append(",");
                            writer.append("\"").append(event.getTitle().replace("\"", "\"\"")).append("\",");
                            writer.append(event.getDate().format(dateFormatter)).append(",");
                            writer.append("\"").append(event.getLocation().replace("\"", "\"\"")).append("\",");
                            writer.append("\"").append(event.getCategory().replace("\"", "\"\"")).append("\",");
                            writer.append(String.valueOf(event.getCapacity())).append(",");
                            writer.append(String.valueOf(event.getRegistrations())).append(",");
                            writer.append(String.format("%.1f%%", event.getFillRate())).append(",");
                            writer.append("\"").append(event.getStatus().replace("\"", "\"\"")).append("\"\n");
                        }
                        
                        // Fermer le fichier
                        writer.flush();
                        writer.close();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };
            
            // Ajouter des gestionnaires pour la fin de la tâche
            exportTask.setOnSucceeded(e -> {
                loadingPane.setVisible(false);
                if (exportTask.getValue()) {
                    showNotification("Exportation réussie", "Les événements ont été exportés avec succès !");
                } else {
                    showError("Erreur d'exportation", "Une erreur est survenue lors de l'exportation.");
                }
            });
            
            exportTask.setOnFailed(e -> {
                loadingPane.setVisible(false);
                showError("Erreur d'exportation", "Une erreur est survenue lors de l'exportation.");
            });
            
            // Lancer la tâche en arrière-plan
            new Thread(exportTask).start();
        }
    }


 // Fonction pour ajouter un nouvel événement
    @FXML
    private void handleAddEvent(ActionEvent event) {
        System.out.println("Add Event clicked");
        
        try {
            // Charger le formulaire de création d'événement
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/application/application/event_form.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Créer une nouvelle fenêtre modale
            javafx.stage.Stage eventFormStage = new javafx.stage.Stage();
            eventFormStage.setTitle("Créer un nouvel événement");
            eventFormStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            eventFormStage.initOwner(btnAddEvent.getScene().getWindow());
            eventFormStage.setScene(new javafx.scene.Scene(root));
            
            // Configurer le contrôleur si nécessaire
            /*
            EventFormController controller = loader.getController();
            controller.setDashboardController(this);
            */
            
            // Afficher la fenêtre et attendre qu'elle soit fermée
            eventFormStage.showAndWait();
            
            // Rafraîchir les données lorsque la fenêtre est fermée
            handleRefresh(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire d'événement : " + e.getMessage());
        }
    }
    
 // Fonction pour gérer la pagination vers la page précédente
    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updatePaginationInfo();
            loadPageData(currentPage);
        }
    }

    // Fonction pour gérer la pagination vers la page suivante
    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            updatePaginationInfo();
            loadPageData(currentPage);
        }
    }
     // Fonction pour charger les données d'une page spécifique
        private void loadPageData(int page) {
            // Afficher l'indicateur de chargement
            loadingPane.setVisible(true);
            
            // Créer et lancer une tâche de chargement en arrière-plan
            javafx.concurrent.Task<ObservableList<Event>> loadTask = new javafx.concurrent.Task<ObservableList<Event>>() {
                @Override
                protected ObservableList<Event> call() throws Exception {
                    // Simuler un temps de chargement (à remplacer par les appels réels à la BD)
                    Thread.sleep(800);
                    
                    // Créer une nouvelle liste d'événements pour cette page
                    ObservableList<Event> pageEvents = FXCollections.observableArrayList();
                    
                    // Ici, vous implémenteriez la logique pour charger les données de la page depuis la BD
                    // En attendant, nous simulons des données différentes pour chaque page
                    int startId = (page - 1) * 8 + 1;
                    
                    for (int i = 0; i < 8; i++) {
                        int id = startId + i;
                        if (id <= 30) { // Supposons un maximum de 30 événements
                            LocalDate eventDate = LocalDate.now().plusDays(id * 3);
                            int capacity = 50 + (id * 10);
                            int registrations = (int)(capacity * (0.5 + (Math.random() * 0.4)));
                            double fillRate = 100.0 * registrations / capacity;
                            
                            pageEvents.add(new Event(
                                id, 
                                "Événement " + id, 
                                eventDate,
                                "Ville " + (id % 10 + 1),
                                "Catégorie " + (id % 5 + 1),
                                capacity,
                                registrations,
                                fillRate,
                                "À venir"
                            ));
                        }
                    }
                    
                    return pageEvents;
                }
            };
            
            // Gérer la fin de la tâche
            loadTask.setOnSucceeded(e -> {
                eventsList.clear();
                eventsList.addAll(loadTask.getValue());
                filterEvents();
                loadingPane.setVisible(false);
            });
            
            loadTask.setOnFailed(e -> {
                showError("Erreur de chargement", "Impossible de charger les données de la page.");
                loadingPane.setVisible(false);
            });
            
            // Lancer la tâche en arrière-plan
            new Thread(loadTask).start();
        }
    
 // Gestionnaires pour les actions sur les événements individuels
    private void handleViewEvent(Event event) {
        System.out.println("View Event: " + event.getTitle());
        
        try {
            // Charger la vue détaillée de l'événement
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/vues/event_details.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Configurer le contrôleur avec les données de l'événement
            /*
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            */
            
            // Créer une nouvelle fenêtre
            javafx.stage.Stage detailsStage = new javafx.stage.Stage();
            detailsStage.setTitle("Détails de l'événement - " + event.getTitle());
            detailsStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            detailsStage.initOwner(tableUpcomingEvents.getScene().getWindow());
            detailsStage.setScene(new javafx.scene.Scene(root));
            
            // Afficher la fenêtre
            detailsStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'afficher les détails de l'événement : " + e.getMessage());
        }
    }
    
    private void handleEditEvent(Event event) {
        System.out.println("Edit Event: " + event.getTitle());
        
        try {
            // Charger le formulaire d'édition d'événement
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/vues/event_form.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Configurer le contrôleur avec les données de l'événement
            /*
            EventFormController controller = loader.getController();
            controller.setEvent(event);
            controller.setDashboardController(this);
            controller.setEditMode(true);
            */
            
            // Créer une nouvelle fenêtre
            javafx.stage.Stage editStage = new javafx.stage.Stage();
            editStage.setTitle("Modifier l'événement - " + event.getTitle());
            editStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            editStage.initOwner(tableUpcomingEvents.getScene().getWindow());
            editStage.setScene(new javafx.scene.Scene(root));
            
            // Afficher la fenêtre et attendre qu'elle soit fermée
            editStage.showAndWait();
            
            // Rafraîchir les données lorsque la fenêtre est fermée
            handleRefresh(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire d'édition : " + e.getMessage());
        }
    }

    
    private void handleDeleteEvent(Event event) {
        System.out.println("Delete Event: " + event.getTitle());
        
        // Afficher une boîte de dialogue de confirmation
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION,
            "Êtes-vous sûr de vouloir supprimer l'événement \"" + event.getTitle() + "\" ?",
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer un événement");
        
        // Traiter la réponse
        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.YES) {
                // Afficher l'indicateur de chargement
                loadingPane.setVisible(true);
                
                // Créer et lancer une tâche de suppression en arrière-plan
                javafx.concurrent.Task<Boolean> deleteTask = new javafx.concurrent.Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            // Simuler un traitement (à remplacer par les appels réels à la BD)
                            Thread.sleep(1000);
                            
                            // Ici, vous intégreriez le code pour supprimer l'événement de la base de données
                            // En attendant, nous supprimons simplement de la liste observable
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };
                
                // Gérer la fin de la tâche
                deleteTask.setOnSucceeded(e -> {
                    if (deleteTask.getValue()) {
                        // Supprimer l'événement de la liste observable
                        eventsList.remove(event);
                        // Rafraîchir le tableau
                        filterEvents();
                        // Mettre à jour les statistiques et graphiques
                        updateStatistics();
                        updateCharts();
                        
                        showNotification("Suppression réussie", "L'événement a été supprimé avec succès.");
                    } else {
                        showError("Erreur de suppression", "Une erreur est survenue lors de la suppression de l'événement.");
                    }
                    loadingPane.setVisible(false);
                });
                
                deleteTask.setOnFailed(e -> {
                    showError("Erreur de suppression", "Une erreur est survenue lors de la suppression de l'événement.");
                    loadingPane.setVisible(false);
                });
                
                // Lancer la tâche en arrière-plan
                new Thread(deleteTask).start();
            }
        });
    }

    
    //Fonction supplémentaire
    
 // Fonction utilitaire pour afficher une notification
    private void showNotification(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION, message
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Fonction utilitaire pour afficher une erreur
    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR, message
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}