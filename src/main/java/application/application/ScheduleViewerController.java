package application.application;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Locale;
import java.util.function.Predicate;
import java.time.LocalTime;
import java.util.function.Function;

public class ScheduleViewerController {

    // --- FXML Injected Components ---
    @FXML private BorderPane mainLayout;
    @FXML private ComboBox<String> viewModeComboBox;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField searchField;
    @FXML private TabPane viewTabPane;
    @FXML private Button searchButton;
 
    @FXML
    private StackPane welcomeBanner;
    
    @FXML
    private HBox eventsToggleGroup;
    
    @FXML 
    private VBox homeSection;
    @FXML 
    private VBox eventSection;
    @FXML 
    private VBox contactSection;
    @FXML 
    private VBox aboutSection;
    @FXML 
    private ScrollPane scrollPane; 
    @FXML
    private ComboBox<String> subjectComboBox;

    // Calendar view
    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private Button prevMonthBtn;
    @FXML private Button nextMonthBtn;
    @FXML private Button todayBtn;
    @FXML private ComboBox<Month> moisComboBox;
    @FXML private ComboBox<Integer> anneeComboBox;

    // List view
    @FXML private VBox eventsListContainer;

    // Agenda view
    @FXML private GridPane weekGrid;
    @FXML private Label weekLabel;
    @FXML private Button prevWeekBtn;
    @FXML private Button nextWeekBtn;
    @FXML private Button currentWeekBtn;

    // Status bar
    @FXML private Label totalEventsLabel;
    @FXML private Label upcomingEventsLabel;
    @FXML private Label categoryStatsLabel;
    @FXML private Button exportBtn;
    @FXML private Button settingsBtn;

    // --- Controller Variables ---
    private EvenementService evenementService;
    private ObservableList<Evenement> allEvents;
    private ObservableList<Evenement> filteredEvents;

    // Current view state
    private YearMonth currentYearMonth;
    private LocalDate currentWeekStart;
    
    // Format patterns
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
    private final DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("'Semaine du' d 'au' d MMMM yyyy", Locale.FRENCH);
    
    // Category colors
    private final Map<String, String> categoryStyleClasses = new HashMap<>();
    
    // Localization
    private final Locale locale = Locale.FRENCH;
    private final WeekFields weekFields = WeekFields.of(locale);

  
    private int currentUserId;
    private String currentUserEmail;

