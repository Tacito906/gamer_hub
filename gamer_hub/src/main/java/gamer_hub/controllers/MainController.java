package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

public class MainController
{
    @FXML private VBox sidebar;
    @FXML private VBox mainContent;
    @FXML private VBox noGamesMessage;
    @FXML private TabPane gamesTabPane;
    @FXML private Label userInfoLabel;
    @FXML private Button sidebarToggleButton; // Добавляем ссылку на кнопку-бургер
    
    private Stage mainStage;
    private Set<Integer> currentTabGameIds = new HashSet<>();
    private boolean isSidebarVisible = true; // Флаг видимости боковой панели
    
    public void setStage(Stage stage)
    {
        this.mainStage = stage;
    }
    
    @FXML
    private void initialize()
    {
        System.out.println("✓ Главное окно инициализировано");
        
        // Показываем информацию о текущем пользователе
        var currentUser = DataManager.getCurrentUser();
        if (currentUser != null)
        {
            userInfoLabel.setText("👤 " + currentUser.getUsername());
            System.out.println("Текущий пользователь: " + currentUser.getUsername());
            System.out.println("Отслеживает игр: " + currentUser.getTrackedGameIds().size());
        }
        
        // Проверяем, есть ли отслеживаемые игры
        checkTrackedGames();
    }
    
    private void checkTrackedGames()
    {
        var currentUser = DataManager.getCurrentUser();
        boolean hasGames = currentUser != null && !currentUser.getTrackedGameIds().isEmpty();
        
        if (hasGames)
        {
            // Есть игры - скрываем сообщение, показываем вкладки
            noGamesMessage.setVisible(false);
            noGamesMessage.setManaged(false);
            gamesTabPane.setVisible(true);
            gamesTabPane.setManaged(true);
            
            // Создаем вкладки для каждой отслеживаемой игры
            createGameTabs(currentUser);
        }
        else
        {
            // Нет игр - показываем сообщение
            noGamesMessage.setVisible(true);
            noGamesMessage.setManaged(true);
            gamesTabPane.setVisible(false);
            gamesTabPane.setManaged(false);
        }
    }
    
    private void createGameTabs(gamer_hub.model.User user)
    {
        // Собираем ID текущих игр пользователя
        Set<Integer> userGameIds = new HashSet<>(user.getTrackedGameIds());
        
        // Если вкладки уже созданы для этих игр - ничего не делаем
        if (currentTabGameIds.equals(userGameIds))
        {
            System.out.println("Вкладки уже актуальны, пропускаем создание");
            return;
        }
        
        // Очищаем старые вкладки
        gamesTabPane.getTabs().clear();
        currentTabGameIds.clear();
        
        // Для каждой игры создаем вкладку
        for (var gameId : user.getTrackedGameIds())
        {
            var game = DataManager.getGameById(gameId);
            if (game != null && !currentTabGameIds.contains(gameId))
            {
                Tab tab = new Tab(game.getTitle());
                tab.setClosable(false);
                
                // Создаем содержимое вкладки
                ScrollPane tabContent = createTabContent(game);
                tab.setContent(tabContent);
                
                gamesTabPane.getTabs().add(tab);
                currentTabGameIds.add(gameId);
                
                System.out.println("Создана вкладка для игры: " + game.getTitle() + " (ID: " + gameId + ")");
            }
        }
        
        System.out.println("Всего создано вкладок: " + gamesTabPane.getTabs().size());
    }
    
