package gamer_hub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tournament
{
    private int id;
    private String title;
    private int gameId;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String prizeInfo;
    private boolean isGlobal;
    private int creatorId;
    private int communityId;
    private List<Integer> participantIds;
    private String status;

    @JsonCreator
    public Tournament(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("gameId") int gameId,
            @JsonProperty("description") String description,
            @JsonProperty("startDate") LocalDateTime startDate,
            @JsonProperty("endDate") LocalDateTime endDate,
            @JsonProperty("prizeInfo") String prizeInfo,
            @JsonProperty("isGlobal") boolean isGlobal,
            @JsonProperty("creatorId") int creatorId,
            @JsonProperty("communityId") int communityId,
            @JsonProperty("participantIds") List<Integer> participantIds,
            @JsonProperty("status") String status)
    {
        this.id = id;
        this.title = title;
        this.gameId = gameId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizeInfo = prizeInfo;
        this.isGlobal = isGlobal;
        this.creatorId = creatorId;
        this.communityId = communityId;
        this.participantIds = participantIds != null ? participantIds : new ArrayList<>();
        this.status = status != null ? status : "upcoming";
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

    public LocalDateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate)
    {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate)
    {
        this.endDate = endDate;
    }

    public String getPrizeInfo()
    {
        return prizeInfo;
    }

    public void setPrizeInfo(String prizeInfo)
    {
        this.prizeInfo = prizeInfo;
    }

    public boolean isGlobal()
    {
        return isGlobal;
    }

    public void setGlobal(boolean isGlobal)
    {
        this.isGlobal = isGlobal;
    }

    public int getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(int creatorId)
    {
        this.creatorId = creatorId;
    }

    public int getCommunityId()
    {
        return communityId;
    }

    public void setCommunityId(int communityId)
    {
        this.communityId = communityId;
    }

    public List<Integer> getParticipantIds()
    {
        return participantIds;
    }

    public void setParticipantIds(List<Integer> participantIds)
    {
        this.participantIds = participantIds;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
