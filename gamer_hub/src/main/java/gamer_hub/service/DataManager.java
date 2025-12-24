package gamer_hub.service;

import gamer_hub.model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataManager
{
    // Данные в памяти
    private static List<User> users = new ArrayList<>();
    private static List<Game> games = new ArrayList<>();
    private static List<Community> communities = new ArrayList<>();
    private static List<ProfileRequest> profileRequests = new ArrayList<>();
    private static List<Tournament> tournaments = new ArrayList<>();
    
    // Счетчики для генерации ID
    private static AtomicInteger userIdCounter = new AtomicInteger(1);
    private static AtomicInteger gameIdCounter = new AtomicInteger(1);
    private static AtomicInteger communityIdCounter = new AtomicInteger(1);
    private static AtomicInteger requestIdCounter = new AtomicInteger(1);
    private static AtomicInteger tournamentIdCounter = new AtomicInteger(1);
    
    // Текущий пользователь (кто вошел в систему)
    private static User currentUser = null;
    
    // Статический блок инициализации
    static
    {
        loadAllData();
    }
    
    private static void loadAllData()
    {
        System.out.println("Загрузка данных из JSON файлов...");
        
        users = JsonHelp.readUsers();
        games = JsonHelp.readGames();
        communities = JsonHelp.readCommunities();
        profileRequests = JsonHelp.readProfileRequests();
        tournaments = JsonHelp.readTournaments();
        
        // Инициализируем счетчики ID на основе загруженных данных
        initializeIdCounters();
        
        // Если нет тестовых данных, создаем их
        if (users.isEmpty() && games.isEmpty())
        {
            initializeTestData();
        }
        
        System.out.println("Данные загружены:");
        System.out.println("  Пользователей: " + users.size());
        System.out.println("  Игр: " + games.size());
        System.out.println("  Сообществ: " + communities.size());
        System.out.println("  Анкет: " + profileRequests.size());
        System.out.println("  Турниров: " + tournaments.size());
    }
    
    private static void initializeIdCounters()
    {
        userIdCounter.set(users.stream().mapToInt(User::getId).max().orElse(0) + 1);
        gameIdCounter.set(games.stream().mapToInt(Game::getId).max().orElse(0) + 1);
        communityIdCounter.set(communities.stream().mapToInt(Community::getId).max().orElse(0) + 1);
        requestIdCounter.set(profileRequests.stream().mapToInt(ProfileRequest::getId).max().orElse(0) + 1);
        tournamentIdCounter.set(tournaments.stream().mapToInt(Tournament::getId).max().orElse(0) + 1);
    }
    
    private static void initializeTestData()
    {
        System.out.println("Инициализация тестовых данных...");
        
        // Создаем тестовые игры
        Game game1 = createGame("Dota 2", "MOBA", "5v5 multiplayer battle arena");
        Game game2 = createGame("CS:GO", "FPS", "Tactical shooter");
        Game game3 = createGame("World of Warcraft", "MMORPG", "Massively multiplayer online role-playing game");
        
        // Создаем тестового пользователя
        User admin = createUser("admin", "admin123", "admin@test.com");
        admin.getTrackedGameIds().add(game1.getId());
        admin.getTrackedGameIds().add(game2.getId());
        
        // Создаем тестовое сообщество
        Community community = createCommunity(
            "Dota 2 Pro Players", 
            game1.getId(), 
            "Сообщество для профессиональных игроков Dota 2", 
            admin.getId()
        );
        community.setPublic(true);
        
        // Создаем тестовую анкету
        ProfileRequest request = createProfileRequest(
            admin.getId(),
            game1.getId(),
            "Ищу команду для рейда",
            "Ищу опытных игроков для рейда в субботу в 20:00 МСК"
        );
        
        // Создаем тестовый турнир
        Tournament tournament = createTournament(
            "Dota 2 Spring Championship",
            game1.getId(),
            "Ежегодный весенний чемпионат по Dota 2",
            java.time.LocalDateTime.now().plusDays(7),
            java.time.LocalDateTime.now().plusDays(8),
            "Призовой фонд: 10000 руб",
            true,
            admin.getId()
        );
        
        // Сохраняем все данные
        saveAllData();
    }
    
    private static void saveAllData()
    {
        JsonHelp.writeUsers(users);
        JsonHelp.writeGames(games);
        JsonHelp.writeCommunities(communities);
        JsonHelp.writeProfileRequests(profileRequests);
        JsonHelp.writeTournaments(tournaments);
    }
    
    // === МЕТОДЫ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ ===
    public static User getCurrentUser()
    {
        return currentUser;
    }
    
    public static void setCurrentUser(User user)
    {
        currentUser = user;
    }
    
    public static User findUserByUsername(String username)
    {
        for (User user : users)
        {
            if (user.getUsername().equalsIgnoreCase(username))
            {
                return user;
            }
        }
        return null;
    }
    
    public static boolean userExists(String username)
    {
        return findUserByUsername(username) != null;
    }
    
    public static User authenticateUser(String username, String password)
    {
        User user = findUserByUsername(username);
        if (user != null && user.getPassword().equals(password))
        {
            currentUser = user;
            return user;
        }
        return null;
    }
    
    public static void addGamesToUser(int userId, List<Integer> gameIds)
    {
        User user = getUserById(userId);
        if (user != null)
        {
            List<Integer> currentGames = user.getTrackedGameIds();
            
            // Добавляем только те игры, которых ещё нет
            for (Integer gameId : gameIds)
            {
                if (!currentGames.contains(gameId))
                {
                    currentGames.add(gameId);
                }
            }
            
            // Обновляем пользователя в списке
            users.remove(user); // Удаляем старую версию
            users.add(user);    // Добавляем обновлённую
            
            // Сохраняем изменения
            JsonHelp.writeUsers(users);
            
            System.out.println("✓ Игры добавлены пользователю " + user.getUsername());
            System.out.println("  Теперь отслеживает " + currentGames.size() + " игр");
        }
    }
    
    // Метод для получения пользователя по ID
    public static User getUserById(int id)
    {
        for (User user : users)
        {
            if (user.getId() == id)
            {
                return user;
            }
        }
        return null;
    }

    public static User createUser(String username, String password, String email)
    {
        if (userExists(username))
        {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        
        User user = new User(
            userIdCounter.getAndIncrement(),
            username,
            password,
            email,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null
        );
        
        users.add(user);
        JsonHelp.writeUsers(users);
        return user;
    }
    
    // === МЕТОДЫ ДЛЯ РАБОТЫ С ДРУЗЬЯМИ ===

    public static boolean removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        
        if (user != null && friend != null) {
            // Удаляем друга из списка пользователя
            boolean removedFromUser = user.getFriendIds().remove(Integer.valueOf(friendId));
            
            // В двусторонней системе друзей также нужно удалить пользователя из списка друга
            boolean removedFromFriend = friend.getFriendIds().remove(Integer.valueOf(userId));
            
            if (removedFromUser || removedFromFriend) {
                // Сохраняем изменения
                JsonHelp.writeUsers(users);
                System.out.println("✓ Друг удален: пользователь " + user.getUsername() + 
                                " удалил " + friend.getUsername());
                return true;
            }
        }
        
        return false;
    }

    public static List<User> getFriendsList(int userId) {
        User user = getUserById(userId);
        List<User> friends = new ArrayList<>();
        
        if (user != null) {
            for (Integer friendId : user.getFriendIds()) {
                User friend = getUserById(friendId);
                if (friend != null) {
                    friends.add(friend);
                }
            }
        }
        
        return friends;
    }

    public static Map<String, Object> getFriendsStatistics(int userId) {
        Map<String, Object> stats = new HashMap<>();
        List<User> friends = getFriendsList(userId);
        
        stats.put("total", friends.size());
        
        // Подсчитываем количество друзей с общими играми
        User currentUser = getUserById(userId);
        int mutualGamesCount = 0;
        
        if (currentUser != null) {
            for (User friend : friends) {
                if (hasMutualGames(currentUser, friend)) {
                    mutualGamesCount++;
                }
            }
        }
        
        stats.put("withMutualGames", mutualGamesCount);
        
        // Примерная статистика (в реальном приложении было бы из базы данных)
        stats.put("online", (int) (friends.size() * 0.3)); // 30% онлайн
        stats.put("activeThisWeek", (int) (friends.size() * 0.7)); // 70% активны на неделе
        
        return stats;
    }

    private static boolean hasMutualGames(User user1, User user2) {
        List<Integer> games1 = user1.getTrackedGameIds();
        List<Integer> games2 = user2.getTrackedGameIds();
        
        for (Integer gameId : games1) {
            if (games2.contains(gameId)) {
                return true;
            }
        }
        
        return false;
    }

    public static List<User> getAllUsers()
    {
        return new ArrayList<>(users);
    }
    
    // === МЕТОДЫ ДЛЯ ИГР ===
    public static Game createGame(String title, String genre, String description)
    {
        Game game = new Game(
            gameIdCounter.getAndIncrement(),
            title,
            genre,
            description,
            null
        );
        
        games.add(game);
        JsonHelp.writeGames(games);
        return game;
    }
    
    public static Game getGameById(int id)
    {
        for (Game game : games)
        {
            if (game.getId() == id)
            {
                return game;
            }
        }
        return null;
    }
    
    public static List<Game> getAllGames()
    {
        return new ArrayList<>(games);
    }
    
    public static List<Game> getGamesByIds(List<Integer> gameIds)
    {
        List<Game> result = new ArrayList<>();
        for (Integer id : gameIds)
        {
            Game game = getGameById(id);
            if (game != null)
            {
                result.add(game);
            }
        }
        return result;
    }
    
    // === МЕТОДЫ ДЛЯ СООБЩЕСТВ ===
    public static Community createCommunity(String name, int gameId, String description, int adminId)
    {
        Community community = new Community(
            communityIdCounter.getAndIncrement(),
            name,
            gameId,
            description,
            adminId,
            new ArrayList<>(),
            true
        );
        
        // Автоматически добавляем создателя в участники
        community.getMemberIds().add(adminId);
        
        communities.add(community);
        JsonHelp.writeCommunities(communities);
        return community;
    }
    
    public static List<Community> getAllCommunities()
    {
        return new ArrayList<>(communities);
    }
    
    public static List<Community> getCommunitiesForGame(int gameId)
    {
        List<Community> result = new ArrayList<>();
        for (Community community : communities)
        {
            if (community.getGameId() == gameId && community.isPublic())
            {
                result.add(community);
            }
        }
        return result;
    }

    public static Community getCommunityById(int id) 
    {
        for (Community community : communities) 
        {
            if (community.getId() == id) 
            {
                return community;
            }
        }
        return null;
    }

    public static List<Community> getUserCommunities(int userId) 
    {
        List<Community> userCommunities = new ArrayList<>();
        User user = getUserById(userId);
        
        if (user != null) 
        {
            for (Integer communityId : user.getCommunityIds()) 
            {
                Community community = getCommunityById(communityId);
                if (community != null) 
                {
                    userCommunities.add(community);
                }
            }
        }
        
        return userCommunities;
    }

    public static boolean joinCommunity(int userId, int communityId) 
    {
        User user = getUserById(userId);
        Community community = getCommunityById(communityId);
        
        if (user != null && community != null) 
        {
            // Проверяем, не состоит ли уже пользователь в сообществе
            if (!user.getCommunityIds().contains(communityId)) 
            {
                user.getCommunityIds().add(communityId);
                
                // Также добавляем пользователя в список участников сообщества
                if (!community.getMemberIds().contains(userId)) 
                {
                    community.getMemberIds().add(userId);
                }
                
                // Сохраняем изменения
                JsonHelp.writeUsers(users);
                JsonHelp.writeCommunities(communities);
                
                System.out.println("✓ Пользователь " + user.getUsername() + 
                                " вступил в сообщество " + community.getName());
                return true;
            } 
            else 
            {
                System.out.println("⚠ Пользователь уже состоит в этом сообществе");
                return false;
            }
        }
        
        return false;
    }

    public static boolean leaveCommunity(int userId, int communityId) 
    {
        User user = getUserById(userId);
        Community community = getCommunityById(communityId);
        
        if (user != null && community != null) 
            {
            // Нельзя покинуть сообщество, если ты администратор
            if (community.getAdminId() == userId) {
                System.out.println("⚠ Администратор не может покинуть сообщество");
                return false;
            }
            
            // Удаляем сообщество из списка пользователя
            user.getCommunityIds().remove(Integer.valueOf(communityId));
            
            // Удаляем пользователя из списка участников сообщества
            community.getMemberIds().remove(Integer.valueOf(userId));
            
            // Сохраняем изменения
            JsonHelp.writeUsers(users);
            JsonHelp.writeCommunities(communities);
            
            System.out.println("✓ Пользователь " + user.getUsername() + 
                            " покинул сообщество " + community.getName());
            return true;
        }
        
        return false;
    }

    public static List<Community> getRecommendedCommunities(int userId) 
    {
        User user = getUserById(userId);
        List<Community> recommended = new ArrayList<>();
        
        if (user != null) 
        {
            // Получаем все сообщества по играм, которые отслеживает пользователь
            for (Integer gameId : user.getTrackedGameIds()) 
            {
                List<Community> gameCommunities = getCommunitiesForGame(gameId);
                
                // Фильтруем: только публичные и те, в которых пользователь еще не состоит
                for (Community community : gameCommunities) 
                {
                    if (community.isPublic() && !user.getCommunityIds().contains(community.getId())) 
                    {
                        recommended.add(community);
                    }
                }
            }
        }
        
        return recommended;
    }

    // === МЕТОДЫ ДЛЯ АНКЕТ ===
    public static ProfileRequest createProfileRequest(int authorId, int gameId, String title, String message)
    {
        ProfileRequest request = new ProfileRequest(
            requestIdCounter.getAndIncrement(),
            authorId,
            gameId,
            title,
            message,
            "open",
            java.time.LocalDateTime.now(),
            new ArrayList<>()
        );
        
        profileRequests.add(request);
        JsonHelp.writeProfileRequests(profileRequests);
        return request;
    }
    
    public static List<ProfileRequest> getAllProfileRequests()
    {
        return new ArrayList<>(profileRequests);
    }
    
    public static List<ProfileRequest> getProfileRequestsForGame(int gameId)
    {
        List<ProfileRequest> result = new ArrayList<>();
        for (ProfileRequest request : profileRequests)
        {
            if (request.getGameId() == gameId && "open".equals(request.getStatus()))
            {
                result.add(request);
            }
        }
        return result;
    }
    
    // === МЕТОДЫ ДЛЯ ТУРНИРОВ ===
    public static Tournament createTournament(
        String title,
        int gameId,
        String description,
        java.time.LocalDateTime startDate,
        java.time.LocalDateTime endDate,
        String prizeInfo,
        boolean isGlobal,
        int creatorId)
    {
        Tournament tournament = new Tournament(
            tournamentIdCounter.getAndIncrement(),
            title,
            gameId,
            description,
            startDate,
            endDate,
            prizeInfo,
            isGlobal,
            creatorId,
            0, // communityId - для глобальных 0
            new ArrayList<>(),
            "upcoming"
        );
        
        tournaments.add(tournament);
        JsonHelp.writeTournaments(tournaments);
        return tournament;
    }
    
    public static List<Tournament> getAllTournaments()
    {
        return new ArrayList<>(tournaments);
    }
    
    public static List<Tournament> getTournamentsForGame(int gameId)
    {
        List<Tournament> result = new ArrayList<>();
        for (Tournament tournament : tournaments)
        {
            if (tournament.getGameId() == gameId && "upcoming".equals(tournament.getStatus()))
            {
                result.add(tournament);
            }
        }
        return result;
    }

    public static Tournament getTournamentById(int id) {
        for (Tournament tournament : tournaments) {
            if (tournament.getId() == id) {
                return tournament;
            }
        }
        return null;
    }

    public static List<Tournament> getTournamentsByCreator(int creatorId) {
        List<Tournament> result = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            if (tournament.getCreatorId() == creatorId) {
                result.add(tournament);
            }
        }
        // Сортируем по дате начала (сначала ближайшие)
        result.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
        return result;
    }

    public static List<Tournament> getTournamentsByParticipant(int participantId) {
        List<Tournament> result = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            if (tournament.getParticipantIds().contains(participantId)) {
                result.add(tournament);
            }
        }
        // Сортируем по дате начала (сначала ближайшие)
        result.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
        return result;
    }

    public static boolean joinTournament(int userId, int tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        User user = getUserById(userId);
        
        if (tournament != null && user != null && "upcoming".equals(tournament.getStatus())) {
            // Проверяем, не участвует ли уже пользователь
            if (!tournament.getParticipantIds().contains(userId)) {
                tournament.getParticipantIds().add(userId);
                JsonHelp.writeTournaments(tournaments);
                System.out.println("✓ Пользователь " + user.getUsername() + 
                                " вступил в турнир: " + tournament.getTitle());
                return true;
            } else {
                System.out.println("⚠ Пользователь уже участвует в этом турнире");
                return false;
            }
        }
        return false;
    }

    public static boolean leaveTournament(int userId, int tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        User user = getUserById(userId);
        
        if (tournament != null && user != null && "upcoming".equals(tournament.getStatus())) {
            // Удаляем пользователя из списка участников
            boolean removed = tournament.getParticipantIds().remove(Integer.valueOf(userId));
            if (removed) {
                JsonHelp.writeTournaments(tournaments);
                System.out.println("✓ Пользователь " + user.getUsername() + 
                                " покинул турнир: " + tournament.getTitle());
                return true;
            }
        }
        return false;
    }

    public static boolean updateTournamentStatus(int tournamentId, String status) {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament != null) {
            tournament.setStatus(status);
            JsonHelp.writeTournaments(tournaments);
            System.out.println("✓ Статус турнира " + tournament.getTitle() + " изменен на: " + status);
            return true;
        }
        return false;
    }

    public static boolean deleteTournament(int tournamentId, int creatorId) {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament != null && tournament.getCreatorId() == creatorId) {
            boolean removed = tournaments.remove(tournament);
            if (removed) {
                JsonHelp.writeTournaments(tournaments);
                System.out.println("✓ Турнир удален: " + tournament.getTitle());
                return true;
            }
        }
        return false;
    }
    
    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    public static void logout()
    {
        currentUser = null;
    }
    
    public static void refreshData()
    {
        loadAllData();
    }
    
    // Метод для тестирования - очистка всех данных
    public static void clearAllData()
    {
        users.clear();
        games.clear();
        communities.clear();
        profileRequests.clear();
        tournaments.clear();
        
        userIdCounter.set(1);
        gameIdCounter.set(1);
        communityIdCounter.set(1);
        requestIdCounter.set(1);
        tournamentIdCounter.set(1);
        
        currentUser = null;
        
        JsonHelp.clearAllData();
        initializeTestData();
    }

    // === ДОБАВЛЯЕМ НОВЫЕ МЕТОДЫ ДЛЯ РАБОТЫ С АНКЕТАМИ ===

    public static ProfileRequest getProfileRequestById(int id) {
        for (ProfileRequest request : profileRequests) {
            if (request.getId() == id) {
                return request;
            }
        }
        return null;
    }

    public static List<ProfileRequest> getProfileRequestsByAuthor(int authorId) {
        List<ProfileRequest> result = new ArrayList<>();
        for (ProfileRequest request : profileRequests) {
            if (request.getAuthorId() == authorId) {
                result.add(request);
            }
        }
        // Сортируем по дате создания (сначала новые)
        result.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
        return result;
    }

    public static List<ProfileRequest> getProfileRequestsByApplicant(int applicantId) {
        List<ProfileRequest> result = new ArrayList<>();
        for (ProfileRequest request : profileRequests) {
            if (request.getApplicantIds().contains(applicantId)) {
                result.add(request);
            }
        }
        // Сортируем по дате создания (сначала новые)
        result.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
        return result;
    }

    public static List<ProfileRequest> getProfileRequestsByGame(int gameId) {
        List<ProfileRequest> result = new ArrayList<>();
        for (ProfileRequest request : profileRequests) {
            if (request.getGameId() == gameId && "open".equals(request.getStatus())) {
                result.add(request);
            }
        }
        return result;
    }

    public static boolean applyToProfileRequest(int userId, int requestId) {
        ProfileRequest request = getProfileRequestById(requestId);
        User user = getUserById(userId);
        
        if (request != null && user != null && "open".equals(request.getStatus())) {
            // Проверяем, не откликался ли уже пользователь
            if (!request.getApplicantIds().contains(userId)) {
                request.getApplicantIds().add(userId);
                JsonHelp.writeProfileRequests(profileRequests);
                System.out.println("✓ Пользователь " + user.getUsername() + 
                                " откликнулся на анкету: " + request.getTitle());
                return true;
            } else {
                System.out.println("⚠ Пользователь уже откликался на эту анкету");
                return false;
            }
        }
        return false;
    }

    public static boolean cancelApplication(int userId, int requestId) {
        ProfileRequest request = getProfileRequestById(requestId);
        User user = getUserById(userId);
        
        if (request != null && user != null) {
            // Удаляем пользователя из списка откликнувшихся
            boolean removed = request.getApplicantIds().remove(Integer.valueOf(userId));
            if (removed) {
                JsonHelp.writeProfileRequests(profileRequests);
                System.out.println("✓ Пользователь " + user.getUsername() + 
                                " отозвал отклик на анкету: " + request.getTitle());
                return true;
            }
        }
        return false;
    }

    public static boolean updateProfileRequestStatus(int requestId, String status) {
        ProfileRequest request = getProfileRequestById(requestId);
        if (request != null) {
            request.setStatus(status);
            JsonHelp.writeProfileRequests(profileRequests);
            System.out.println("✓ Статус анкеты " + request.getTitle() + " изменен на: " + status);
            return true;
        }
        return false;
    }

    public static boolean deleteProfileRequest(int requestId, int authorId) {
        ProfileRequest request = getProfileRequestById(requestId);
        if (request != null && request.getAuthorId() == authorId) {
            boolean removed = profileRequests.remove(request);
            if (removed) {
                JsonHelp.writeProfileRequests(profileRequests);
                System.out.println("✓ Анкета удалена: " + request.getTitle());
                return true;
            }
        }
        return false;
    }
}
