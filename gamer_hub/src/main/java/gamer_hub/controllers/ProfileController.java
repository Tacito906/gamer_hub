package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import gamer_hub.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable
{
    @FXML private Circle avatarCircle;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label userIdLabel;
    @FXML private Label userUniqueIdLabel;
    @FXML private Label gamesCountLabel;
    @FXML private Label communitiesCountLabel;
    @FXML private Label friendsCountLabel;
    @FXML private Label requestsCountLabel;
    @FXML private Label registrationDateLabel;
    @FXML private Label lastUpdatedLabel;
    
    @FXML private VBox gamesSection;
    @FXML private VBox communitiesSection;
    @FXML private VBox friendsSection;
    @FXML private VBox gamesList;
    @FXML private VBox communitiesList;
    @FXML private VBox friendsList;
    @FXML private Label noGamesLabel;
    @FXML private Label noCommunitiesLabel;
    @FXML private Label noFriendsLabel;
    
    private Stage stage;
    private User currentUser;
    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("✓ Окно профиля инициализировано");
        loadUserData();
    }
    
    private void loadUserData()
    {
        currentUser = DataManager.getCurrentUser();
        
        if (currentUser == null)
        {
            System.out.println("⚠ Ошибка: нет текущего пользователя");
            return;
        }
        
        System.out.println("Загрузка данных профиля для: " + currentUser.getUsername());
        
        // Основная информация
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText("✉️ " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Нет email"));
        userIdLabel.setText("Пользователь #" + currentUser.getId());
        userUniqueIdLabel.setText(String.valueOf(currentUser.getId()));
        
        // Статистика
        int gamesCount = currentUser.getTrackedGameIds().size();
        int communitiesCount = currentUser.getCommunityIds().size();
        int friendsCount = currentUser.getFriendIds().size();
        
        // Получаем количество активных анкет пользователя
        List<ProfileRequest> userRequests = DataManager.getProfileRequestsByAuthor(currentUser.getId());
        int activeRequests = (int) userRequests.stream()
            .filter(r -> "open".equals(r.getStatus()))
            .count();
        
        gamesCountLabel.setText(String.valueOf(gamesCount));
        communitiesCountLabel.setText(String.valueOf(communitiesCount));
        friendsCountLabel.setText(String.valueOf(friendsCount));
        requestsCountLabel.setText(String.valueOf(activeRequests));
        
        // Даты (симулируем, так как в модели User нет даты регистрации)
        // Можно добавить в будущем или использовать текущую дату
        LocalDateTime registrationDate = LocalDateTime.now().minusMonths(2);
        registrationDateLabel.setText(registrationDate.format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " (примерная)");
        
        // Обновляем метку последнего обновления
        lastUpdatedLabel.setText("Обновлено: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        // Загружаем списки
        loadGamesList();
        loadCommunitiesList();
        loadFriendsList();
        
        System.out.println("✓ Данные профиля загружены:");
        System.out.println("  Игр: " + gamesCount);
        System.out.println("  Сообществ: " + communitiesCount);
        System.out.println("  Друзей: " + friendsCount);
        System.out.println("  Активных анкет: " + activeRequests);
    }
    
    private void loadGamesList()
    {
        gamesList.getChildren().clear();
        List<Integer> gameIds = currentUser.getTrackedGameIds();
        
        if (gameIds.isEmpty())
        {
            noGamesLabel.setVisible(true);
            noGamesLabel.setManaged(true);
            return;
        }
        
        noGamesLabel.setVisible(false);
        noGamesLabel.setManaged(false);
        
        int count = 0;
        for (Integer gameId : gameIds)
        {
            Game game = DataManager.getGameById(gameId);
            if (game != null)
            {
                HBox gameItem = createGameItem(game);
                gamesList.getChildren().add(gameItem);
                count++;
            }
        }
        
        System.out.println("  Загружено игр в список: " + count);
    }
    
    private HBox createGameItem(Game game)
    {
        HBox item = new HBox();
        item.setSpacing(15);
        item.setStyle("-fx-background-color: #423738; -fx-background-radius: 8; -fx-padding: 10;");
        item.setPrefWidth(780);
        
        // Иконка игры
        Label icon = new Label("🎮");
        icon.setStyle("-fx-font-size: 20px;");
        
        // Информация об игре
        VBox info = new VBox(5);
        
        Label title = new Label(game.getTitle());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label genre = new Label(game.getGenre());
        genre.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label description = new Label(game.getDescription());
        description.setStyle("-fx-font-size: 11px; -fx-text-fill: #8E5915; -fx-wrap-text: true;");
        description.setMaxWidth(600);
        description.setPrefWidth(600);
        
        info.getChildren().addAll(title, genre, description);
        
        item.getChildren().addAll(icon, info);
        return item;
    }
    
    private void loadCommunitiesList()
    {
        communitiesList.getChildren().clear();
        List<Integer> communityIds = currentUser.getCommunityIds();
        
        if (communityIds.isEmpty())
        {
            noCommunitiesLabel.setVisible(true);
            noCommunitiesLabel.setManaged(true);
            return;
        }
        
        noCommunitiesLabel.setVisible(false);
        noCommunitiesLabel.setManaged(false);
        
        int count = 0;
        for (Integer communityId : communityIds)
        {
            Community community = DataManager.getCommunityById(communityId);
            if (community != null)
            {
                HBox communityItem = createCommunityItem(community);
                communitiesList.getChildren().add(communityItem);
                count++;
            }
        }
        
        System.out.println("  Загружено сообществ в список: " + count);
    }
    
    private HBox createCommunityItem(Community community)
    {
        HBox item = new HBox();
        item.setSpacing(15);
        item.setStyle("-fx-background-color: #423738; -fx-background-radius: 8; -fx-padding: 10;");
        item.setPrefWidth(780);
        
        // Иконка сообщества
        Label icon = new Label(community.isPublic() ? "🌐" : "🔒");
        icon.setStyle("-fx-font-size: 20px;");
        
        // Информация о сообществе
        VBox info = new VBox(5);
        
        Label title = new Label(community.getName());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Game game = DataManager.getGameById(community.getGameId());
        String gameName = game != null ? game.getTitle() : "Игра #" + community.getGameId();
        Label gameLabel = new Label("Игра: " + gameName);
        gameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        // Администратор
        User admin = DataManager.getUserById(community.getAdminId());
        String adminName = admin != null ? admin.getUsername() : "Админ #" + community.getAdminId();
        Label adminLabel = new Label("Админ: " + adminName);
        adminLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8E5915;");
        
        // Статистика участников
        Label membersLabel = new Label("👥 " + community.getMemberIds().size() + " участников");
        membersLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8E5915;");
        
        info.getChildren().addAll(title, gameLabel, adminLabel, membersLabel);
        
        item.getChildren().addAll(icon, info);
        return item;
    }
    
    private void loadFriendsList()
    {
        friendsList.getChildren().clear();
        List<Integer> friendIds = currentUser.getFriendIds();
        
        if (friendIds.isEmpty())
        {
            noFriendsLabel.setVisible(true);
            noFriendsLabel.setManaged(true);
            return;
        }
        
        noFriendsLabel.setVisible(false);
        noFriendsLabel.setManaged(false);
        
        int count = 0;
        for (Integer friendId : friendIds)
        {
            User friend = DataManager.getUserById(friendId);
            if (friend != null)
            {
                HBox friendItem = createFriendItem(friend);
                friendsList.getChildren().add(friendItem);
                count++;
            }
        }
        
        System.out.println("  Загружено друзей в список: " + count);
    }
    
    private HBox createFriendItem(User friend)
    {
        HBox item = new HBox();
        item.setSpacing(15);
        item.setStyle("-fx-background-color: #423738; -fx-background-radius: 8; -fx-padding: 10;");
        item.setPrefWidth(780);
        
        // Иконка друга
        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 20px;");
        
        // Информация о друге
        VBox info = new VBox(5);
        
        Label username = new Label(friend.getUsername());
        username.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label email = new Label(friend.getEmail() != null ? friend.getEmail() : "Нет email");
        email.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        // Статистика друга
        Label stats = new Label("🎮 " + friend.getTrackedGameIds().size() + " игр | " +
                               "👥 " + friend.getCommunityIds().size() + " сообществ | " +
                               "ID: #" + friend.getId());
        stats.setStyle("-fx-font-size: 11px; -fx-text-fill: #8E5915;");
        
        info.getChildren().addAll(username, email, stats);
        
        item.getChildren().addAll(icon, info);
        return item;
    }
    
    // ========== ОБРАБОТЧИКИ КНОПОК ==========
    
    @FXML
    private void onCloseClick()
    {
        System.out.println("Нажата кнопка: Закрыть профиль");
        if (stage != null)
        {
            stage.close();
        }
    }
    
    @FXML
    private void onEditProfileClick()
    {
        System.out.println("Нажата кнопка: Редактировать профиль");
        // Здесь будет логика для редактирования профиля
        // Сейчас просто показываем сообщение
        usernameLabel.setText(usernameLabel.getText() + " ✏️");
    }
    
    @FXML
    private void onRefreshClick()
    {
        System.out.println("Нажата кнопка: Обновить данные");
        loadUserData();
    }
    
    @FXML
    private void onExportClick()
    {
        System.out.println("Нажата кнопка: Экспорт данных");
        
        // Создаем строку с данными профиля для экспорта
        StringBuilder exportData = new StringBuilder();
        exportData.append("=== Экспорт данных профиля Gamer Hub ===\n\n");
        exportData.append("Имя пользователя: ").append(currentUser.getUsername()).append("\n");
        exportData.append("Email: ").append(currentUser.getEmail()).append("\n");
        exportData.append("ID пользователя: ").append(currentUser.getId()).append("\n");
        exportData.append("Дата экспорта: ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append("\n\n");
        
        exportData.append("--- Статистика ---\n");
        exportData.append("Отслеживаемых игр: ").append(currentUser.getTrackedGameIds().size()).append("\n");
        exportData.append("Сообществ: ").append(currentUser.getCommunityIds().size()).append("\n");
        exportData.append("Друзей: ").append(currentUser.getFriendIds().size()).append("\n");
        
        List<ProfileRequest> userRequests = DataManager.getProfileRequestsByAuthor(currentUser.getId());
        int activeRequests = (int) userRequests.stream()
            .filter(r -> "open".equals(r.getStatus()))
            .count();
        exportData.append("Активных анкет: ").append(activeRequests).append("\n\n");
        
        exportData.append("=== Конец экспорта ===\n");
        
        // Выводим в консоль (в будущем можно сохранить в файл)
        System.out.println("Экспортированные данные:\n" + exportData.toString());
        
        lastUpdatedLabel.setText("Данные экспортированы в консоль " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
