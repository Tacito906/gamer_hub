package gamer_hub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class User
{
    private int id;
    private String username;
    private String password;
    private String email;
    private List<Integer> trackedGameIds;
    private List<Integer> communityIds;
    private List<Integer> friendIds;
    private String avatarUrl;

    @JsonCreator
    public User(
            @JsonProperty("id") int id,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("email") String email,
            @JsonProperty("trackedGameIds") List<Integer> trackedGameIds,
            @JsonProperty("communityIds") List<Integer> communityIds,
            @JsonProperty("friendIds") List<Integer> friendIds,
            @JsonProperty("avatarUrl") String avatarUrl)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.trackedGameIds = trackedGameIds != null ? trackedGameIds : new ArrayList<>();
        this.communityIds = communityIds != null ? communityIds : new ArrayList<>();
        this.friendIds = friendIds != null ? friendIds : new ArrayList<>();
        this.avatarUrl = avatarUrl;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public List<Integer> getTrackedGameIds()
    {
        return trackedGameIds;
    }

    public void setTrackedGameIds(List<Integer> trackedGameIds)
    {
        this.trackedGameIds = trackedGameIds;
    }

    public List<Integer> getCommunityIds()
    {
        return communityIds;
    }

    public void setCommunityIds(List<Integer> communityIds)
    {
        this.communityIds = communityIds;
    }

    public List<Integer> getFriendIds()
    {
        return friendIds;
    }

    public void setFriendIds(List<Integer> friendIds)
    {
        this.friendIds = friendIds;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }
}