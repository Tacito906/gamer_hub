package gamer_hub.controllers;

import gamer_hub.model.Community;
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
import java.util.List;
import java.util.ResourceBundle;

public class CommunitiesController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private VBox myCommunitiesContainer;
    @FXML private VBox recommendedCommunitiesContainer;
    @FXML private VBox allCommunitiesContainer;
    @FXML private VBox manageCommunitiesContainer;
    
    private Stage communitiesStage;
    private User currentUser;
    
    public void setStage(Stage stage) {
        this.communitiesStage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✓ Окно сообществ инициализировано");
        
        currentUser = DataManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("  Пользователь: " + currentUser.getUsername());
            System.out.println("  Сообществ: " + currentUser.getCommunityIds().size());
            
            // Загружаем все вкладки
            loadMyCommunities();
            loadRecommendedCommunities();
            loadAllCommunities();
            loadManageTab();
        }
    }
    
    private void loadMyCommunities() {
        myCommunitiesContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<Community> userCommunities = DataManager.getUserCommunities(currentUser.getId());
        
        if (userCommunities.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "👥",
                "Вы пока не состоите ни в одном сообществе",
                "Найдите интересные сообщества во вкладке 'Найти сообщества'"
            );
            myCommunitiesContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Ваши сообщества (" + userCommunities.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            myCommunitiesContainer.getChildren().add(titleLabel);
            
            for (Community community : userCommunities) {
                HBox communityCard = createCommunityCard(community, true);
                myCommunitiesContainer.getChildren().add(communityCard);
            }
        }
    }
    
    private void loadRecommendedCommunities() {
        recommendedCommunitiesContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        List<Community> recommendedCommunities = DataManager.getRecommendedCommunities(currentUser.getId());
        
        if (recommendedCommunities.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "✨",
                "Нет рекомендованных сообществ",
                "Добавьте больше игр в отслеживаемые, чтобы видеть рекомендации"
            );
            recommendedCommunitiesContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Рекомендованные сообщества (" + recommendedCommunities.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            recommendedCommunitiesContainer.getChildren().add(titleLabel);
            
            for (Community community : recommendedCommunities) {
                HBox communityCard = createCommunityCard(community, false);
                recommendedCommunitiesContainer.getChildren().add(communityCard);
            }
        }
    }
    
    private void loadAllCommunities() {
        allCommunitiesContainer.getChildren().clear();
        
        List<Community> allCommunities = DataManager.getAllCommunities();
        
        if (allCommunities.isEmpty()) {
            VBox emptyMessage = createEmptyMessage(
                "🌐",
                "В системе пока нет сообществ",
                "Будьте первым, кто создаст сообщество!"
            );
            allCommunitiesContainer.getChildren().add(emptyMessage);
        } else {
            Label titleLabel = new Label("Все сообщества (" + allCommunities.size() + ")");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            allCommunitiesContainer.getChildren().add(titleLabel);
            
            for (Community community : allCommunities) {
                boolean isMember = currentUser != null && 
                                 currentUser.getCommunityIds().contains(community.getId());
                HBox communityCard = createCommunityCard(community, isMember);
                allCommunitiesContainer.getChildren().add(communityCard);
            }
        }
    }
    
    private void loadManageTab() {
        manageCommunitiesContainer.getChildren().clear();
        
        if (currentUser == null) return;
        
        VBox manageContent = new VBox();
        manageContent.setSpacing(20);
        
        // Создание нового сообщества
        VBox createSection = new VBox();
        createSection.setSpacing(10);
        createSection.setStyle("-fx-background-color: #423738; -fx-background-radius: 10; -fx-padding: 20;");
        
        Label createTitle = new Label("➕ Создать новое сообщество");
        createTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label createDesc = new Label("Создайте сообщество для вашей любимой игры");
        createDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        
        Button createButton = new Button("Создать сообщество");
        createButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                            "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 14px; " +
                            "-fx-font-weight: bold; -fx-cursor: hand;");
        createButton.setOnAction(e -> onCreateCommunityClick());
        
        createSection.getChildren().addAll(createTitle, createDesc, createButton);
        
        // Администрируемые сообщества
        List<Community> adminCommunities = getAdminCommunities();
        
        if (!adminCommunities.isEmpty()) {
            VBox adminSection = new VBox();
            adminSection.setSpacing(10);
            adminSection.setStyle("-fx-background-color: #423738; -fx-background-radius: 10; -fx-padding: 20;");
            
            Label adminTitle = new Label("👑 Администрируемые сообщества (" + adminCommunities.size() + ")");
            adminTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            
            adminSection.getChildren().add(adminTitle);
            
            for (Community community : adminCommunities) {
                HBox adminCommunityCard = createAdminCommunityCard(community);
                adminSection.getChildren().add(adminCommunityCard);
            }
            
            manageContent.getChildren().addAll(createSection, adminSection);
        } else {
            manageContent.getChildren().add(createSection);
            
            Label noAdminLabel = new Label("Вы не администрируете ни одного сообщества");
            noAdminLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8E5915; -fx-padding: 10;");
            manageContent.getChildren().add(noAdminLabel);
        }
        
        manageCommunitiesContainer.getChildren().add(manageContent);
    }
    
    private List<Community> getAdminCommunities() {
        List<Community> adminCommunities = new java.util.ArrayList<>();
        
        if (currentUser == null) return adminCommunities;
        
        for (Community community : DataManager.getAllCommunities()) {
            if (community.getAdminId() == currentUser.getId()) {
                adminCommunities.add(community);
            }
        }
        
        return adminCommunities;
    }
    
    private HBox createCommunityCard(Community community, boolean isMember) {
        HBox card = new HBox();
        card.setSpacing(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Иконка сообщества
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setMinWidth(50);
        
        Label iconLabel = new Label("👥");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        iconContainer.getChildren().add(iconLabel);
        
        // Информация о сообществе
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        
        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        // Получаем игру сообщества
        Game game = DataManager.getGameById(community.getGameId());
        String gameInfo = game != null ? "Игра: " + game.getTitle() : "Игра не найдена";
        
        Label gameLabel = new Label(gameInfo);
        gameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label descLabel = new Label(community.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        descLabel.setMaxWidth(400);
        
        HBox statsContainer = new HBox();
        statsContainer.setSpacing(10);
        
        Label membersLabel = new Label("👤 " + community.getMemberIds().size() + " участников");
        membersLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label privacyLabel = new Label(community.isPublic() ? "🌐 Открытое" : "🔒 Закрытое");
        privacyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        statsContainer.getChildren().addAll(membersLabel, privacyLabel);
        
        infoContainer.getChildren().addAll(nameLabel, gameLabel, descLabel, statsContainer);
        
        // Кнопка действия
        HBox actionContainer = new HBox();
        actionContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(actionContainer, Priority.ALWAYS);
        
        Button actionButton;
        
        if (isMember) {
            actionButton = new Button("Выйти");
            actionButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                                "-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-size: 12px; " +
                                "-fx-font-weight: bold; -fx-cursor: hand;");
            actionButton.setOnAction(e -> onLeaveCommunityClick(community));
        } else {
            actionButton = new Button("Вступить");
            actionButton.setStyle("-fx-background-color: #F4B315; -fx-text-fill: #1A141A; " +
                                "-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-size: 12px; " +
                                "-fx-font-weight: bold; -fx-cursor: hand;");
            actionButton.setOnAction(e -> onJoinCommunityClick(community));
        }
        
        actionContainer.getChildren().add(actionButton);
        
        card.getChildren().addAll(iconContainer, infoContainer, actionContainer);
        return card;
    }
    
    private HBox createAdminCommunityCard(Community community) {
        HBox card = new HBox();
        card.setSpacing(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                     "-fx-border-color: #F4B315; -fx-border-radius: 10; -fx-border-width: 1;");
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Иконка
        Label iconLabel = new Label("👑");
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        // Информация
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        
        Label nameLabel = new Label(community.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label statsLabel = new Label("Участников: " + community.getMemberIds().size() + 
                                   " | Сообщений: " + getRandomCount());
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        infoContainer.getChildren().addAll(nameLabel, statsLabel);
        
        // Кнопки управления
        HBox buttonsContainer = new HBox();
        buttonsContainer.setSpacing(10);
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonsContainer, Priority.ALWAYS);
        
        Button membersButton = new Button("Участники");
        membersButton.setStyle("-fx-background-color: #8E5915; -fx-text-fill: #D3AF85; " +
                             "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                             "-fx-cursor: hand;");
        membersButton.setOnAction(e -> onManageMembersClick(community));
        
        Button settingsButton = new Button("Настройки");
        settingsButton.setStyle("-fx-background-color: #423738; -fx-text-fill: #D3AF85; " +
                              "-fx-border-color: #8E5915; -fx-border-radius: 5; -fx-background-radius: 5; " +
                              "-fx-padding: 5 15; -fx-font-size: 12px; -fx-cursor: hand;");
        settingsButton.setOnAction(e -> onCommunitySettingsClick(community));
        
        Button deleteButton = new Button("Удалить");
        deleteButton.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: #1A141A; " +
                            "-fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px; " +
                            "-fx-cursor: hand;");
        deleteButton.setOnAction(e -> onDeleteCommunityClick(community));
        
        buttonsContainer.getChildren().addAll(membersButton, settingsButton, deleteButton);
        
        card.getChildren().addAll(iconLabel, infoContainer, buttonsContainer);
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
    
    @FXML
    private void onJoinCommunityClick(Community community) {
        if (currentUser != null) {
            boolean success = DataManager.joinCommunity(currentUser.getId(), community.getId());
            
            if (success) {
                System.out.println("✓ Вступление в сообщество: " + community.getName());
                showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                         "Вы успешно вступили в сообщество: " + community.getName());
                
                // Обновляем все вкладки
                refreshAllTabs();
            } else {
                showAlert(Alert.AlertType.WARNING, "Ошибка", 
                         "Не удалось вступить в сообщество");
            }
        }
    }
    
    @FXML
    private void onLeaveCommunityClick(Community community) {
        if (currentUser != null) {
            // Подтверждение выхода
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение выхода");
            confirmAlert.setHeaderText("Вы уверены, что хотите покинуть сообщество?");
            confirmAlert.setContentText("Сообщество: " + community.getName());
            
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = DataManager.leaveCommunity(currentUser.getId(), community.getId());
                
                if (success) {
                    System.out.println("✓ Выход из сообщества: " + community.getName());
                    showAlert(Alert.AlertType.INFORMATION, "Успех!", 
                             "Вы вышли из сообщества: " + community.getName());
                    
                    // Обновляем все вкладки
                    refreshAllTabs();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Ошибка", 
                             "Не удалось выйти из сообщества. Возможно, вы администратор.");
                }
            }
        }
    }
    
    @FXML
    private void onCreateCommunityClick() {
        System.out.println("Создание нового сообщества");
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Функция создания сообщества находится в разработке");
    }
    
    @FXML
    private void onManageMembersClick(Community community) {
        System.out.println("Управление участниками сообщества: " + community.getName());
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Управление участниками находится в разработке");
    }
    
    @FXML
    private void onCommunitySettingsClick(Community community) {
        System.out.println("Настройки сообщества: " + community.getName());
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Настройки сообщества находятся в разработке");
    }
    
    @FXML
    private void onDeleteCommunityClick(Community community) {
        System.out.println("Удаление сообщества: " + community.getName());
        showAlert(Alert.AlertType.INFORMATION, "В разработке", 
                 "Удаление сообщества находится в разработке");
    }
    
    @FXML
    private void onCloseClick() {
        if (communitiesStage != null) {
            communitiesStage.close();
        }
    }
    
    private void refreshAllTabs() {
        loadMyCommunities();
        loadRecommendedCommunities();
        loadAllCommunities();
        loadManageTab();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private int getRandomCount() {
        return 5 + (int)(Math.random() * 20); // 5-25
    }
}
