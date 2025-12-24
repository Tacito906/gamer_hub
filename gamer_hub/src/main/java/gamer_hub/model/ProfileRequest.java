package gamer_hub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProfileRequest
{
    private int id;
    private int authorId;
    private int gameId;
    private String title;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private List<Integer> applicantIds;

    @JsonCreator
    public ProfileRequest(
            @JsonProperty("id") int id,
            @JsonProperty("authorId") int authorId,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("status") String status,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("applicantIds") List<Integer> applicantIds)
    {
        this.id = id;
        this.authorId = authorId;
        this.gameId = gameId;
        this.title = title;
        this.message = message;
        this.status = status != null ? status : "open";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.applicantIds = applicantIds != null ? applicantIds : new ArrayList<>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(int authorId)
    {
        this.authorId = authorId;
    }

    public int getGameId()
    {
        return gameId;
    }

    public void setGameId(int gameId)
    {
        this.gameId = gameId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public List<Integer> getApplicantIds()
    {
        return applicantIds;
    }

    public void setApplicantIds(List<Integer> applicantIds)
    {
        this.applicantIds = applicantIds;
    }
}