    private ScrollPane createTabContent(gamer_hub.model.Game game)
    {
        VBox content = new VBox();
        content.setSpacing(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #423738; -fx-background-radius: 10;");
        
        // Заголовок игры
        HBox header = new HBox();
        header.setSpacing(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label gameTitle = new Label(game.getTitle());
        gameTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        Label gameGenre = new Label("(" + game.getGenre() + ")");
        gameGenre.setStyle("-fx-font-size: 14px; -fx-text-fill: #8E5915; -fx-font-style: italic;");
        
        header.getChildren().addAll(gameTitle, gameGenre);
        
        // Блок 1: Открытые сообщества
        VBox communitiesBlock = createContentBlock(
            "👥 Открытые сообщества (" + getRandomCount() + ")", 
            "Найдите единомышленников для совместной игры",
            "Показать все сообщества",
            "#8E5915"
        );
        
        // Блок 2: Активные анкеты
        VBox requestsBlock = createContentBlock(
            "📝 Активные анкеты (" + getRandomCount() + ")", 
            "Игроки ищут команду или напарников прямо сейчас",
            "Показать все анкеты",
            "#E59312"
        );
        
        // Блок 3: Предстоящие турниры
        VBox tournamentsBlock = createContentBlock(
            "🏆 Предстоящие турниры (" + getRandomCount() + ")", 
            "Примите участие в соревнованиях с призовыми фондами",
            "Показать все турниры",
            "#F4B315"
        );
        
        content.getChildren().addAll(header, communitiesBlock, requestsBlock, tournamentsBlock);
        
        // Обёртка в ScrollPane для прокрутки
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(0));
        
        return scrollPane;
    }
    
    private VBox createContentBlock(String title, String description, String buttonText, String buttonColor)
    {
        VBox block = new VBox();
        block.setSpacing(12);
        block.setPadding(new Insets(15));
        block.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                      "-fx-border-color: #8E5915; -fx-border-radius: 10; -fx-border-width: 1;");
        
        // Заголовок блока
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
        
        // Описание
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
        descLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Примеры элементов в блоке (3-5 элементов)
        VBox itemsContainer = new VBox();
        itemsContainer.setSpacing(8);
        itemsContainer.setPadding(new Insets(10, 0, 0, 0));
        
        // Генерируем 3-5 примеров
        for (int i = 1; i <= 3 + (int)(Math.random() * 3); i++)
        {
            HBox item = createExampleItem(i, title);
            itemsContainer.getChildren().add(item);
        }
        
        // Кнопка "Показать все"
        Button viewAllButton = new Button(buttonText);
        viewAllButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: #1A141A; " +
                              "-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-size: 12px; " +
                              "-fx-font-weight: bold; -fx-cursor: hand;");
        viewAllButton.setOnAction(e -> System.out.println("Открыто: " + title));
        
