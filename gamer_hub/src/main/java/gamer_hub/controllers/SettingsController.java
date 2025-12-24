package gamer_hub.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private ScrollPane mainScrollPane;
    
    // Общие настройки
    @FXML private ComboBox<String> languageComboBox;
    @FXML private ComboBox<String> themeComboBox;
    @FXML private Slider fontSizeSlider;
    @FXML private Label fontSizeLabel;
    @FXML private CheckBox animationsCheckBox;
    @FXML private CheckBox autoStartCheckBox;
    
    // Уведомления
    @FXML private CheckBox notificationsEnabledCheckBox;
    @FXML private CheckBox friendRequestsCheckBox;
    @FXML private CheckBox communityInvitesCheckBox;
    @FXML private CheckBox tournamentUpdatesCheckBox;
    @FXML private CheckBox gameNewsCheckBox;
    @FXML private Slider notificationVolumeSlider;
    @FXML private Label notificationVolumeLabel;
    
    // Конфиденциальность
    @FXML private CheckBox profileVisibleCheckBox;
    @FXML private CheckBox showOnlineStatusCheckBox;
    @FXML private CheckBox showTrackedGamesCheckBox;
    @FXML private CheckBox allowFriendRequestsCheckBox;
    @FXML private CheckBox dataCollectionCheckBox;
    
    // Производительность
    @FXML private ComboBox<String> graphicsQualityComboBox;
    @FXML private ComboBox<String> refreshRateComboBox;
    @FXML private CheckBox hardwareAccelerationCheckBox;
    @FXML private CheckBox reduceAnimationsCheckBox;
    
    // Экспериментальные функции
    @FXML private CheckBox betaFeaturesCheckBox;
    @FXML private CheckBox voiceChatCheckBox;
    @FXML private CheckBox advancedStatsCheckBox;
    
    // Безопасность
    @FXML private CheckBox twoFactorAuthCheckBox;
    @FXML private CheckBox sessionExpirationCheckBox;
    @FXML private CheckBox loginNotificationsCheckBox;
    @FXML private ComboBox<String> sessionHistoryComboBox;
    
    // Расширенные настройки
    @FXML private Slider cacheSizeSlider;
    @FXML private Label cacheSizeLabel;
    @FXML private ComboBox<String> loggingLevelComboBox;
    @FXML private CheckBox developerModeCheckBox;
    @FXML private CheckBox debugModeCheckBox;
    @FXML private CheckBox verboseLoggingCheckBox;
    
    private Stage settingsStage;
    
    public void setStage(Stage stage) {
        this.settingsStage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✓ Окно настроек инициализировано");
        
        // Инициализация всех ComboBox
        initializeComboBoxes();
        
        // Привязка слайдеров к меткам
        bindSlidersToLabels();
        
        // Настройка зависимостей чекбоксов
        setupDependencies();
        
        // Настройка прокрутки
        setupScrollPane();
    }
    
    private void initializeComboBoxes() {
        // Язык
        languageComboBox.getItems().addAll(
            "Русский",
            "English",
            "Español",
            "Deutsch",
            "Français",
            "日本語",
            "中文"
        );
        languageComboBox.setValue("Русский");
        
        // Тема
        themeComboBox.getItems().addAll(
            "Темная (по умолчанию)",
            "Светлая",
            "Контрастная",
            "Автоматически",
            "Ночной режим",
            "Классическая"
        );
        themeComboBox.setValue("Темная (по умолчанию)");
        
        // Качество графики
        graphicsQualityComboBox.getItems().addAll(
            "Высокое",
            "Среднее",
            "Низкое",
            "Автоматически",
            "Экстремальное",
            "Экономия батареи"
        );
        graphicsQualityComboBox.setValue("Среднее");
        
        // Частота обновления
        refreshRateComboBox.getItems().addAll(
            "60 Гц",
            "75 Гц",
            "120 Гц",
            "144 Гц",
            "240 Гц",
            "Автоматически"
        );
        refreshRateComboBox.setValue("Автоматически");
        
        // История сессий
        sessionHistoryComboBox.getItems().addAll(
            "1 неделя",
            "1 месяц",
            "3 месяца",
            "6 месяцев",
            "1 год",
            "Всегда"
        );
        sessionHistoryComboBox.setValue("1 месяц");
        
        // Уровень логирования
        loggingLevelComboBox.getItems().addAll(
            "Выкл.",
            "Только ошибки",
            "Предупреждения",
            "Информация",
            "Отладка",
            "Трассировка"
        );
        loggingLevelComboBox.setValue("Предупреждения");
    }
    
    private void bindSlidersToLabels() {
        // Размер шрифта
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            fontSizeLabel.setText(String.format("%.0f", newVal));
        });
        
        // Громкость уведомлений
        notificationVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            notificationVolumeLabel.setText(String.format("%.0f%%", newVal));
        });
        
        // Размер кэша
        cacheSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            cacheSizeLabel.setText(String.format("%.0f МБ", newVal));
        });
        
        // Устанавливаем начальные значения меток
        fontSizeLabel.setText(String.format("%.0f", fontSizeSlider.getValue()));
        notificationVolumeLabel.setText(String.format("%.0f%%", notificationVolumeSlider.getValue()));
        cacheSizeLabel.setText(String.format("%.0f МБ", cacheSizeSlider.getValue()));
    }
    
    private void setupDependencies() {
        // Если уведомления выключены, отключаем все подпункты
        notificationsEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean enabled = newVal;
            friendRequestsCheckBox.setDisable(!enabled);
            communityInvitesCheckBox.setDisable(!enabled);
            tournamentUpdatesCheckBox.setDisable(!enabled);
            gameNewsCheckBox.setDisable(!enabled);
            notificationVolumeSlider.setDisable(!enabled);
            notificationVolumeLabel.setDisable(!enabled);
        });
        
        // Если включено уменьшение анимаций, выключаем основную анимацию
        reduceAnimationsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                animationsCheckBox.setSelected(false);
            }
        });
        
        animationsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                reduceAnimationsCheckBox.setSelected(false);
            }
        });
        
        // Если включен режим разработчика, активируем отладочные опции
        developerModeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            debugModeCheckBox.setDisable(!newVal);
            verboseLoggingCheckBox.setDisable(!newVal);
            loggingLevelComboBox.setDisable(!newVal);
        });
    }
    
    private void setupScrollPane() {
        // Настройка прокрутки
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Добавляем CSS стиль для кастомного скроллбара
        mainScrollPane.getStyleClass().add("settings-scroll-pane");
    }
    
    @FXML
    private void onSaveClick() {
        System.out.println("✓ Настройки сохранены");
        
        // Логирование выбранных настроек
        System.out.println("  Язык: " + languageComboBox.getValue());
        System.out.println("  Тема: " + themeComboBox.getValue());
        System.out.println("  Размер шрифта: " + fontSizeSlider.getValue());
        System.out.println("  Уведомления: " + notificationsEnabledCheckBox.isSelected());
        System.out.println("  Конфиденциальность: " + profileVisibleCheckBox.isSelected());
        System.out.println("  Качество графики: " + graphicsQualityComboBox.getValue());
        System.out.println("  Бета-функции: " + betaFeaturesCheckBox.isSelected());
        System.out.println("  Размер кэша: " + cacheSizeSlider.getValue() + " МБ");
        
        // Закрываем окно
        if (settingsStage != null) {
            settingsStage.close();
        }
    }
    
    @FXML
    private void onCancelClick() {
        System.out.println("✗ Изменения отменены");
        if (settingsStage != null) {
            settingsStage.close();
        }
    }
    
    @FXML
    private void onResetClick() {
        System.out.println("🔄 Сброс настроек к значениям по умолчанию");
        
        // Сброс всех значений к умолчанию
        languageComboBox.setValue("Русский");
        themeComboBox.setValue("Темная (по умолчанию)");
        fontSizeSlider.setValue(14);
        animationsCheckBox.setSelected(true);
        autoStartCheckBox.setSelected(false);
        
        notificationsEnabledCheckBox.setSelected(true);
        friendRequestsCheckBox.setSelected(true);
        communityInvitesCheckBox.setSelected(true);
        tournamentUpdatesCheckBox.setSelected(true);
        gameNewsCheckBox.setSelected(true);
        notificationVolumeSlider.setValue(80);
        
        profileVisibleCheckBox.setSelected(true);
        showOnlineStatusCheckBox.setSelected(true);
        showTrackedGamesCheckBox.setSelected(true);
        allowFriendRequestsCheckBox.setSelected(true);
        dataCollectionCheckBox.setSelected(false);
        
        graphicsQualityComboBox.setValue("Среднее");
        refreshRateComboBox.setValue("Автоматически");
        hardwareAccelerationCheckBox.setSelected(true);
        reduceAnimationsCheckBox.setSelected(false);
        
        betaFeaturesCheckBox.setSelected(false);
        voiceChatCheckBox.setSelected(false);
        advancedStatsCheckBox.setSelected(false);
        
        twoFactorAuthCheckBox.setSelected(false);
        sessionExpirationCheckBox.setSelected(true);
        loginNotificationsCheckBox.setSelected(true);
        sessionHistoryComboBox.setValue("1 месяц");
        
        cacheSizeSlider.setValue(500);
        loggingLevelComboBox.setValue("Предупреждения");
        developerModeCheckBox.setSelected(false);
        debugModeCheckBox.setSelected(false);
        verboseLoggingCheckBox.setSelected(false);
        
        // Обновляем метки
        fontSizeLabel.setText("14");
        notificationVolumeLabel.setText("80%");
        cacheSizeLabel.setText("500 МБ");
        
        System.out.println("✓ Настройки сброшены к значениям по умолчанию");
    }
    
    @FXML
    private void onExportClick() {
        System.out.println("📁 Экспорт настроек в файл");
        // Здесь будет логика экспорта настроек
    }
    
    @FXML
    private void onImportClick() {
        System.out.println("📂 Импорт настроек из файла");
        // Здесь будет логика импорта настроек
    }
    
    @FXML
    private void onClearCacheClick() {
        System.out.println("🗑️ Очистка кэша приложения");
        // Здесь будет логика очистки кэша
    }
    
    @FXML
    private void onHelpClick() {
        System.out.println("❓ Открытие справки по настройкам");
        // Здесь будет логика открытия справки
    }
    
    @FXML
    private void onAboutClick() {
        System.out.println("ℹ️ Открытие информации о приложении");
        // Здесь будет логика открытия информации
    }
}