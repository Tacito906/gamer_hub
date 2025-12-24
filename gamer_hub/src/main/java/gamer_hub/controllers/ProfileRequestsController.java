package gamer_hub.controllers;

import gamer_hub.model.ProfileRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileRequestsController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private VBox myRequestsContainer;
    @FXML private VBox myApplicationsContainer;
    @FXML private VBox activeRequestsContainer;
    @FXML private VBox createRequestContainer;
    
    @FXML private ComboBox<Game> gameComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea messageArea;
    @FXML private Label formStatusLabel;
    
    private Stage profileRequestsStage;
    private User currentUser;
    private DateTimeFormatter dateFormatter;
    
    public void setStage(Stage stage) {
        this.profileRequestsStage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✓ Окно анкет инициализировано");
        
        dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        currentUser = DataManager.getCurrentUser();
        
        if (currentUser != null) {
            System.out.println("  Пользователь: " + currentUser.getUsername());
            
            // Инициализируем все вкладки
            initializeCreateTab();
            loadMyRequests();
            loadMyApplications();
            loadActiveRequests();
        }
    }
    
    private void initializeCreateTab() 
    {
        createRequestContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        VBox formContainer = new VBox();
        formContainer.setSpacing(15);
        formContainer.setStyle("-fx-background-color: #423738; -fx-background-radius: 10; -fx-padding: 20;");
        
        // Заголовок формы
        Label formTitle = new Label("➕ Создать новую анкету");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        // Поле: Игра
        HBox gameRow = new HBox();
        gameRow.setSpacing(10);
        gameRow.setAlignment(Pos.CENTER_LEFT);
        
        Label gameLabel = new Label("Игра:");
        gameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-min-width: 80;");
        
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
        
        // Настраиваем отображение выбранного элемента
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
            // Если нет отслеживаемых игр
            gameComboBox.setDisable(true);
            gameComboBox.setPromptText("Добавьте игры в главном окне");
        }
        
        gameRow.getChildren().addAll(gameLabel, gameComboBox);
        
        // Поле: Заголовок
        HBox titleRow = new HBox();
        titleRow.setSpacing(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Заголовок:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-min-width: 80;");
        
        titleField = new TextField();
        titleField.setPromptText("Например: Ищу команду для рейда");
        titleField.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                        "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8; -fx-pref-width: 300;");
        
        titleRow.getChildren().addAll(titleLabel, titleField);
        
        // Поле: Сообщение
        VBox messageRow = new VBox();
        messageRow.setSpacing(5);
        
        Label messageLabel = new Label("Сообщение:");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85;");
        
        messageArea = new TextArea();
        messageArea.setPromptText("Опишите подробно, что вы ищете, ваши требования, время проведения и т.д.");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(6);
        messageArea.setStyle("-fx-background-color: #1A141A; -fx-text-fill: #D3AF85; " +
                        "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                        "-fx-background-radius: 5; -fx-padding: 8;");
        
        messageRow.getChildren().addAll(messageLabel, messageArea);
        
        // Кнопка создания
        Button createButton = new Button("Создать анкету");
        createButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                            "-fx-background-radius: 5; -fx-padding: 10 30; -fx-font-size: 14px; " +
                            "-fx-font-weight: bold; -fx-cursor: hand;");
        createButton.setOnAction(e -> onCreateRequestClick());
        
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
            formTitle, gameRow, titleRow, messageRow, createButton, formStatusLabel
        );
        
        createRequestContainer.getChildren().add(formContainer);
    }
    
    private void loadMyRequests() 
    {
        myRequestsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<ProfileRequest> myRequests = DataManager.getProfileRequestsByAuthor(currentUser.getId());
        
        if (myRequests.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "📝",
                "У вас нет созданных анкет",
                "Создайте свою первую анкету во вкладке 'Создать анкету'"
            );
            myRequestsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Мои анкеты (" + myRequests.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            myRequestsContainer.getChildren().add(titleLabel);
            
            for (ProfileRequest request : myRequests) {
                VBox requestCard = createRequestCard(request, true);
                myRequestsContainer.getChildren().add(requestCard);
            }
        }
    }
    
    private void loadMyApplications() {
        myApplicationsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<ProfileRequest> myApplications = DataManager.getProfileRequestsByApplicant(currentUser.getId());
        
        if (myApplications.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "✅",
                "Вы еще не откликались на анкеты",
                "Найдите интересные анкеты во вкладке 'Активные анкеты'"
            );
            myApplicationsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Мои отклики (" + myApplications.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            myApplicationsContainer.getChildren().add(titleLabel);
            
            for (ProfileRequest request : myApplications) {
                VBox requestCard = createRequestCard(request, false);
                myApplicationsContainer.getChildren().add(requestCard);
            }
        }
    }
    
    private void loadActiveRequests() {
        activeRequestsContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        // Получаем активные анкеты по играм пользователя
        List<ProfileRequest> activeRequests = new java.util.ArrayList<>();
        for (Integer gameId : currentUser.getTrackedGameIds()) {
            List<ProfileRequest> gameRequests = DataManager.getProfileRequestsByGame(gameId);
            
            // Фильтруем: только открытые анкеты и не созданные самим пользователем
            for (ProfileRequest request : gameRequests) {
                if (request.getAuthorId() != currentUser.getId() && 
                    !request.getApplicantIds().contains(currentUser.getId())) {
                    activeRequests.add(request);
                }
            }
        }
        
        if (activeRequests.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "🔍",
                "Нет активных анкет по вашим играм",
                "Добавьте больше игр или проверьте позже"
            );
            activeRequestsContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Активные анкеты (" + activeRequests.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            activeRequestsContainer.getChildren().add(titleLabel);
            
            for (ProfileRequest request : activeRequests) {
                VBox requestCard = createActiveRequestCard(request);
                activeRequestsContainer.getChildren().add(requestCard);
            }
        }
    }
    
    private VBox createRequestCard(ProfileRequest request, boolean isOwner) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        
        // Заголовок и статус
        HBox headerRow = new HBox();
        headerRow.setSpacing(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(request.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label statusLabel = new Label(getStatusText(request.getStatus()));
        statusLabel.setStyle(getStatusStyle(request.getStatus()));
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label(request.getCreatedAt().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        headerRow.getChildren().addAll(titleLabel, statusLabel, spacer, dateLabel);
        
        // Информация об игре
        Game game = DataManager.getGameById(request.getGameId());
        User author = DataManager.getUserById(request.getAuthorId());
        
        HBox gameRow = new HBox();
        gameRow.setSpacing(5);
        
        Label gameIcon = new Label("🎮");
        gameIcon.setStyle("-fx-font-size: 14px;");
        
        String gameText = game != null ? game.getTitle() : "Игра не найдена";
        Label gameLabel = new Label(gameText);
        gameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #8E5915;");
        
        String authorText = author != null ? "Автор: " + author.getUsername() : "Автор неизвестен";
        Label authorLabel = new Label(authorText);
        authorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        gameRow.getChildren().addAll(gameIcon, gameLabel, separator, authorLabel);
        
        // Сообщение
        Label messageLabel = new Label(request.getMessage());
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Статистика
        HBox statsRow = new HBox();
        statsRow.setSpacing(15);
        
        Label applicantsLabel = new Label("👤 Откликов: " + request.getApplicantIds().size());
        applicantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        statsRow.getChildren().add(applicantsLabel);
        
        // Кнопки управления
        HBox buttonsRow = new HBox();
        buttonsRow.setSpacing(10);
        
        if (isOwner) {
            // Кнопки для владельца анкеты
            Button editButton = new Button("Редактировать");
            editButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                              "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                              "-fx-cursor: hand;");
            editButton.setOnAction(e -> onEditRequestClick(request));
            
            if ("open".equals(request.getStatus())) {
                Button closeButton = new Button("Закрыть");
                closeButton.setStyle("-fx-background-color: #E59312; -fx-text-fill: #1A141A; " +
                                   "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                   "-fx-cursor: hand;");
                closeButton.setOnAction(e -> onCloseRequestClick(request));
                buttonsRow.getChildren().add(closeButton);
            } else {
                Button openButton = new Button("Открыть");
                openButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                                  "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                  "-fx-cursor: hand;");
                openButton.setOnAction(e -> onOpenRequestClick(request));
                buttonsRow.getChildren().add(openButton);
            }
            
            Button applicantsButton = new Button("Отклики (" + request.getApplicantIds().size() + ")");
            applicantsButton.setStyle("-fx-background-color: #423738; -fx-text-fill: #D3AF85; " +
                                    "-fx-border-color: #8E5915; -fx-border-radius: 5; " +
                                    "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                    "-fx-cursor: hand;");
            applicantsButton.setOnAction(e -> onViewApplicantsClick(request));
            
            Button deleteButton = new Button("Удалить");
            deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #1A141A; " +
                                "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                "-fx-cursor: hand;");
            deleteButton.setOnAction(e -> onDeleteRequestClick(request));
            
            HBox spacer2 = new HBox();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            
            buttonsRow.getChildren().addAll(editButton, applicantsButton, spacer2, deleteButton);
        } else {
            // Кнопка для отзыва отклика
            Button cancelButton = new Button("Отозвать отклик");
            cancelButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                                "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                                "-fx-cursor: hand;");
            cancelButton.setOnAction(e -> onCancelApplicationClick(request));
            
            HBox spacer2 = new HBox();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            
            buttonsRow.getChildren().addAll(spacer2, cancelButton);
        }
        
        card.getChildren().addAll(headerRow, gameRow, messageLabel, statsRow, buttonsRow);
        return card;
    }
    
    private VBox createActiveRequestCard(ProfileRequest request) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        
        // Заголовок
        HBox headerRow = new HBox();
        headerRow.setSpacing(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(request.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label dateLabel = new Label(request.getCreatedAt().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        headerRow.getChildren().addAll(titleLabel, spacer, dateLabel);
        
        // Информация об игре и авторе
        Game game = DataManager.getGameById(request.getGameId());
        User author = DataManager.getUserById(request.getAuthorId());
        
        HBox infoRow = new HBox();
        infoRow.setSpacing(10);
        
        Label gameIcon = new Label("🎮");
        gameIcon.setStyle("-fx-font-size: 14px;");
        
        String gameText = game != null ? game.getTitle() : "Игра не найдена";
        Label gameLabel = new Label(gameText);
        gameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #8E5915;");
        
        String authorText = author != null ? "Автор: " + author.getUsername() : "Автор неизвестен";
        Label authorLabel = new Label(authorText);
        authorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        infoRow.getChildren().addAll(gameIcon, gameLabel, separator, authorLabel);
        
        // Сообщение (укороченное)
        String shortMessage = request.getMessage();
        if (shortMessage.length() > 150) {
            shortMessage = shortMessage.substring(0, 150) + "...";
        }
        
        Label messageLabel = new Label(shortMessage);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Статистика
        HBox statsRow = new HBox();
        statsRow.setSpacing(15);
        
        Label applicantsLabel = new Label("👤 Откликов: " + request.getApplicantIds().size());
        applicantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        statsRow.getChildren().add(applicantsLabel);
        
        // Кнопка отклика
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        
        Button applyButton = new Button("Откликнуться");
        applyButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                           "-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-size: 12px; " +
                           "-fx-font-weight: bold; -fx-cursor: hand;");
        applyButton.setOnAction(e -> onApplyToRequestClick(request));
        
        buttonRow.getChildren().add(applyButton);
        
        card.getChildren().addAll(headerRow, infoRow, messageLabel, statsRow, buttonRow);
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
    private void onCreateRequestClick() {
        if (currentUser == null) return;
        
        String title = titleField.getText().trim();
        String message = messageArea.getText().trim();
        Game selectedGame = gameComboBox.getValue();
        
        // Валидация
        if (selectedGame == null) {
            formStatusLabel.setText("❌ Выберите игру");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (title.isEmpty()) {
            formStatusLabel.setText("❌ Введите заголовок анкеты");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        if (message.isEmpty()) {
            formStatusLabel.setText("❌ Введите описание анкеты");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        // Создаем анкету
        ProfileRequest request = DataManager.createProfileRequest(
            currentUser.getId(),
            selectedGame.getId(),
            title,
            message
        );
        
        if (request != null) {
            formStatusLabel.setText("✅ Анкета успешно создана!");
            formStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
            
            // Очищаем форму
            titleField.clear();
            messageArea.clear();
            
            // Обновляем вкладки
            loadMyRequests();
            
            // Переключаемся на вкладку "Мои анкеты"
            mainTabPane.getSelectionModel().select(0);
        } else {
            formStatusLabel.setText("❌ Ошибка при создании анкеты");
            formStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
        }
    }
    
    @FXML
    private void onEditRequestClick(ProfileRequest request) {
        System.out.println("Редактирование анкеты: " + request.getTitle());
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Редактирование анкеты находится в разработке");
    }
    
    @FXML
    private void onCloseRequestClick(ProfileRequest request) {
        boolean success = DataManager.updateProfileRequestStatus(request.getId(), "closed");
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Анкета закрыта для новых откликов");
            loadMyRequests();
        }
    }
    
    @FXML
    private void onOpenRequestClick(ProfileRequest request) {
        boolean success = DataManager.updateProfileRequestStatus(request.getId(), "open");
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Анкета открыта для новых откликов");
            loadMyRequests();
        }
    }
    
    @FXML
    private void onViewApplicantsClick(ProfileRequest request) {
        System.out.println("Просмотр откликов на анкету: " + request.getTitle());
        
        if (request.getApplicantIds().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Отклики", 
                     "На вашу анкету пока никто не откликнулся");
        } else {
            StringBuilder applicants = new StringBuilder("Откликнулись:\n\n");
            for (Integer applicantId : request.getApplicantIds()) {
                User applicant = DataManager.getUserById(applicantId);
                if (applicant != null) {
                    applicants.append("👤 ").append(applicant.getUsername()).append("\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Отклики (" + request.getApplicantIds().size() + ")", 
                     applicants.toString());
        }
    }
    
    @FXML
    private void onDeleteRequestClick(ProfileRequest request) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Вы уверены, что хотите удалить анкету?");
        confirmAlert.setContentText("Анкета: " + request.getTitle() + "\nЭто действие нельзя отменить.");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = DataManager.deleteProfileRequest(request.getId(), currentUser.getId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Анкета удалена");
                loadMyRequests();
            }
        }
    }
    
    @FXML
    private void onCancelApplicationClick(ProfileRequest request) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Подтверждение отзыва");
        confirmAlert.setHeaderText("Вы уверены, что хотите отозвать отклик?");
        confirmAlert.setContentText("Анкета: " + request.getTitle());
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = DataManager.cancelApplication(currentUser.getId(), request.getId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Отклик отозван");
                loadMyApplications();
                loadActiveRequests();
            }
        }
    }
    
    @FXML
    private void onApplyToRequestClick(ProfileRequest request) {
        boolean success = DataManager.applyToProfileRequest(currentUser.getId(), request.getId());
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                     "Вы откликнулись на анкету: " + request.getTitle());
            loadMyApplications();
            loadActiveRequests();
        } else {
            showAlert(Alert.AlertType.WARNING, "Ошибка", 
                     "Не удалось откликнуться на анкету");
        }
    }
    
    @FXML
    private void onCloseClick() {
        if (profileRequestsStage != null) {
            profileRequestsStage.close();
        }
    }
    
    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    
    private String getStatusText(String status) {
        switch (status) {
            case "open": return "Открыта";
            case "closed": return "Закрыта";
            case "fulfilled": return "Завершена";
            default: return status;
        }
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "open": 
                return "-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;";
            case "closed": 
                return "-fx-font-size: 12px; -fx-text-fill: #FF9800; -fx-font-weight: bold;";
            case "fulfilled": 
                return "-fx-font-size: 12px; -fx-text-fill: #8E5915; -fx-font-weight: bold;";
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