        block.getChildren().addAll(titleLabel, descLabel, itemsContainer, viewAllButton);
        return block;
    }
    
    private HBox createExampleItem(int index, String category)
    {
        HBox item = new HBox();
        item.setSpacing(10);
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setStyle("-fx-background-color: #423738; -fx-background-radius: 8;");
        
        // Иконка в зависимости от категории
        String icon = "•";
        if (category.contains("сообщества")) icon = "👥";
        else if (category.contains("анкеты")) icon = "📋";
        else if (category.contains("турниры")) icon = "🏆";
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 14px;");
        
        // Название элемента
        String itemName = getExampleItemName(category, index);
        Label nameLabel = new Label(itemName);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #D3AF85; -fx-font-weight: bold;");
        
        // Дополнительная информация
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        String info = getExampleItemInfo(category, index);
        Label infoLabel = new Label(info);
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8E5915;");
        
        item.getChildren().addAll(iconLabel, nameLabel, spacer, infoLabel);
        return item;
    }
    
    private String getExampleItemName(String category, int index)
    {
        if (category.contains("сообщества"))
        {
            String[] names = {"Dota 2 Pro", "CS:GO Legends", "WoW Raiders", "Русскоязычное", "Новички"};
            return names[(index - 1) % names.length];
        }
        else if (category.contains("анкеты"))
        {
            String[] names = {"Ищу команду на рейд", "Нужен танк", "Сбор на арену 3x3", "Рейд в 20:00"};
            return names[(index - 1) % names.length];
        }
        else
        {
            String[] names = {"Весенний кубок", "Еженедельный турнир", "Кубок чемпионов", "Онлайн-лига"};
            return names[(index - 1) % names.length];
        }
    }
    
    private String getExampleItemInfo(String category, int index)
    {
        if (category.contains("сообщества"))
        {
            return (50 + index * 10) + " участников";
        }
        else if (category.contains("анкеты"))
        {
            return (1 + index) + " часа назад";
        }
        else
        {
            return "Через " + index + " дней";
        }
    }
    
    private int getRandomCount()
    {
        return 3 + (int)(Math.random() * 3); // 3-5
    }
    
    // ========== ОБРАБОТЧИКИ КНОПОК БОКОВОЙ ПАНЕЛИ ==========
    
    @FXML
    private void onHomeClick()
    {
        System.out.println("Нажата кнопка: Главная");
    }
    
    @FXML
    private void onCommunitiesClick()
    {
        System.out.println("Нажата кнопка: Мои сообщества");
        openCommunitiesWindow();
    }
    
    @FXML
    private void onFriendsClick()
    {
        System.out.println("Нажата кнопка: Мои друзья");
        openFriendsWindow();
    }
    
    @FXML
    private void onMyRequestsClick()
    {
        System.out.println("Нажата кнопка: Мои анкеты");
        openProfileRequestsWindow();
    }
    
    @FXML
    private void onMyTournamentsClick()
    {
        System.out.println("Нажата кнопка: Мои турниры");
        openTournamentsWindow();
    }
    
    @FXML
    private void onProfileClick()
    {
        System.out.println("Нажата кнопка: Профиль");
        openProfileWindow();
    }
    
    @FXML
    private void onSettingsClick()
    {
        System.out.println("Нажата кнопка: Настройки");
        openSettingsWindow();
    }
    
    // ========== ОБРАБОТЧИКИ КНОПОК ОСНОВНОЙ ОБЛАСТИ ==========
    
    @FXML
    private void onAddGameClick()
    {
        System.out.println("Нажата кнопка: Добавить игру");
        openAddGameWindow();
    }
    
    @FXML
    private void onAddFirstGameClick()
    {
        System.out.println("Нажата кнопка: Добавить первую игру");
        openAddGameWindow();
    }
    
    // Новый обработчик для кнопки-бургера
    @FXML
    private void onSidebarToggleClick()
    {
        toggleSidebar();
    }
    
    // Метод для переключения видимости боковой панели
    private void toggleSidebar()
    {
        if (isSidebarVisible)
        {
            // Скрываем боковую панель
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            
            // Меняем отступы у основной области
            AnchorPane.setLeftAnchor(mainContent, 0.0);
            
            // Меняем иконку на стрелку (чтобы показать, что панель скрыта)
            HBox graphic = (HBox) sidebarToggleButton.getGraphic();
            Label iconLabel = (Label) graphic.getChildren().get(0);
            iconLabel.setText("→");
            
            System.out.println("✓ Боковая панель скрыта");
        }
        else
        {
            // Показываем боковую панель
            sidebar.setVisible(true);
            sidebar.setManaged(true);
            
            // Восстанавливаем отступы у основной области
            AnchorPane.setLeftAnchor(mainContent, 220.0);
            
            // Меняем иконку обратно на бургер
            HBox graphic = (HBox) sidebarToggleButton.getGraphic();
            Label iconLabel = (Label) graphic.getChildren().get(0);
            iconLabel.setText("☰");
            
            System.out.println("✓ Боковая панель показана");
        }
        
        // Инвертируем флаг
        isSidebarVisible = !isSidebarVisible;
    }
    
    private void openAddGameWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/add_game.fxml"));
            Parent root = loader.load();
            
            Stage addGameStage = new Stage();
            addGameStage.setTitle("Добавление игр");
            addGameStage.initModality(Modality.APPLICATION_MODAL);
            addGameStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 1200, 1000);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            addGameStage.setScene(scene);
            
            // Передаем stage в контроллер
            AddGameController controller = loader.getController();
            controller.setStage(addGameStage);
            
            addGameStage.showAndWait();
            
            // После закрытия окна обновляем список игр
            System.out.println("✓ Окно добавления игр закрыто. Обновляем главное окно...");
            refreshGames();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна добавления игр: " + e.getMessage());
        }
    }
    
    // Метод для обновления списка игр после добавления
    private void refreshGames()
    {
        System.out.println("🔄 Обновление списка игр в главном окне...");
        checkTrackedGames();
        
        // Показываем информационное сообщение
        var currentUser = DataManager.getCurrentUser();
        if (currentUser != null)
        {
            int gameCount = currentUser.getTrackedGameIds().size();
            System.out.println("  Пользователь теперь отслеживает " + gameCount + " игр");
        }
    }

    private void openSettingsWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/settings.fxml"));
            Parent root = loader.load();
            
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Настройки Gamer Hub");
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 1000, 750); // Увеличил размер окна
            settingsStage.setMinWidth(900);
            settingsStage.setMinHeight(700);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            settingsStage.setScene(scene);
            
            // Передаем stage в контроллер
            SettingsController controller = loader.getController();
            controller.setStage(settingsStage);
            
            settingsStage.showAndWait();
            
            System.out.println("✓ Окно настроек закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна настроек: " + e.getMessage());
        }
    }

    private void openCommunitiesWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/communities.fxml"));
            Parent root = loader.load();
            
            Stage communitiesStage = new Stage();
            communitiesStage.setTitle("Сообщества - Gamer Hub");
            communitiesStage.initModality(Modality.APPLICATION_MODAL);
            communitiesStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 1000, 800);
            communitiesStage.setMinWidth(900);
            communitiesStage.setMinHeight(700);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            communitiesStage.setScene(scene);
            
            // Передаем stage в контроллер
            CommunitiesController controller = loader.getController();
            controller.setStage(communitiesStage);
            
            communitiesStage.showAndWait();
            
            System.out.println("✓ Окно сообществ закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна сообществ: " + e.getMessage());
        }
    }

    private void openProfileRequestsWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/profile_requests.fxml"));
            Parent root = loader.load();
            
            Stage profileRequestsStage = new Stage();
            profileRequestsStage.setTitle("Мои анкеты - Gamer Hub");
            profileRequestsStage.initModality(Modality.APPLICATION_MODAL);
            profileRequestsStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 1000, 800);
            profileRequestsStage.setMinWidth(900);
            profileRequestsStage.setMinHeight(700);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            profileRequestsStage.setScene(scene);
            
            // Передаем stage в контроллер
            ProfileRequestsController controller = loader.getController();
            controller.setStage(profileRequestsStage);
            
            profileRequestsStage.showAndWait();
            
            System.out.println("✓ Окно анкет закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна анкет: " + e.getMessage());
        }
    }

    private void openTournamentsWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/tournaments.fxml"));
            Parent root = loader.load();
            
            Stage tournamentsStage = new Stage();
            tournamentsStage.setTitle("Мои турниры - Gamer Hub");
            tournamentsStage.initModality(Modality.APPLICATION_MODAL);
            tournamentsStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 1000, 800);
            tournamentsStage.setMinWidth(900);
            tournamentsStage.setMinHeight(700);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            tournamentsStage.setScene(scene);
            
            // Передаем stage в контроллер
            TournamentsController controller = loader.getController();
            controller.setStage(tournamentsStage);
            
            tournamentsStage.showAndWait();
            
            System.out.println("✓ Окно турниров закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна турниров: " + e.getMessage());
        }
    }

    private void openProfileWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/profile.fxml"));
            Parent root = loader.load();
            
            Stage profileStage = new Stage();
            profileStage.setTitle("Мой профиль - Gamer Hub");
            profileStage.initModality(Modality.APPLICATION_MODAL);
            profileStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 900, 700);
            profileStage.setMinWidth(800);
            profileStage.setMinHeight(600);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            profileStage.setScene(scene);
            
            // Передаем stage в контроллер
            ProfileController controller = loader.getController();
            controller.setStage(profileStage);
            
            profileStage.showAndWait();
            
            System.out.println("✓ Окно профиля закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна профиля: " + e.getMessage());
        }
    }

    private void openFriendsWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/friends.fxml"));
            Parent root = loader.load();
            
            Stage friendsStage = new Stage();
            friendsStage.setTitle("Мои друзья - Gamer Hub");
            friendsStage.initModality(Modality.APPLICATION_MODAL);
            friendsStage.initOwner(mainStage);
            
            Scene scene = new Scene(root, 900, 700);
            friendsStage.setMinWidth(800);
            friendsStage.setMinHeight(600);
            
            // Загружаем CSS
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            friendsStage.setScene(scene);
            
            // Передаем stage в контроллер
            FriendsController controller = loader.getController();
            controller.setStage(friendsStage);
            
            friendsStage.showAndWait();
            
            System.out.println("✓ Окно друзей закрыто");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Ошибка открытия окна друзей: " + e.getMessage());
        }
    }
}