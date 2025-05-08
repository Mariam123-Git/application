package application.application;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Window;

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
        		"Today", "This week", "This month", "This quarter", "This year", "Custom"
        );
        cbPeriode.setItems(periodes);
        cbPeriode.setValue("Cette semaine");
        
        // Initialiser le combo de filtrage des événements
        ObservableList<String> statuts = FXCollections.observableArrayList(
        	   "All", "Upcoming", "Ongoing", "Completed", "Cancelled"
        );
        cbFilterEvents.setItems(statuts);
        cbFilterEvents.setValue("All");
        
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
            // Événements à venir
            String upcomingQuery = "SELECT COUNT(*) FROM evenement WHERE statut = 'publié' AND date_debut > NOW()";
            try (PreparedStatement stmt = conn.prepareStatement(upcomingQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lblUpcomingEvents.setText(String.valueOf(rs.getInt(1)));
                }
            }
            
            // Total participants
            String participantsQuery = "SELECT COUNT(*) FROM inscription WHERE statut = 'confirmée'";
            try (PreparedStatement stmt = conn.prepareStatement(participantsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lblTotalParticipants.setText(String.valueOf(rs.getInt(1)));
                }
            }
            
            // Taux de remplissage moyen
            String fillRateQuery = "SELECT AVG(CASE WHEN e.nb_places_max > 0 THEN " +
                                  "(COUNT(i.id_inscription) * 100.0 / e.nb_places_max) ELSE 0 END) " +
                                  "FROM evenement e LEFT JOIN inscription i ON e.id_evenement = i.id_evenement " +
                                  "WHERE i.statut = 'confirmée' OR i.id_inscription IS NULL " +
                                  "GROUP BY e.id_evenement";
            try (PreparedStatement stmt = conn.prepareStatement(fillRateQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avgFillRate = rs.getDouble(1);
                    lblAverageFillingRate.setText(String.format("%.1f%%", avgFillRate));
                }
            }
            
            // Total communications
            String commQuery = "SELECT COUNT(*) FROM notification";
            try (PreparedStatement stmt = conn.prepareStatement(commQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lblTotalCommunications.setText(String.valueOf(rs.getInt(1)));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Unable to load statistics: " + e.getMessage());
        }
    }
    
    /**
     * Initialise les graphiques
     */
    private void initCharts() {
        // Graphique des tendances d'inscription
        XYChart.Series<String, Number> registrationSeries = new XYChart.Series<>();
        registrationSeries.setName("Inscriptions");
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
            String query = "SELECT DATE_FORMAT(date_inscription, '%Y-%m') AS month, " +
                          "COUNT(*) AS count " +
                          "FROM inscription " +
                          "WHERE statut = 'confirmée' " +
                          "GROUP BY DATE_FORMAT(date_inscription, '%Y-%m') " +
                          "ORDER BY month";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrationSeries.getData().add(
                        new XYChart.Data<>(rs.getString("month"), rs.getInt("count"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        chartRegistrationTrend.getData().add(registrationSeries);
        
        // Graphique camembert des statuts
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
            String query = "SELECT statut, COUNT(*) AS count FROM evenement GROUP BY statut";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pieChartData.add(new PieChart.Data(
                        translateStatus(rs.getString("statut")),
                        rs.getInt("count")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        chartEventStatus.setData(pieChartData);
        chartEventStatus.setLegendVisible(true);
        
        // Graphique des événements par catégorie
        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Number of events");
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
            String query = "SELECT c.nom, COUNT(e.id_evenement) AS count " +
                          "FROM categorie c LEFT JOIN evenement e ON c.id_categorie = e.id_categorie " +
                          "GROUP BY c.nom";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorySeries.getData().add(
                        new XYChart.Data<>(rs.getString("nom"), rs.getInt("count"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        chartEventsByCategory.getData().add(categorySeries);
    }
    
    /**
     * Initialise le tableau des événements
     */
    private void initEventTable() {
        // Configurer les colonnes
        colEventId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEventTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colEventDate.setCellValueFactory(new PropertyValueFactory<>("dateD"));
        colEventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colEventCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
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
        eventsList = FXCollections.observableArrayList(
                new Event(1, "Conférence IA", LocalDate.of(2025, 6, 10), "Paris", "Tech", 100, 80, 0.8, "Ouvert"),
                new Event(2, "Hackathon", LocalDate.of(2025, 6, 15), "Lyon", "Innovation", 150, 120, 0.8, "Ouvert"),
                new Event(3, "Atelier Web", LocalDate.of(2025, 6, 20), "Marseille", "Formation", 50, 45, 0.9, "Fermé")
            );
        
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
        
        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
            String query = "SELECT e.id_evenement, e.titre, e.date_debut, e.lieu, c.nom AS categorie, " +
                          "e.nb_places_max, COUNT(i.id_inscription) AS inscriptions, " +
                          "CASE WHEN e.nb_places_max > 0 THEN (COUNT(i.id_inscription) * 100.0 / e.nb_places_max) ELSE 0 END AS taux_remplissage, " +
                          "e.statut " +
                          "FROM evenement e " +
                          "LEFT JOIN categorie c ON e.id_categorie = c.id_categorie " +
                          "LEFT JOIN inscription i ON e.id_evenement = i.id_evenement AND i.statut = 'confirmée' " +
                          "GROUP BY e.id_evenement " +
                          "ORDER BY e.date_debut";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Event event = new Event(
                        rs.getInt("id_evenement"),
                        rs.getString("titre"),
                        rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate(),
                        rs.getString("lieu"),
                        rs.getString("categorie"),
                        rs.getInt("nb_places_max"),
                        rs.getInt("inscriptions"),
                        rs.getDouble("taux_remplissage"),
                        translateStatus(rs.getString("statut"))
                    );
                    eventsList.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Unable to load events from database: " + e.getMessage());
        }
    }

    private String translateStatus(String dbStatus) {
        switch (dbStatus) {
            case "brouillon": return "Draft";
            case "publié": return "Upcoming";
            case "clôturé": return "Completed";
            case "annulé": return "Cancelled";
            default: return dbStatus;
        }
    }
    
    /**
     * Met à jour l'affichage des informations de pagination
     */
    private void updatePaginationInfo() {
        lblPageInfo.setText("Page " + currentPage + " of " + totalPages);
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
	/*
	 * private void updateStatistics() { // Cette méthode serait normalement
	 * utilisée pour charger les données réelles // Ici, nous simulons simplement
	 * quelques mises à jour
	 * 
	 * // Déterminer les variations en fonction de la période String periode =
	 * cbPeriode.getValue(); if ("Cette semaine".equals(periode)) {
	 * lblEventsVariation.setText("+12% Since last month");
	 * lblParticipantsVariation.setText("+8% Since last month");
	 * lblFillingRateVariation.setText("+5% Since last month");
	 * lblCommunicationsVariation.setText("+15% Since last month"); } else if
	 * ("Ce mois".equals(periode)) {
	 * lblEventsVariation.setText("+8% Since last month");
	 * lblParticipantsVariation.setText("+5% Since last month");
	 * lblFillingRateVariation.setText("+3% Since last month");
	 * lblCommunicationsVariation.setText("+10% Since last month"); } else if
	 * ("Personnalisée".equals(periode)) { // Calcul basé sur les dates
	 * sélectionnées LocalDate debut = dateDebut.getValue(); LocalDate fin =
	 * dateFin.getValue(); long daysBetween =
	 * java.time.temporal.ChronoUnit.DAYS.between(debut, fin);
	 * 
	 * if (daysBetween <= 7) {
	 * lblEventsVariation.setText("+5% since the previous period"); } else if
	 * (daysBetween <= 30) {
	 * lblEventsVariation.setText("+8% since the previous period"); } else {
	 * lblEventsVariation.setText("+12% since the previous period"); } } }
	 */
    
    /**
     * Met à jour les graphiques selon les filtres
     */
    private void updateCharts() {
        // Graphique des tendances d'inscription
        chartRegistrationTrend.getData().clear();
        XYChart.Series<String, Number> registrationSeries = new XYChart.Series<>();
        registrationSeries.setName("Registrations");
        
        try (Connection conn =DBConnection.getConnection()) {
            // Obtenir la période sélectionnée
            String period = cbPeriode.getValue();
            String dateCondition = "";
            
            if ("This week".equals(period)) {
                dateCondition = "WHERE date_inscription BETWEEN DATE_SUB(NOW(), INTERVAL 1 WEEK) AND NOW()";
            } else if ("This month".equals(period)) {
                dateCondition = "WHERE date_inscription BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()";
            } else if ("Custom".equals(period)) {
                LocalDate start = dateDebut.getValue();
                LocalDate end = dateFin.getValue();
                if (start != null && end != null) {
                    dateCondition = String.format("WHERE date_inscription BETWEEN '%s' AND '%s'", 
                        start.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        end.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            }
            
            String query = "SELECT DATE_FORMAT(date_inscription, '%Y-%m-%d') AS day, " +
                          "COUNT(*) AS count " +
                          "FROM inscription " +
                          dateCondition + " " +
                          "GROUP BY DATE_FORMAT(date_inscription, '%Y-%m-%d') " +
                          "ORDER BY day";
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrationSeries.getData().add(
                        new XYChart.Data<>(rs.getString("day"), rs.getInt("count"))
                    );
                }
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
        
        chartRegistrationTrend.getData().add(registrationSeries);
        
        // Mettre à jour les autres graphiques de la même manière...
    }

    private void updateStatistics() {
        try (Connection conn = DBConnection.getConnection()) {
            String periodCondition = getPeriodCondition();
            
            // Événements à venir
            String upcomingQuery = "SELECT COUNT(*) FROM evenement WHERE statut = 'publié' AND date_debut > NOW() " + 
                                 (periodCondition.isEmpty() ? "" : "AND " + periodCondition);
            try (PreparedStatement stmt = conn.prepareStatement(upcomingQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lblUpcomingEvents.setText(String.valueOf(rs.getInt(1)));
                }
            }
            
            // Variations
            updateVariationStatistics(conn, periodCondition);
            
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private void handleDatabaseError(SQLException e) {
        Platform.runLater(() -> {
            showError("Database Error", "An error occurred while accessing the database:\n" + e.getMessage());
            loadingPane.setVisible(false);
        });
        e.printStackTrace();
    }

	private String getPeriodCondition() {
        String period = cbPeriode.getValue();
        if ("This week".equals(period)) {
            return "date_debut BETWEEN DATE_SUB(NOW(), INTERVAL 1 WEEK) AND DATE_ADD(NOW(), INTERVAL 1 WEEK)";
        } else if ("This month".equals(period)) {
            return "date_debut BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND DATE_ADD(NOW(), INTERVAL 1 MONTH)";
        } else if ("Custom".equals(period)) {
            LocalDate start = dateDebut.getValue();
            LocalDate end = dateFin.getValue();
            if (start != null && end != null) {
                return String.format("date_debut BETWEEN '%s' AND '%s'", 
                    start.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    end.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        return "";
    }

    private void updateVariationStatistics(Connection conn, String periodCondition) throws SQLException {
        // Calculer les variations en pourcentage
        String prevPeriodCondition = "";
        if (!periodCondition.isEmpty()) {
            if (periodCondition.contains("INTERVAL 1 WEEK")) {
                prevPeriodCondition = periodCondition.replace("INTERVAL 1 WEEK", "INTERVAL 2 WEEK");
            } else if (periodCondition.contains("INTERVAL 1 MONTH")) {
                prevPeriodCondition = periodCondition.replace("INTERVAL 1 MONTH", "INTERVAL 2 MONTH");
            } else if (periodCondition.contains("BETWEEN")) {
                // Pour la période personnalisée, calculer la période précédente de même durée
                LocalDate start = dateDebut.getValue();
                LocalDate end = dateFin.getValue();
                if (start != null && end != null) {
                    long daysBetween = ChronoUnit.DAYS.between(start, end);
                    LocalDate prevStart = start.minusDays(daysBetween);
                    LocalDate prevEnd = start.minusDays(1);
                    prevPeriodCondition = String.format("date_debut BETWEEN '%s' AND '%s'", 
                        prevStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        prevEnd.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            }
        }
        
        // Calculer les variations pour chaque statistique
        if (!prevPeriodCondition.isEmpty()) {
            // Événements
            int currentEvents = getCountForCondition(conn, "evenement", periodCondition);
            int prevEvents = getCountForCondition(conn, "evenement", prevPeriodCondition);
            double eventVariation = prevEvents > 0 ? ((currentEvents - prevEvents) * 100.0 / prevEvents) : 0;
            lblEventsVariation.setText(String.format("%+.1f%% since previous period", eventVariation));
            
            // Participants
            int currentParticipants = getCountForCondition(conn, "inscription", 
                "WHERE statut = 'confirmée' " + (periodCondition.isEmpty() ? "" : "AND " + periodCondition));
            int prevParticipants = getCountForCondition(conn, "inscription", 
                "WHERE statut = 'confirmée' " + (prevPeriodCondition.isEmpty() ? "" : "AND " + prevPeriodCondition));
            double participantVariation = prevParticipants > 0 ? ((currentParticipants - prevParticipants) * 100.0 / prevParticipants) : 0;
            lblParticipantsVariation.setText(String.format("%+.1f%% since previous period", participantVariation));
        }
    }

    private int getCountForCondition(Connection conn, String table, String condition) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + table + " " + condition;
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Filtre les événements selon le statut sélectionné
     */
    private void filterEvents() {
        String filter = cbFilterEvents.getValue();
        loadingPane.setVisible(true);
        
        javafx.concurrent.Task<ObservableList<Event>> filterTask = new javafx.concurrent.Task<ObservableList<Event>>() {
            @Override
            protected ObservableList<Event> call() throws Exception {
                ObservableList<Event> filteredList = FXCollections.observableArrayList();
                
                try (Connection conn = DBConnection.getConnection()) {
                    String baseQuery = "SELECT e.id_evenement, e.titre, e.date_debut, e.lieu, c.nom AS categorie, " +
                                     "e.nb_places_max, COUNT(i.id_inscription) AS inscriptions, " +
                                     "CASE WHEN e.nb_places_max > 0 THEN (COUNT(i.id_inscription) * 100.0 / e.nb_places_max) ELSE 0 END AS taux_remplissage, " +
                                     "e.statut " +
                                     "FROM evenement e " +
                                     "LEFT JOIN categorie c ON e.id_categorie = c.id_categorie " +
                                     "LEFT JOIN inscription i ON e.id_evenement = i.id_evenement AND i.statut = 'confirmée' ";
                    
                    String whereClause = "";
                    if (!"All".equals(filter)) {
                        switch (filter) {
                            case "Upcoming":
                                whereClause = "WHERE e.statut = 'publié' AND e.date_debut > NOW() ";
                                break;
                            case "Ongoing":
                                whereClause = "WHERE e.statut = 'publié' AND e.date_debut <= NOW() AND e.date_fin >= NOW() ";
                                break;
                            case "Completed":
                                whereClause = "WHERE e.statut = 'clôturé' ";
                                break;
                            case "Cancelled":
                                whereClause = "WHERE e.statut = 'annulé' ";
                                break;
                        }
                    }
                    
                    String groupBy = "GROUP BY e.id_evenement " +
                                    "ORDER BY e.date_debut";
                    
                    try (PreparedStatement stmt = conn.prepareStatement(baseQuery + whereClause + groupBy);
                         ResultSet rs = stmt.executeQuery()) {
                        
                        while (rs.next()) {
                            Event event = new Event(
                                rs.getInt("id_evenement"),
                                rs.getString("titre"),
                                rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate(),
                                rs.getString("lieu"),
                                rs.getString("categorie"),
                                rs.getInt("nb_places_max"),
                                rs.getInt("inscriptions"),
                                rs.getDouble("taux_remplissage"),
                                translateStatus(rs.getString("statut"))
                            );
                            filteredList.add(event);
                        }
                    }
                } catch (SQLException e) {
                    handleDatabaseError(e);
                }
                
                return filteredList;
            }
        };
        
        filterTask.setOnSucceeded(e -> {
            tableUpcomingEvents.setItems(filterTask.getValue());
            loadingPane.setVisible(false);
        });
        
        filterTask.setOnFailed(e -> {
            loadingPane.setVisible(false);
            showError("Filter Error", "Unable to filter events");
        });
        
        new Thread(filterTask).start();
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
        alert.setTitle("Logout confirmation");
        alert.setHeaderText("Logout ");
        
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
                    currentStage.setTitle("Login - Event Management System");
                    
                    currentStage.sizeToScene();   //pour center l'élément
                    currentStage.centerOnScreen();//pour centrer l'élément
                   
                    currentStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error loading the login page : " + e.getMessage());
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export events");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("events_export_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");
        
        Window window = btnExportEvents.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        
        if (file != null) {
            loadingPane.setVisible(true);
            
            javafx.concurrent.Task<Boolean> exportTask = new javafx.concurrent.Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try (Connection conn = DBConnection.getConnection();
                         FileWriter writer = new FileWriter(file)) {
                        
                        // Écrire l'en-tête
                        writer.append("ID,Title,Start Date,End Date,Location,Category,Capacity,Registrations,Fill Rate,Status,Description\n");
                        
                        // Requête pour obtenir tous les détails des événements
                        String query = "SELECT e.id_evenement, e.titre, e.date_debut, e.date_fin, e.lieu, " +
                                      "c.nom AS categorie, e.nb_places_max, " +
                                      "COUNT(i.id_inscription) AS inscriptions, " +
                                      "CASE WHEN e.nb_places_max > 0 THEN (COUNT(i.id_inscription) * 100.0 / e.nb_places_max) ELSE 0 END AS taux_remplissage, " +
                                      "e.statut, e.description " +
                                      "FROM evenement e " +
                                      "LEFT JOIN categorie c ON e.id_categorie = c.id_categorie " +
                                      "LEFT JOIN inscription i ON e.id_evenement = i.id_evenement AND i.statut = 'confirmée' " +
                                      "GROUP BY e.id_evenement " +
                                      "ORDER BY e.date_debut";
                        
                        try (PreparedStatement stmt = conn.prepareStatement(query);
                             ResultSet rs = stmt.executeQuery()) {
                            
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            while (rs.next()) {
                                writer.append(String.valueOf(rs.getInt("id_evenement"))).append(",");
                                writer.append("\"").append(rs.getString("titre").replace("\"", "\"\"")).append("\",");
                                writer.append(rs.getTimestamp("date_debut").toLocalDateTime().format(dateFormatter)).append(",");
                                writer.append(rs.getTimestamp("date_fin").toLocalDateTime().format(dateFormatter)).append(",");
                                writer.append("\"").append(rs.getString("lieu").replace("\"", "\"\"")).append("\",");
                                writer.append("\"").append(rs.getString("categorie").replace("\"", "\"\"")).append("\",");
                                writer.append(String.valueOf(rs.getInt("nb_places_max"))).append(",");
                                writer.append(String.valueOf(rs.getInt("inscriptions"))).append(",");
                                writer.append(String.format("%.1f%%", rs.getDouble("taux_remplissage"))).append(",");
                                writer.append("\"").append(translateStatus(rs.getString("statut")).replace("\"", "\"\"")).append("\",");
                                writer.append("\"").append(rs.getString("description").replace("\"", "\"\"")).append("\"\n");
                            }
                        }
                        
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };
            
            exportTask.setOnSucceeded(e -> {
                loadingPane.setVisible(false);
                if (exportTask.getValue()) {
                    showNotification("Export successful", "Events exported successfully to: " + file.getAbsolutePath());
                } else {
                    showError("Export error", "Failed to export events");
                }
            });
            
            exportTask.setOnFailed(e -> {
                loadingPane.setVisible(false);
                showError("Export error", "Failed to export events");
            });
            
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
                getClass().getResource("/vues/event_form.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Créer une nouvelle fenêtre modale
            javafx.stage.Stage eventFormStage = new javafx.stage.Stage();
            eventFormStage.setTitle("Create a new Event");
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
            showError("Error", " Unable to open the event form : " + e.getMessage());
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
        loadingPane.setVisible(true);
        
        javafx.concurrent.Task<ObservableList<Event>> loadTask = new javafx.concurrent.Task<ObservableList<Event>>() {
            @Override
            protected ObservableList<Event> call() throws Exception {
                ObservableList<Event> pageEvents = FXCollections.observableArrayList();
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "")) {
                    int itemsPerPage = 8;
                    int offset = (page - 1) * itemsPerPage;
                    
                    // Requête avec pagination
                    String query = "SELECT e.id_evenement, e.titre, e.date_debut, e.lieu, c.nom AS categorie, " +
                                  "e.nb_places_max, COUNT(i.id_inscription) AS inscriptions, " +
                                  "CASE WHEN e.nb_places_max > 0 THEN (COUNT(i.id_inscription) * 100.0 / e.nb_places_max) ELSE 0 END AS taux_remplissage, " +
                                  "e.statut " +
                                  "FROM evenement e " +
                                  "LEFT JOIN categorie c ON e.id_categorie = c.id_categorie " +
                                  "LEFT JOIN inscription i ON e.id_evenement = i.id_evenement AND i.statut = 'confirmée' " +
                                  "GROUP BY e.id_evenement " +
                                  "ORDER BY e.date_debut " +
                                  "LIMIT ? OFFSET ?";
                    
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, itemsPerPage);
                        stmt.setInt(2, offset);
                        
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                Event event = new Event(
                                    rs.getInt("id_evenement"),
                                    rs.getString("titre"),
                                    rs.getTimestamp("date_debut").toLocalDateTime().toLocalDate(),
                                    rs.getString("lieu"),
                                    rs.getString("categorie"),
                                    rs.getInt("nb_places_max"),
                                    rs.getInt("inscriptions"),
                                    rs.getDouble("taux_remplissage"),
                                    translateStatus(rs.getString("statut"))
                                );
                                pageEvents.add(event);
                            }
                        }
                    }
                    
                    // Calcul du nombre total de pages
                    String countQuery = "SELECT COUNT(*) AS total FROM evenement";
                    try (PreparedStatement countStmt = conn.prepareStatement(countQuery);
                         ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            int totalItems = countRs.getInt("total");
                            totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
                        }
                    }
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
                
                return pageEvents;
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            eventsList.clear();
            eventsList.addAll(loadTask.getValue());
            filterEvents();
            updatePaginationInfo();
            loadingPane.setVisible(false);
        });
        
        loadTask.setOnFailed(e -> {
            showError("Loading error", "Unable to load page data.");
            loadingPane.setVisible(false);
        });
        
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
            detailsStage.setTitle("Event Details - " + event.getTitle());
            detailsStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            detailsStage.initOwner(tableUpcomingEvents.getScene().getWindow());
            detailsStage.setScene(new javafx.scene.Scene(root));
            
            // Afficher la fenêtre
            detailsStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Unable to display event details : " + e.getMessage());
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
            editStage.setTitle("Edit the event - " + event.getTitle());
            editStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            editStage.initOwner(tableUpcomingEvents.getScene().getWindow());
            editStage.setScene(new javafx.scene.Scene(root));
            
            // Afficher la fenêtre et attendre qu'elle soit fermée
            editStage.showAndWait();
            
            // Rafraîchir les données lorsque la fenêtre est fermée
            handleRefresh(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Unable to open the edit form : " + e.getMessage());
        }
    }

    
    private void handleDeleteEvent(Event event) {
        System.out.println("Delete Event: " + event.getTitle());
        
        // Afficher une boîte de dialogue de confirmation
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete the event? \"" + event.getTitle() + "\" ?",
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );
        confirmation.setTitle("Deletion confirmation");
        confirmation.setHeaderText("Delete an event");
        
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
                        
                        showNotification("Deletion successful", "The event was successfully deleted.");
                    } else {
                        showError("Deletion error", "An error occurred while deleting the event.");
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