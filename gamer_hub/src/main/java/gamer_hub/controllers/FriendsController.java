package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import gamer_hub.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FriendsController implements Initializable
{
    @FXML private TextField searchField;
    @FXML private VBox friendsList;
    @FXML private VBox noFriendsMessage;
    @FXML private VBox statsSection;
    @FXML private Label totalFriendsLabel;
    @FXML private Label onlineFriendsLabel;
    @FXML private Label activeFriendsLabel;
    @FXML private Label mutualGamesLabel;
    @FXML private Label infoLabel;
    
    @FXML private VBox friendTemplate; // Шаблон карточки друга
    
    private Stage stage;
    private User currentUser;
    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("✓ Окно друзей инициализировано");
        loadFriendsData();
    }
    
    private void loadFriendsData()
    {
        currentUser = DataManager.getCurrentUser();
        
        if (currentUser == null)
        {
            System.out.println("⚠ Ошибка: нет текущего пользователя");
            return;
        }
        
        System.out.println("Загрузка списка друзей для: " + currentUser.getUsername());
        
        // Очищаем список
        friendsList.getChildren().clear();
        
        // Получаем список ID друзей
        List<Integer> friendIds = currentUser.getFriendIds();
        
        if (friendIds.isEmpty())
        {
            // Нет друзей - показываем сообщение
            noFriendsMessage.setVisible(true);
            noFriendsMessage.setManaged(true);
            statsSection.setVisible(false);
            statsSection.setManaged(false);
            infoLabel.setText("У вас пока нет друзей");
            System.out.println("  У пользователя нет друзей");
            return;
        }
        
        // Есть друзья - скрываем сообщение
        noFriendsMessage.setVisible(false);
        noFriendsMessage.setManaged(false);
        statsSection.setVisible(true);
        statsSection.setManaged(true);
        
        // Загружаем статистику
        loadStatistics(friendIds);
        
        int loadedCount = 0;
        
        // Загружаем каждого друга
        for (Integer friendId : friendIds)
        {
            User friend = DataManager.getUserById(friendId);
            if (friend != null)
            {
                HBox friendItem = createFriendItem(friend);
                friendsList.getChildren().add(friendItem);
                loadedCount++;
                System.out.println("  Загружен друг: " + friend.getUsername() + " (ID: " + friendId + ")");
            }
            else
            {
                System.out.println("  ⚠ Не найден друг с ID: " + friendId);
            }
        }
        
        infoLabel.setText("Загружено друзей: " + loadedCount + " из " + friendIds.size());
        System.out.println("✓ Всего загружено друзей: " + loadedCount);
    }
    
    private void loadStatistics(List<Integer> friendIds)
    {
        totalFriendsLabel.setText(String.valueOf(friendIds.size()));
        
        // Собираем статистику
        int onlineCount = 0;
        int activeCount = 0;
        int mutualGamesTotal = 0;
        
        for (Integer friendId : friendIds)
        {
            User friend = DataManager.getUserById(friendId);
            if (friend != null)
            {
                // Для примера - случайное определение онлайн статуса
                if (Math.random() > 0.5) onlineCount++;
                if (Math.random() > 0.3) activeCount++;
                
                // Подсчитываем общие игры
                mutualGamesTotal += countMutualGames(friend);
            }
        }
        
        onlineFriendsLabel.setText(String.valueOf(onlineCount));
        activeFriendsLabel.setText(String.valueOf(activeCount));
        mutualGamesLabel.setText(String.valueOf(mutualGamesTotal));
    }
    
    private int countMutualGames(User friend)
    {
        int mutualCount = 0;
        List<Integer> currentUserGames = currentUser.getTrackedGameIds();
        List<Integer> friendGames = friend.getTrackedGameIds();
        
        for (Integer gameId : currentUserGames)
        {
            if (friendGames.contains(gameId))
            {
                mutualCount++;
            }
        }
        
        return mutualCount;
    }
    
    private HBox createFriendItem(User friend)
    {
        // Создаем копию шаблона
        HBox friendItem = (HBox) friendTemplate.lookup("HBox");
        if (friendItem == null) {
            // Если не нашли в шаблоне, создаем вручную
            return createFriendItemManually(friend);
        }
        
        // Клонируем шаблон (в JavaFX нет встроенного клонирования, поэтому создаем новый)
        return createFriendItemManually(friend);
    }
    
    private HBox createFriendItemManually(User friend)
    {
        HBox item = new HBox();
        item.setSpacing(15);
        item.setStyle("-fx-background-color: #423738; -fx-background-radius: 10; " +
                     "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1; " +
                     "-fx-padding: 15;");
        item.setPrefWidth(800);
        
        // Аватар друга
        VBox avatarBox = new VBox();
        avatarBox.setStyle("-fx-alignment: CENTER;");
        
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 32px;");
        
        // Онлайн статус (случайный для демонстрации)
        boolean isOnline = Math.random() > 0.5;
        Label status = new Label(isOnline ? "● онлайн" : "○ офлайн");
        status.setStyle("-fx-font-size: 10px; -fx-text-fill: " + 
                       (isOnline ? "#4CAF50" : "#8E5915") + ";");
        
        avatarBox.getChildren().addAll(avatar, status);
        
        // Основная информация о друге
        VBox info = new VBox(8);
        
        Label username = new Label(friend.getUsername());
        username.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label email = new Label(friend.getEmail() != null ? friend.getEmail() : "Нет email");
        email.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E5915;");
        
        // Статистика друга
        VBox stats = new VBox(5);
        
        Label gamesStat = new Label("🎮 Игр: " + friend.getTrackedGameIds().size());
        gamesStat.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        Label communitiesStat = new Label("👥 Сообществ: " + friend.getCommunityIds().size());
        communitiesStat.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        // Общие игры
        int mutualGames = countMutualGames(friend);
        Label mutualGamesStat = new Label("🔄 Общих игр: " + mutualGames);
        mutualGamesStat.setStyle("-fx-font-size: 12px; -fx-text-fill: " + 
                                (mutualGames > 0 ? "#F4B315" : "#8E5915") + ";");
        
        stats.getChildren().addAll(gamesStat, communitiesStat, mutualGamesStat);
        
        info.getChildren().addAll(username, email, stats);
        
        // Панель управления
        VBox actions = new VBox(10);
        actions.setStyle("-fx-alignment: CENTER_RIGHT; -fx-min-width: 150;");
        
        Button messageButton = new Button("💬 Написать");
        messageButton.setStyle("-fx-background-color: #8E5915; " +
                              "-fx-text-fill: #1A141A; " +
                              "-fx-background-radius: 5; " +
                              "-fx-padding: 8 15; " +
                              "-fx-font-size: 12px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-cursor: hand; " +
                              "-fx-pref-width: 120;");
        messageButton.setOnAction(e -> onMessageFriendClick(friend));
        
        Button profileButton = new Button("👁️ Профиль");
        profileButton.setStyle("-fx-background-color: #E59312; " +
                              "-fx-text-fill: #1A141A; " +
                              "-fx-background-radius: 5; " +
                              "-fx-padding: 8 15; " +
                              "-fx-font-size: 12px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-cursor: hand; " +
                              "-fx-pref-width: 120;");
        profileButton.setOnAction(e -> onViewProfileClick(friend));
        
        Button removeButton = new Button("❌ Удалить");
        removeButton.setStyle("-fx-background-color: #FF6B6B; " +
                             "-fx-text-fill: #1A141A; " +
                             "-fx-background-radius: 5; " +
                             "-fx-padding: 8 15; " +
                             "-fx-font-size: 12px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-cursor: hand; " +
                             "-fx-pref-width: 120;");
        removeButton.setOnAction(e -> onRemoveFriendClick(friend));
        
        // Добавляем эффекты при наведении
        addHoverEffect(messageButton, "#A36B1E");
        addHoverEffect(profileButton, "#F4B315");
        addHoverEffect(removeButton, "#FF5252");
        
        actions.getChildren().addAll(messageButton, profileButton, removeButton);
        
        item.getChildren().addAll(avatarBox, info, actions);
        return item;
    }
    
    private void addHoverEffect(Button button, String hoverColor) {
        String originalStyle = button.getStyle();
        
        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle.replace(
                "-fx-background-color: " + getCurrentBackgroundColor(originalStyle) + ";",
                "-fx-background-color: " + hoverColor + ";") +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);");
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }
    
    private String getCurrentBackgroundColor(String style) {
        // Простой парсинг для получения текущего цвета фона
        if (style.contains("#8E5915")) return "#8E5915";
        if (style.contains("#E59312")) return "#E59312";
        if (style.contains("#FF6B6B")) return "#FF6B6B";
        return "#8E5915";
    }
    
    // ========== ОБРАБОТЧИКИ КНОПОК ==========
    
    @FXML
    private void onCloseClick()
    {
        System.out.println("Нажата кнопка: Закрыть окно друзей");
        if (stage != null)
        {
            stage.close();
        }
    }
    
    @FXML
    private void onSearchClick()
    {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty())
        {
            System.out.println("Поиск друзей по запросу: " + searchText);
            // В реальном приложении здесь был бы поиск по базе данных
            searchField.setText("");
            searchField.setPromptText("Функция поиска в разработке...");
        }
    }
    
    @FXML
    private void onRefreshClick()
    {
        System.out.println("Нажата кнопка: Обновить список друзей");
        loadFriendsData();
        infoLabel.setText("Список обновлен ✓");
    }
    
    @FXML
    private void onStatsClick()
    {
        System.out.println("Нажата кнопка: Показать статистику");
        // Переключаем видимость статистики
        boolean isVisible = statsSection.isVisible();
        statsSection.setVisible(!isVisible);
        statsSection.setManaged(!isVisible);
    }
    
    @FXML
    private void onFindFriendsClick()
    {
        System.out.println("Нажата кнопка: Найти друзей");
        // В реальном приложении здесь открывалось бы окно поиска
        infoLabel.setText("Функция поиска друзей в разработке...");
    }
    
    // Обработчики для кнопок действий с друзьями
    
    private void onMessageFriendClick(User friend)
    {
        System.out.println("Нажата кнопка: Написать другу " + friend.getUsername());
        // В реальном приложении здесь открывался бы чат
        infoLabel.setText("Чат с " + friend.getUsername() + " (в разработке)");
    }
    
    private void onViewProfileClick(User friend)
    {
        System.out.println("Нажата кнопка: Просмотр профиля друга " + friend.getUsername());
        // В реальном приложении здесь открывался бы профиль друга
        infoLabel.setText("Просмотр профиля " + friend.getUsername() + " (в разработке)");
    }
    
    private void onRemoveFriendClick(User friend)
    {
        System.out.println("Нажата кнопка: Удалить друга " + friend.getUsername());
        
        // Удаляем друга из списка
        boolean removed = currentUser.getFriendIds().remove(Integer.valueOf(friend.getId()));
        
        if (removed)
        {
            // Сохраняем изменения
            DataManager.getAllUsers(); // Это перезагрузит список пользователей из JSON
            
            // Обновляем отображение
            loadFriendsData();
            
            System.out.println("✓ Друг " + friend.getUsername() + " удален");
            infoLabel.setText("Друг " + friend.getUsername() + " удален");
        }
        else
        {
            System.out.println("⚠ Ошибка при удалении друга");
            infoLabel.setText("Ошибка при удалении друга");
        }
    }
}