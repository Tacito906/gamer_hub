package gamer_hub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Community
{
    private int id;
    private String name;
    private int gameId;
    private String description;
    private int adminId;
    private List<Integer> memberIds;
    private boolean isPublic;

    @JsonCreator
    public Community(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("description") String description,
            @JsonProperty("adminId") int adminId,
            @JsonProperty("memberIds") List<Integer> memberIds,
            @JsonProperty("isPublic") boolean isPublic)
    {
        this.id = id;
        this.name = name;
        this.gameId = gameId;
        this.description = description;
        this.adminId = adminId;
        this.memberIds = memberIds != null ? memberIds : new ArrayList<>();
        this.isPublic = isPublic;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getGameId()
    {
        return gameId;
    }

    public void setGameId(int gameId)
    {
        this.gameId = gameId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getAdminId()
    {
        return adminId;
    }

    public void setAdminId(int adminId)
    {
        this.adminId = adminId;
    }

    public List<Integer> getMemberIds()
    {
        return memberIds;
    }

    public void setMemberIds(List<Integer> memberIds)
    {
        this.memberIds = memberIds;
    }

    public boolean isPublic()
    {
        return isPublic;
    }

    public void setPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }
}