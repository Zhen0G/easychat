package com.easychat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "friends") // 在 MongoDB 中存储为 friends 集合
public class Friend {
    @Id
    private String id;
    private String userId;
    private String friendId;
    private String createdAt;

    public Friend(String userId, String friendId, String createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
