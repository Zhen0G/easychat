package com.easychat.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups")
public class Group {
    @Id
    private String id;
    private String groupName;
    private String ownerId;
    private Set<String> members = new HashSet<>();
    private Set<String> admins = new HashSet<>();
    private String announcement;

    public Group(String groupName, String ownerId) {
        this.groupName = groupName;
        this.ownerId = ownerId;
        this.members.add(ownerId); // 创建者自动加入
    }

    public String getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Set<String> getMembers() {
        return members;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public void addMember(String userId) {
        this.members.add(userId);
    }

    public void removeMember(String userId) {
        this.members.remove(userId);
        this.admins.remove(userId); // 移除管理员权限
    }

    public void addAdmin(String userId) {
        if (members.contains(userId)) {
            this.admins.add(userId);
        }
    }

    public void removeAdmin(String userId) {
        this.admins.remove(userId);
    }
}
