package com.easychat.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_messages")
public class GroupMessage {
    @Id
    private String id;
    private String groupId;
    private String senderId;
    private String content;
    private Instant timestamp;

    public GroupMessage(String groupId, String senderId, String content) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public String getGroupId() { return groupId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
