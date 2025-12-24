package gamer_hub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Game
{
    private int id;
    private String title;
    private String genre;
    private String description;
    private String imageUrl;

    @JsonCreator
    public Game(@JsonProperty("id") int id, @JsonProperty("title") String title, @JsonProperty("genre") String genre, @JsonProperty("description") String description, @JsonProperty("imageUrl") String imageUrl)
    {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
}