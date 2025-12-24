package gamer_hub.controllers;

import gamer_hub.model.Tournament;
import gamer_hub.model.Game;
import gamer_hub.model.User;
import gamer_hub.service.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TournamentsController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private VBox myTournamentsContainer;
    @FXML private VBox participatingTournamentsContainer;
    @FXML private VBox upcomingTournamentsContainer;
    @FXML private VBox createTournamentContainer;
    
    @FXML private ComboBox<Game> gameComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField prizeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private CheckBox isGlobalCheckBox;
    @FXML private Label formStatusLabel;
    
    private Stage tournamentsStage;
    private User currentUser;
    private DateTimeFormatter dateFormatter;
    
    public void setStage(Stage stage) {
        this.tournamentsStage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✓ Окно турниров инициализировано");
        
        dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        currentUser = DataManager.getCurrentUser();
        
        if (currentUser != null) {
            System.out.println("  Пользователь: " + currentUser.getUsername());
            
            // Инициализируем все вкладки
            initializeCreateTab();
            loadMyTournaments();
            loadParticipatingTournaments();
            loadUpcomingTournaments();
        }
    }
    
    private void initializeCreateTab() {
        createTournamentContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        VBox formContainer = new VBox();
        formContainer.setSpacing(15);
        formContainer.setStyle("-fx-background-color: #423738; -fx-background-radius: 10; -fx-padding: 20;");
        
        // Заголовок формы
        Label formTitle = new Label("➕ Создать новый турнир");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        // Поле: Игра
        HBox gameRow = new HBox();
        gameRow.setSpacing(10);
        gameRow.setAlignment(Pos.CENTER_LEFT);
        
        Label gameLabel = new Label("Игра:");
        gameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-min-width: 100;");
        
        gameComboBox = new ComboBox<>();
        gameComboBox.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                            "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                            "-fx-background-radius: 5; -fx-padding: 8; -fx-pref-width: 300;");
        
        // Заполняем список играми, которые отслеживает пользователь
        List<Game> userGames = DataManager.getGamesByIds(currentUser.getTrackedGameIds());
        
        // Настраиваем отображение игр в ComboBox
        gameComboBox.setCellFactory(lv -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(game.getTitle() + " (" + game.getGenre() + ")");
                    setStyle("-fx-text-fill: #D3AF85; -fx-font-size: 13px;");
                }
            }
        });
        
        gameComboBox.setButtonCell(new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(game.getTitle());
                    setStyle("-fx-text-fill: #D3AF85; -fx-font-size: 14px;");
                }
            }
        });
        
        gameComboBox.getItems().addAll(userGames);
        
        if (!userGames.isEmpty()) {
            gameComboBox.getSelectionModel().select(0);
        } else {
            gameComboBox.setDisable(true);
            gameComboBox.setPromptText("Добавьте игры в главном окне");
        }
        
        gameRow.getChildren().addAll(gameLabel, gameComboBox);
        
        // Поле: Заголовок
        HBox titleRow = new HBox();
        titleRow.setSpacing(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Название:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-min-width: 100;");
        
        titleField = new TextField();
        titleField.setPromptText("Введите название турнира");
        titleField.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                          "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                          "-fx-background-radius: 5; -fx-padding: 8; -fx-pref-width: 300;");
        
        titleRow.getChildren().addAll(titleLabel, titleField);
        
        // Поле: Описание
        VBox descriptionRow = new VBox();
        descriptionRow.setSpacing(5);
        
        Label descriptionLabel = new Label("Описание:");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85;");
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Опишите турнир, правила, условия участия и т.д.");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                               "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                               "-fx-background-radius: 5; -fx-padding: 8;");
        
        descriptionRow.getChildren().addAll(descriptionLabel, descriptionArea);
        
        // Поле: Призовой фонд
        HBox prizeRow = new HBox();
        prizeRow.setSpacing(10);
        prizeRow.setAlignment(Pos.CENTER_LEFT);
        
        Label prizeLabel = new Label("Призовой фонд:");
        prizeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-min-width: 100;");
        
        prizeField = new TextField();
        prizeField.setPromptText("Например: 10000 руб");
        prizeField.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                          "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                          "-fx-background-radius: 5; -fx-padding: 8; -fx-pref-width: 300;");
        
        prizeRow.getChildren().addAll(prizeLabel, prizeField);
        
        // Поля: Дата начала и окончания
        HBox dateRow = new HBox();
        dateRow.setSpacing(20);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        
        VBox startDateBox = new VBox();
        startDateBox.setSpacing(5);
        
        Label startDateLabel = new Label("Дата начала:");
        startDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85;");
        
        startDatePicker = new DatePicker();
        startDatePicker.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                               "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                               "-fx-background-radius: 5;");
        
        startDateBox.getChildren().addAll(startDateLabel, startDatePicker);
        
        VBox endDateBox = new VBox();
        endDateBox.setSpacing(5);
        
        Label endDateLabel = new Label("Дата окончания:");
        endDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85;");
        
        endDatePicker = new DatePicker();
        endDatePicker.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                             "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                             "-fx-background-radius: 5;");
        
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);
        
        dateRow.getChildren().addAll(startDateBox, endDateBox);
        
        // Чекбокс: Глобальный турнир
        HBox globalRow = new HBox();
        globalRow.setSpacing(10);
        globalRow.setAlignment(Pos.CENTER_LEFT);
        
        isGlobalCheckBox = new CheckBox("Глобальный турнир (доступен всем пользователям)");
        isGlobalCheckBox.setStyle("-fx-text-fill: #D3AF85; -fx-font-size: 14px;");
        isGlobalCheckBox.setSelected(true);
        
        globalRow.getChildren().add(isGlobalCheckBox);
        
        // Кнопка создания
        Button createButton = new Button("Создать турнир");
        createButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                            "-fx-background-radius: 5; -fx-padding: 10 30; -fx-font-size: 14px; " +
                            "-fx-font-weight: bold; -fx-cursor: hand;");
        createButton.setOnAction(e -> onCreateTournamentClick());
        
        // Делаем кнопку неактивной, если нет игр
        if (userGames.isEmpty()) {
            createButton.setDisable(true);
            createButton.setTooltip(new Tooltip("Добавьте игры в главном окне"));
        }
        
        // Статус формы
        formStatusLabel = new Label();
        formStatusLabel.setStyle("-fx-font-size: 13px;");
        formStatusLabel.setWrapText(true);
        
        formContainer.getChildren().addAll(
            formTitle, gameRow, titleRow, descriptionRow, prizeRow, dateRow, globalRow, createButton, formStatusLabel
        );
        
        createTournamentContainer.getChildren().add(formContainer);
    }
    
    private void loadMyTournaments() {
        myTournamentsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<Tournament> myTournaments = DataManager.getTournamentsByCreator(currentUser.getId());
        
        if (myTournaments.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "🏆",
                "Вы пока не создали ни одного турнира",
                "Создайте свой первый турнир во вкладке 'Создать турнир'"
            );
            myTournamentsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Мои турниры (" + myTournaments.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            myTournamentsContainer.getChildren().add(titleLabel);
            
            for (Tournament tournament : myTournaments) {
                VBox tournamentCard = createTournamentCard(tournament, true, false);
                myTournamentsContainer.getChildren().add(tournamentCard);
            }
        }
    }
    
    private void loadParticipatingTournaments() {
        participatingTournamentsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<Tournament> participatingTournaments = DataManager.getTournamentsByParticipant(currentUser.getId());
        
        if (participatingTournaments.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "✅",
                "Вы пока не участвуете ни в одном турнире",
                "Найдите интересные турниры во вкладке 'Предстоящие турниры'"
            );
            participatingTournamentsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Турниры, в которых я участвую (" + participatingTournaments.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            participatingTournamentsContainer.getChildren().add(titleLabel);
            
            for (Tournament tournament : participatingTournaments) {
                VBox tournamentCard = createTournamentCard(tournament, false, true);
                participatingTournamentsContainer.getChildren().add(tournamentCard);
            }
        }
    }
    
    private void loadUpcomingTournaments() {
        upcomingTournamentsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        // Получаем предстоящие турниры по играм пользователя
        List<Tournament> upcomingTournaments = new java.util.ArrayList<>();
        for (Integer gameId : currentUser.getTrackedGameIds()) {
            List<Tournament> gameTournaments = DataManager.getTournamentsForGame(gameId);
            
            // Фильтруем: только предстоящие, не созданные самим пользователем и не участвует
            for (Tournament tournament : gameTournaments) {
                if (tournament.getCreatorId() != currentUser.getId() && 
                    !tournament.getParticipantIds().contains(currentUser.getId()) &&
                    "upcoming".equals(tournament.getStatus())) {
                    upcomingTournaments.add(tournament);
                }
            }
        }
        
        // Сортируем по дате начала (сначала ближайшие)
        upcomingTournaments.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
        
        if (upcomingTournaments.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "🔍",
                "Нет предстоящих турниров по вашим играм",
                "Добавьте больше игр или проверьте позже"
            );
            upcomingTournamentsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Предстоящие турниры (" + upcomingTournaments.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            upcomingTournamentsContainer.getChildren().add(titleLabel);
            
            for (Tournament tournament : upcomingTournaments) {
                VBox tournamentCard = createUpcomingTournamentCard(tournament);
                upcomingTournamentsContainer.getChildren().add(tournamentCard);
            }
        }
    }
    
    private VBox createTournamentCard(Tournament tournament, boolean isOwner, boolean isParticipant) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        
        // Заголовок и статус
        HBox headerRow = new HBox();
        headerRow.setSpacing(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(tournament.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label statusLabel = new Label(getStatusText(tournament.getStatus()));
        statusLabel.setStyle(getTournamentStatusStyle(tournament.getStatus()));
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label(tournament.getStartDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        headerRow.getChildren().addAll(titleLabel, statusLabel, spacer, dateLabel);
        
        // Информация об игре
        Game game = DataManager.getGameById(tournament.getGameId());
        
        HBox gameRow = new HBox();
        gameRow.setSpacing(5);
        
        Label gameIcon = new Label("🎮");
        gameIcon.setStyle("-fx-font-size: 14px;");
        
        String gameText = game != null ? game.getTitle() : "Игра не найдена";
        Label gameLabel = new Label(gameText);
        gameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #8E5915;");
        
        String prizeText = tournament.getPrizeInfo() != null ? tournament.getPrizeInfo() : "Без призового фонда";
        Label prizeLabel = new Label(prizeText);
        prizeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        gameRow.getChildren().addAll(gameIcon, gameLabel, separator, prizeLabel);
        
        // Описание
        Label descriptionLabel = new Label(tournament.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Дополнительная информация
        HBox infoRow = new HBox();
        infoRow.setSpacing(15);
        
        Label participantsLabel = new Label("👥 Участников: " + tournament.getParticipantIds().size());
        participantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label globalLabel = new Label(tournament.isGlobal() ? "🌐 Глобальный" : "🏠 Локальный");
        globalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        infoRow.getChildren().addAll(participantsLabel, globalLabel);
        
        // Кнопки управления
        HBox buttonsRow = new HBox();
        buttonsRow.setSpacing(10);
        
        if (isOwner) {
            // Кнопки для создателя турнира
            Button editButton = new Button("Редактировать");
            editButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                              "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                              "-fx-cursor: hand;");
            editButton.setOnAction(e -> onEditTournamentClick(tournament));
            
            if ("upcoming".equals(tournament.getStatus())) {
                Button startButton = new Button("Начать турнир");
                startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #1A141A; " +
                                   "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                   "-fx-cursor: hand;");
                startButton.setOnAction(e -> onStartTournamentClick(tournament));
                buttonsRow.getChildren().add(startButton);
                
                Button cancelButton = new Button("Отменить");
                cancelButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #1A141A; " +
                                    "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                    "-fx-cursor: hand;");
                cancelButton.setOnAction(e -> onCancelTournamentClick(tournament));
                buttonsRow.getChildren().add(cancelButton);
            } else if ("ongoing".equals(tournament.getStatus())) {
                Button endButton = new Button("Завершить");
                endButton.setStyle("-fx-background-color: #E59312; -fx-text-fill: #1A141A; " +
                                 "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                 "-fx-cursor: hand;");
                endButton.setOnAction(e -> onEndTournamentClick(tournament));
                buttonsRow.getChildren().add(endButton);
            }
            
            Button participantsButton = new Button("Участники (" + tournament.getParticipantIds().size() + ")");
            participantsButton.setStyle("-fx-background-color: #423738; -fx-text-fill: #D3AF85; " +
                                      "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                                      "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                      "-fx-cursor: hand;");
            participantsButton.setOnAction(e -> onViewParticipantsClick(tournament));
            
            Button deleteButton = new Button("Удалить");
            deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #1A141A; " +
                                "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                "-fx-cursor: hand;");
            deleteButton.setOnAction(e -> onDeleteTournamentClick(tournament));
            
            HBox spacer2 = new HBox();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            
            buttonsRow.getChildren().addAll(editButton, participantsButton, spacer2, deleteButton);
        } else if (isParticipant) {
            // Кнопка для участника
            Button leaveButton = new Button("Покинуть турнир");
            leaveButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                               "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                               "-fx-cursor: hand;");
            leaveButton.setOnAction(e -> onLeaveTournamentClick(tournament));
            
            HBox spacer2 = new HBox();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            
            buttonsRow.getChildren().addAll(spacer2, leaveButton);
        }
        
        card.getChildren().addAll(headerRow, gameRow, descriptionLabel, infoRow, buttonsRow);
        return card;
    }
    
    private VBox createUpcomingTournamentCard(Tournament tournament) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        
        // Заголовок
        HBox headerRow = new HBox();
        headerRow.setSpacing(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(tournament.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label(tournament.getStartDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        headerRow.getChildren().addAll(titleLabel, spacer, dateLabel);
        
        // Информация об игре и призе
        Game game = DataManager.getGameById(tournament.getGameId());
        
        HBox infoRow = new HBox();
        infoRow.setSpacing(10);
        
        Label gameIcon = new Label("🎮");
        gameIcon.setStyle("-fx-font-size: 14px;");
        
        String gameText = game != null ? game.getTitle() : "Игра не найдена";
        Label gameLabel = new Label(gameText);
        gameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #8E5915;");
        
        String prizeText = tournament.getPrizeInfo() != null ? tournament.getPrizeInfo() : "Без призового фонда";
        Label prizeLabel = new Label(prizeText);
        prizeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        infoRow.getChildren().addAll(gameIcon, gameLabel, separator, prizeLabel);
        
        // Описание (укороченное)
        String shortDescription = tournament.getDescription();
        if (shortDescription.length() > 150) {
            shortDescription = shortDescription.substring(0, 150) + "...";
        }
        
        Label descriptionLabel = new Label(shortDescription);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Дополнительная информация
        HBox statsRow = new HBox();
        statsRow.setSpacing(15);
        
        Label participantsLabel = new Label("👥 Участников: " + tournament.getParticipantIds().size());
        participantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label globalLabel = new Label(tournament.isGlobal() ? "🌐 Глобальный" : "🏠 Локальный");
        globalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        statsRow.getChildren().addAll(participantsLabel, globalLabel);
        
        // Кнопка участия
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        
        Button joinButton = new Button("Участвовать");
        joinButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                          "-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-size: 12px; " +
                          "-fx-font-weight: bold; -fx-cursor: hand;");
        joinButton.setOnAction(e -> onJoinTournamentClick(tournament));
        
        buttonRow.getChildren().add(joinButton);
        
        card.getChildren().addAll(headerRow, infoRow, descriptionLabel, statsRow, buttonRow);
        return card;
    }
    
    private VBox createEmptyMessage(String icon, String title, String description) {
        VBox emptyMessage = new VBox();
        emptyMessage.setSpacing(10);
        emptyMessage.setAlignment(Pos.CENTER);
        emptyMessage.setPadding(new Insets(50, 20, 50, 20));
        emptyMessage.setStyle("-fx-background-color: #423738; -fx-background-radius: 10;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8E5915; -fx-wrap-text: true; -fx-text-alignment: center;");
        descLabel.setMaxWidth(400);
        
        emptyMessage.getChildren().addAll(iconLabel, titleLabel, descLabel);
        return emptyMessage;
    }
    
    // === ОБРАБОТЧИКИ СОБЫТИЙ ===
    
    @FXML
    private void onCreateTournamentClick() {
        if (currentUser == null) return;
        
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String prize = prizeField.getText().trim();
        Game selectedGame = gameComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        boolean isGlobal = isGlobalCheckBox.isSelected();
        
        // Валидация
        if (selectedGame == null) {
            formStatusLabel.setText("❌ Выберите игру");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (title.isEmpty()) {
            formStatusLabel.setText("❌ Введите название турнира");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (description.isEmpty()) {
            formStatusLabel.setText("❌ Введите описание турнира");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (startDate == null) {
            formStatusLabel.setText("❌ Выберите дату начала");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (endDate == null) {
            formStatusLabel.setText("❌ Выберите дату окончания");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            formStatusLabel.setText("❌ Дата начала не может быть позже даты окончания");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        // Преобразуем LocalDate в LocalDateTime
        LocalDateTime startDateTime = startDate.atTime(18, 0); // По умолчанию 18:00
        LocalDateTime endDateTime = endDate.atTime(23, 59); // По умолчанию 23:59
        
        // Создаем турнир
        Tournament tournament = DataManager.createTournament(
            title,
            selectedGame.getId(),
            description,
            startDateTime,
            endDateTime,
            prize,
            isGlobal,
            currentUser.getId()
        );
        
        if (tournament != null) {
            formStatusLabel.setText("✅ Турнир успешно создан!");
            formStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
            
            // Очищаем форму
            titleField.clear();
            descriptionArea.clear();
            prizeField.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            
            // Обновляем вкладки
            loadMyTournaments();
            
            // Переключаемся на вкладку "Мои турниры"
            mainTabPane.getSelectionModel().select(0);
        } else {
            formStatusLabel.setText("❌ Ошибка при создании турнира");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
        }
    }
    
    @FXML
    private void onEditTournamentClick(Tournament tournament) {
        System.out.println("Редактирование турнира: " + tournament.getTitle());
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Редактирование турнира находится в разработке");
    }
    
    @FXML
    private void onStartTournamentClick(Tournament tournament) {
        boolean success = DataManager.updateTournamentStatus(tournament.getId(), "ongoing");
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Турнир начат!");
            loadMyTournaments();
            loadParticipatingTournaments();
            loadUpcomingTournaments();
        }
    }
    
    @FXML
    private void onCancelTournamentClick(Tournament tournament) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение отмены");
        confirmAlert.setHeaderText("Вы уверены, что хотите отменить турнир?");
        confirmAlert.setContentText("Турнир: " + tournament.getTitle() + "\nЭто действие нельзя отменить.");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = DataManager.updateTournamentStatus(tournament.getId(), "cancelled");
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Турнир отменен");
                loadMyTournaments();
                loadParticipatingTournaments();
                loadUpcomingTournaments();
            }
        }
    }
    
    @FXML
    private void onEndTournamentClick(Tournament tournament) {
        boolean success = DataManager.updateTournamentStatus(tournament.getId(), "finished");
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Турнир завершен!");
            loadMyTournaments();
            loadParticipatingTournaments();
            loadUpcomingTournaments();
        }
    }
    
    @FXML
    private void onViewParticipantsClick(Tournament tournament) {
        System.out.println("Просмотр участников турнира: " + tournament.getTitle());
        
        if (tournament.getParticipantIds().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Участники", 
                     "В турнире пока нет участников");
        } else {
            StringBuilder participants = new StringBuilder("Участники:\n\n");
            for (Integer participantId : tournament.getParticipantIds()) {
                User participant = DataManager.getUserById(participantId);
                if (participant != null) {
                    participants.append("👤 ").append(participant.getUsername()).append("\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Участники (" + tournament.getParticipantIds().size() + ")", 
                     participants.toString());
        }
    }
    
    @FXML
    private void onDeleteTournamentClick(Tournament tournament) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Вы уверены, что хотите удалить турнир?");
        confirmAlert.setContentText("Турнир: " + tournament.getTitle() + "\nЭто действие нельзя отменить.");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = DataManager.deleteTournament(tournament.getId(), currentUser.getId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Турнир удален");
                loadMyTournaments();
                loadUpcomingTournaments();
            }
        }
    }
    
    @FXML
    private void onLeaveTournamentClick(Tournament tournament) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение выхода");
        confirmAlert.setHeaderText("Вы уверены, что хотите покинуть турнир?");
        confirmAlert.setContentText("Турнир: " + tournament.getTitle());
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = DataManager.leaveTournament(currentUser.getId(), tournament.getId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Вы вышли из турнира");
                loadParticipatingTournaments();
                loadUpcomingTournaments();
            }
        }
    }
    
    @FXML
    private void onJoinTournamentClick(Tournament tournament) {
        boolean success = DataManager.joinTournament(currentUser.getId(), tournament.getId());
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Вы вступили в турнир: " + tournament.getTitle());
            loadParticipatingTournaments();
            loadUpcomingTournaments();
        } else {
            showAlert(Alert.AlertType.WARNING, "Ошибка", 
                     "Не удалось вступить в турнир");
        }
    }
    
    @FXML
    private void onCloseClick() {
        if (tournamentsStage != null) {
            tournamentsStage.close();
        }
    }
    
    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    
    private String getStatusText(String status) {
        switch (status) {
            case "upcoming": return "Предстоящий";
            case "ongoing": return "Идет";
            case "finished": return "Завершен";
            case "cancelled": return "Отменен";
            default: return status;
        }
    }
    
    private String getTournamentStatusStyle(String status) {
        switch (status) {
            case "upcoming": 
                return "-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;";
            case "ongoing": 
                return "-fx-font-size: 12px; -fx-text-fill: #F4B315; -fx-font-weight: bold;";
            case "finished": 
                return "-fx-font-size: 12px; -fx-text-fill: #8E5915; -fx-font-weight: bold;";
            case "cancelled": 
                return "-fx-font-size: 12px; -fx-text-fill: #FF6B6B; -fx-font-weight: bold;";
            default: 
                return "-fx-font-size: 12px; -fx-text-fill: #D3AF85;";
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