    public void setUserData(int userId, String userEmail) {
        this.currentUserId = userId;
        this.currentUserEmail = userEmail;
    }
    /**
     * Naviguer vers la page d'accueil
     * @param event L'événement de clic
     */
    
    
    @FXML
    public void goToHome(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/vues/Home.fxml"));
            Scene scene = new Scene(root);
            // Cette ligne évite les problèmes d'injection FXML
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page du planning.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page des événements
     * @param event L'événement de clic
     */
    @FXML
    public void goToEvents(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/events.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page des événements.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page À propos
     * @param event L'événement de clic
     */
    @FXML
    public void goToAbout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/about.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page À propos.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page de contact
     * @param event L'événement de clic
     */
    @FXML
    public void goToContact(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/contact.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page de contact.");
            e.printStackTrace();
        }
    }


    /**
     * Afficher une alerte d'information
     * @param title Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'erreur
     * @param title Le titre de l'alerte
     * @param message Le message d'erreur à afficher
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

 
   

    @FXML
    public void initialize() {
       
           
  
        // Initialize service
        evenementService = new EvenementService();
        allEvents = FXCollections.observableArrayList();
        filteredEvents = FXCollections.observableArrayList();
        
        currentYearMonth = YearMonth.from(LocalDate.now());
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        
      
        setupCategoryStyles();
        
        // Initialize components
        initializeComboBoxes();
        initializeListeners();
        initializeCalendarView();
        initializeAgendaView();
        
        // Load initial data
        loadEvents();
    }
    
    private void setupCategoryStyles() {
        categoryStyleClasses.put("Travail", "event-category-work");
        categoryStyleClasses.put("Personnel", "event-category-personal");
        categoryStyleClasses.put("Congé", "event-category-holiday");
        categoryStyleClasses.put("Autre", "event-category-other");
    }
    
    private void initializeComboBoxes() {
      
   
        categorieComboBox.getItems().add("Toutes les catégories");
        categorieComboBox.getItems().addAll(evenementService.getAllCategories());
        categorieComboBox.setValue("Toutes les catégories");
        
     
        sortComboBox.getItems().addAll("Date (croissant)", "Date (décroissant)", "Catégorie", "Titre");
        sortComboBox.setValue("Date (croissant)");
        
     
        moisComboBox.setConverter(new StringConverter<Month>() {
            @Override
            public String toString(Month month) {
                return month != null ? month.getDisplayName(TextStyle.FULL, locale) : "";
            }

            @Override
            public Month fromString(String string) {
                for (Month month : Month.values()) {
                    if (month.getDisplayName(TextStyle.FULL, locale).equals(string)) {
                        return month;
                    }
                }
                return null;
            }
        });
        
        ObservableList<Month> months = FXCollections.observableArrayList(Month.values());
        moisComboBox.setItems(months);
        moisComboBox.setValue(Month.of(LocalDate.now().getMonthValue()));
        

        int currentYear = LocalDate.now().getYear();
        anneeComboBox.getItems().addAll(IntStream.rangeClosed(currentYear - 2, currentYear + 5)
                .boxed().collect(Collectors.toList()));
        anneeComboBox.setValue(currentYear);
        
  
        startDatePicker.setValue(currentYearMonth.atDay(1));
        endDatePicker.setValue(currentYearMonth.atEndOfMonth());
    }
    
    private void initializeListeners() {
       
        viewTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    updateCalendarView();
                    break;
                case 1:
                    updateListView();
                    break;
                case 2: 
                    updateAgendaView();
                    break;
            }
        });
        
        // Calendar navigation
        prevMonthBtn.setOnAction(e -> navigateMonth(-1));
        nextMonthBtn.setOnAction(e -> navigateMonth(1));
        todayBtn.setOnAction(e -> goToToday());
        
        // Month/Year selection for calendar
        moisComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && anneeComboBox.getValue() != null) {
                currentYearMonth = YearMonth.of(anneeComboBox.getValue(), newVal.getValue());
                updateCalendarView();
            }
        });
        
        anneeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && moisComboBox.getValue() != null) {
                currentYearMonth = YearMonth.of(newVal, moisComboBox.getValue().getValue());
                updateCalendarView();
            }
        });
        
        // Week navigation
        prevWeekBtn.setOnAction(e -> navigateWeek(-1));
        nextWeekBtn.setOnAction(e -> navigateWeek(1));
        currentWeekBtn.setOnAction(e -> goToCurrentWeek());
        
        // Filter controls
        categorieComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchButton.setOnAction(e -> applyFilters());
        
        // Sort options
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateListView());
    }
    
    // --- Data Loading ---
    private void loadEvents() {
        Task<List<Evenement>> loadTask = new Task<>() {
            @Override
            protected List<Evenement> call() {
                return evenementService.getAllEvents();
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            allEvents.setAll(loadTask.getValue());
            applyFilters();
            updateStatusBar();
            refreshCurrentView();
        });
        
        loadTask.setOnFailed(e -> {
            showError("Erreur lors du chargement des événements: " + loadTask.getException().getMessage());
        });
        
        new Thread(loadTask).start();
    }
    
    private void applyFilters() {
        Task<List<Evenement>> filterTask = new Task<>() {
            @Override
            protected List<Evenement> call() {
                return allEvents.stream()
                    .filter(createCategoryFilter())
                    .filter(createDateRangeFilter())
                    .filter(createSearchFilter())
                    .collect(Collectors.toList());
            }
        };
        
        filterTask.setOnSucceeded(e -> {
            filteredEvents.setAll(filterTask.getValue());
            refreshCurrentView();
            updateStatusBar();
        });
        
        new Thread(filterTask).start();
    }
    
    private Predicate<Evenement> createCategoryFilter() {
        String selectedCategory = categorieComboBox.getValue();
        if (selectedCategory == null || selectedCategory.equals("Toutes les catégories")) {
            return e -> true;
        }
        return e -> e.getCategorie().equals(selectedCategory);
    }
    
    private Predicate<Evenement> createDateRangeFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        if (start == null || end == null) {
            return e -> true;
        }
        
        return e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end);
    }
    
    private Predicate<Evenement> createSearchFilter() {
        String searchTerm = searchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return e -> true;
        }
        
        String lowerCaseSearch = searchTerm.toLowerCase();
        return e -> e.getTitre().toLowerCase().contains(lowerCaseSearch) || 
                  (e.getDescription() != null && e.getDescription().toLowerCase().contains(lowerCaseSearch));
    }
    
    private void updateStatusBar() {
        int total = filteredEvents.size();
        long upcoming = filteredEvents.stream()
            .filter(e -> e.getDate().isAfter(LocalDate.now()) || 
                        (e.getDate().isEqual(LocalDate.now()) && 
                         e.getHeure().isAfter(LocalTime.now())))
            .count();
        
        long categories = filteredEvents.stream()
            .map(Evenement::getCategorie)
            .distinct()
            .count();
        
        totalEventsLabel.setText("Total: " + total + " événements");
        upcomingEventsLabel.setText("À venir: " + upcoming + " événements");
        categoryStatsLabel.setText(categories + " catégories");
    }
    
    // --- View Management ---
    private void refreshCurrentView() {
        int currentTabIndex = viewTabPane.getSelectionModel().getSelectedIndex();
        switch (currentTabIndex) {
            case 0:
                updateCalendarView();
                break;
            case 1:
                updateListView();
                break;
            case 2:
                updateAgendaView();
                break;
        }
    }
    
 
    private void initializeCalendarView() {
   
        updateCalendarView();
    }
    
    private void updateCalendarView() {
        monthYearLabel.setText(currentYearMonth.format(monthYearFormatter));
        moisComboBox.setValue(currentYearMonth.getMonth());
        anneeComboBox.setValue(currentYearMonth.getYear());
        
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();
        calendarGrid.getColumnConstraints().clear();
        
        // Set up columns for days of the week
        for (int i = 0; i < 7; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / 7);
            colConstraints.setHalignment(HPos.CENTER);
            calendarGrid.getColumnConstraints().add(colConstraints);
        }
        
        // Get first day of month and adjust to weekfields
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekValue = firstDayOfMonth.get(weekFields.dayOfWeek()) - 1; // 0-based day of week
        
      
        int weeksInMonth = (int) Math.ceil((dayOfWeekValue + currentYearMonth.lengthOfMonth()) / 7.0);
        
        for (int i = 0; i < weeksInMonth; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(80); 
            rowConstraints.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rowConstraints);
        }
        
      
        LocalDate date = firstDayOfMonth.minusDays(dayOfWeekValue);
        
        for (int week = 0; week < weeksInMonth; week++) {
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                StackPane dayCell = createDayCell(date);
                calendarGrid.add(dayCell, dayOfWeek, week);
                
                date = date.plusDays(1);
            }
        }
    }
    
    private StackPane createDayCell(LocalDate date) {
        StackPane dayCell = new StackPane();
        dayCell.getStyleClass().add("calendar-day-cell");
        
      
        boolean isCurrentMonth = date.getMonth() == currentYearMonth.getMonth();
        boolean isToday = date.equals(LocalDate.now());
        
        if (!isCurrentMonth) {
            dayCell.getStyleClass().add("other-month-day");
        }
        
        if (isToday) {
            dayCell.getStyleClass().add("today");
        }
        
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayCell.getStyleClass().add("weekend-day");
        }
        
     
        Label dayNumberLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumberLabel.getStyleClass().add("day-number");
        
        
        VBox eventsContainer = new VBox(2);
        eventsContainer.setPadding(new Insets(20, 2, 2, 2));
        eventsContainer.setAlignment(Pos.TOP_CENTER);
  
        List<Evenement> dayEvents = getEventsForDate(date);
        
     
        int maxEventsToShow = 3;
        int countToShow = Math.min(dayEvents.size(), maxEventsToShow);
        
        for (int i = 0; i < countToShow; i++) {
            Evenement event = dayEvents.get(i);
            eventsContainer.getChildren().add(createEventIndicator(event));
        }
        
     
        if (dayEvents.size() > maxEventsToShow) {
            Label moreLabel = new Label("+" + (dayEvents.size() - maxEventsToShow) + " plus...");
            moreLabel.getStyleClass().addAll("event-more-label");
            eventsContainer.getChildren().add(moreLabel);
        }
        
  
        BorderPane cellContent = new BorderPane();
        cellContent.setTop(dayNumberLabel);
        BorderPane.setAlignment(dayNumberLabel, Pos.TOP_RIGHT);
        BorderPane.setMargin(dayNumberLabel, new Insets(2));
        cellContent.setCenter(eventsContainer);
        
        dayCell.getChildren().add(cellContent);
        
   
        final LocalDate clickedDate = date;
        dayCell.setOnMouseClicked(e -> showDayDetails(clickedDate));
        
        return dayCell;
    }
    
    private Node createEventIndicator(Evenement event) {
        HBox eventIndicator = new HBox(5);
        eventIndicator.setPadding(new Insets(2, 5, 2, 5));
        eventIndicator.getStyleClass().addAll("event-indicator");
        
        // Add category-specific styling
        String categoryClass = categoryStyleClasses.getOrDefault(event.getCategorie(), "event-category-other");
        eventIndicator.getStyleClass().add(categoryClass);
        
        // Event time
        Label timeLabel = new Label(event.getHeure().format(timeFormatter));
        timeLabel.getStyleClass().add("event-time");
        
        // Event title (truncated if needed)
        String displayTitle = event.getTitre();
        if (displayTitle.length() > 15) {
            displayTitle = displayTitle.substring(0, 13) + "...";
        }
        Label titleLabel = new Label(displayTitle);
        titleLabel.getStyleClass().add("event-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        eventIndicator.getChildren().addAll(timeLabel, titleLabel);
        
        // Click handler to show event details
        eventIndicator.setOnMouseClicked(e -> {
            showEventDetails(event);
            e.consume(); // Prevent day cell click event
        });
        
        return eventIndicator;
    }
    
    private List<Evenement> getEventsForDate(LocalDate date) {
        return filteredEvents.stream()
            .filter(e -> e.getDate().equals(date))
            .sorted(Comparator.comparing(Evenement::getHeure))
            .collect(Collectors.toList());
    }
    
    private void navigateMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateCalendarView();
    }
    
    private void goToToday() {
        currentYearMonth = YearMonth.from(LocalDate.now());
        updateCalendarView();
    }
    
    private void showDayDetails(LocalDate date) {
        // Implement day details dialog or switch to day view
        System.out.println("Day details for: " + date.format(dateFormatter));
    }
    
    // --- List View ---
    private void updateListView() {
        eventsListContainer.getChildren().clear();
        
        // Create sorted copy based on selected sort option
        List<Evenement> sortedEvents = new ArrayList<>(filteredEvents);
        
        String sortOption = sortComboBox.getValue();
        if (sortOption != null) {
            switch (sortOption) {
                case "Date (croissant)":
                    sortedEvents.sort(Comparator.comparing(Evenement::getDate)
                                     .thenComparing(Evenement::getHeure));
                    break;
                case "Date (décroissant)":
                    sortedEvents.sort(Comparator.comparing(Evenement::getDate)
                                     .thenComparing(Evenement::getHeure)
                                     .reversed());
                    break;
                case "Catégorie":
                    sortedEvents.sort(Comparator.comparing(Evenement::getCategorie)
                                     .thenComparing(Evenement::getDate)
                                     .thenComparing(Evenement::getHeure));
                    break;
                case "Titre":
                    sortedEvents.sort(Comparator.comparing(Evenement::getTitre)
                                     .thenComparing(Evenement::getDate));
                    break;
            }
        }
        
        if (sortedEvents.isEmpty()) {
            Label noEventsLabel = new Label("Aucun événement trouvé");
            noEventsLabel.getStyleClass().add("no-events-label");
            eventsListContainer.getChildren().add(noEventsLabel);
        } else {
            for (Evenement event : sortedEvents) {
                eventsListContainer.getChildren().add(createEventListItem(event));
            }
        }
    }
    
    private Node createEventListItem(Evenement event) {
        HBox eventItem = new HBox(10);
        eventItem.getStyleClass().add("event-list-item");
        eventItem.setPadding(new Insets(10));
        
        // Date/time column
        VBox dateTimeBox = new VBox(5);
        dateTimeBox.setAlignment(Pos.CENTER);
        dateTimeBox.getStyleClass().add("event-date-box");
        
        Label dayLabel = new Label(String.valueOf(event.getDate().getDayOfMonth()));
        dayLabel.getStyleClass().add("event-day");
        
        Label monthLabel = new Label(event.getDate().getMonth().getDisplayName(TextStyle.SHORT, locale));
        monthLabel.getStyleClass().add("event-month");
        
        Label timeLabel = new Label(event.getHeure().format(timeFormatter));
        timeLabel.getStyleClass().add("event-time");
        
        dateTimeBox.getChildren().addAll(dayLabel, monthLabel, timeLabel);
        
        // Event details column
        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);
        
        Label titleLabel = new Label(event.getTitre());
        titleLabel.getStyleClass().add("event-title");
        
        HBox categoryBox = new HBox(5);
        categoryBox.setAlignment(Pos.CENTER_LEFT);
        
        Label categoryLabel = new Label(event.getCategorie());
        categoryLabel.getStyleClass().add("event-category");
        categoryLabel.getStyleClass().add(categoryStyleClasses.getOrDefault(event.getCategorie(), "event-category-other"));
        
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            Label descLabel = new Label(event.getDescription());
            descLabel.getStyleClass().add("event-description");
            descLabel.setWrapText(true);
            detailsBox.getChildren().addAll(titleLabel, categoryBox, descLabel);
        } else {
            detailsBox.getChildren().addAll(titleLabel, categoryBox);
        }
        
        categoryBox.getChildren().add(categoryLabel);
        
        eventItem.getChildren().addAll(dateTimeBox, detailsBox);
        
        // Click handler
        eventItem.setOnMouseClicked(e -> showEventDetails(event));
        
        return eventItem;
    }
    
    // --- Agenda View ---
    private void initializeAgendaView() {
        updateAgendaView();
    }
    
    private void updateAgendaView() {
        weekLabel.setText(currentWeekStart.format(weekFormatter));
        weekGrid.getChildren().clear();
        weekGrid.getRowConstraints().clear();
        weekGrid.getColumnConstraints().clear();

        // Set up columns for days of the week
        for (int i = 0; i < 7; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / 7);
            weekGrid.getColumnConstraints().add(colConstraints);
        }

        // Add day headers row (row 0)
        for (int day = 0; day < 7; day++) {
            LocalDate date = currentWeekStart.plusDays(day);
            Label dayLabel = new Label(date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale) + 
                           "\n" + date.getDayOfMonth() + " " + 
                           date.getMonth().getDisplayName(TextStyle.SHORT, locale));
            dayLabel.getStyleClass().add("day-header");
            if (date.equals(LocalDate.now())) {
                dayLabel.getStyleClass().add("today-header");
            }
            weekGrid.add(dayLabel, day, 0); // Changed from -1 to 0 for row index
        }

        // Set up rows for hours (starting from row 1)
        for (int hour = 0; hour < 24; hour++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(60);
            weekGrid.getRowConstraints().add(rowConstraints);
            
            // Add hour label (row hour + 1 because row 0 is for day headers)
            Label hourLabel = new Label(String.format("%02d:00", hour));
            hourLabel.getStyleClass().add("hour-label");
            weekGrid.add(hourLabel, 0, hour + 1);
        }

        // Add events to the grid (starting from row 1)
        for (int day = 0; day < 7; day++) {
            LocalDate date = currentWeekStart.plusDays(day);
            List<Evenement> dayEvents = getEventsForDate(date);
            
            for (Evenement event : dayEvents) {
                addEventToWeekGrid(event, day);
            }
        }
    }
    
    private void addEventToWeekGrid(Evenement event, int dayColumn) {
        int startHour = event.getHeure().getHour();
        int startMinute = event.getHeure().getMinute();
        
        // Calculate row (add 1 to account for header row)
        int row = startHour + 1;
        
        Button eventButton = new Button(event.getTitre());
        eventButton.getStyleClass().add("week-event");
        eventButton.getStyleClass().add(categoryStyleClasses.getOrDefault(event.getCategorie(), "event-category-other"));
        eventButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        Tooltip tooltip = new Tooltip();
        tooltip.setText(String.format("%s\n%s - %s\n%s", 
            event.getTitre(),
            event.getDate().format(dateFormatter),
            event.getHeure().format(timeFormatter),
            event.getCategorie()));
        eventButton.setTooltip(tooltip);
        
        eventButton.setOnAction(e -> showEventDetails(event));
        
        weekGrid.add(eventButton, dayColumn, row, 1, 1);
    }
    private void navigateWeek(int weeks) {
        currentWeekStart = currentWeekStart.plusWeeks(weeks);
        updateAgendaView();
    }
    
    private void goToCurrentWeek() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateAgendaView();
    }
    
    // --- Event Details ---
    private void showEventDetails(Evenement event) {
        // Implement event details dialog
        System.out.println("Showing details for: " + event.getTitre());
    }
    
    // --- Utility Methods ---
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}