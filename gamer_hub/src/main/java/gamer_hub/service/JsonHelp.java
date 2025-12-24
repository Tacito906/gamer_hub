package gamer_hub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import gamer_hub.model.*;

public class JsonHelp
{
    private static final String DATA_DIR = "data";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static
    {
        mapper.registerModule(new JavaTimeModule());
        createDataDirectory();
    }
    
    private static void createDataDirectory()
    {
        File dir = new File(DATA_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
            System.out.println("Создана директория для данных: " + DATA_DIR);
        }
    }
    
    // Общие методы для чтения/записи списков
    public static <T> List<T> readList(String filename, Class<T> clazz)
    {
        File file = new File(DATA_DIR + "/" + filename);
        if (!file.exists())
        {
            return new ArrayList<>();
        }
        
        try
        {
            return mapper.readValue(file, 
                mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        }
        catch (IOException e)
        {
            System.err.println("Ошибка чтения файла " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static <T> void writeList(String filename, List<T> list)
    {
        try
        {
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                new File(DATA_DIR + "/" + filename), list);
            System.out.println("Данные сохранены в " + filename);
        }
        catch (IOException e)
        {
            System.err.println("Ошибка записи в файл " + filename + ": " + e.getMessage());
        }
    }
    
    // Методы для каждой сущности
    public static List<User> readUsers()
    {
        return readList("users.json", User.class);
    }
    
    public static void writeUsers(List<User> users)
    {
        writeList("users.json", users);
    }
    
    public static List<Game> readGames()
    {
        return readList("games.json", Game.class);
    }
    
    public static void writeGames(List<Game> games)
    {
        writeList("games.json", games);
    }
    
    public static List<Community> readCommunities()
    {
        return readList("communities.json", Community.class);
    }
    
    public static void writeCommunities(List<Community> communities)
    {
        writeList("communities.json", communities);
    }
    
    public static List<ProfileRequest> readProfileRequests()
    {
        return readList("profile_requests.json", ProfileRequest.class);
    }
    
    public static void writeProfileRequests(List<ProfileRequest> requests)
    {
        writeList("profile_requests.json", requests);
    }
    
    public static List<Tournament> readTournaments()
    {
        return readList("tournaments.json", Tournament.class);
    }
    
    public static void writeTournaments(List<Tournament> tournaments)
    {
        writeList("tournaments.json", tournaments);
    }
    
    // Метод для очистки всех данных (для тестирования)
    public static void clearAllData()
    {
        String[] files = {
            "users.json",
            "games.json", 
            "communities.json",
            "profile_requests.json",
            "tournaments.json"
        };
        
        for (String file : files)
        {
            try
            {
                Files.deleteIfExists(Paths.get(DATA_DIR + "/" + file));
            }
            catch (IOException e)
            {
                System.err.println("Ошибка удаления файла " + file + ": " + e.getMessage());
            }
        }
        System.out.println("Все данные очищены");
    }
}
