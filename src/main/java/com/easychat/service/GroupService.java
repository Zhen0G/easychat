package com.easychat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easychat.entity.Group;
import com.easychat.repository.GroupRepository;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    public Group createGroup(String groupName, String ownerId) {
        Group group = new Group(groupName, ownerId);
        return groupRepository.save(group);
    }

    public String addMember(String groupId, String userId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            group.addMember(userId);
            groupRepository.save(group);
            return "✅ 成员 " + userId + " 加入群聊";
        }
        return "❌ 群组不存在";
    }

    public String removeMember(String groupId, String userId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            group.removeMember(userId);
            groupRepository.save(group);
            return "✅ 成员 " + userId + " 被移出群聊";
        }
        return "❌ 群组不存在";
    }

    public List<Group> getUserGroups(String userId) {
        return groupRepository.findByMembersContaining(userId);
    }

    public List<String> getGroupMembers(String groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        return groupOpt.map(group -> new ArrayList<>(group.getMembers())).orElse(null);
    }


    public String updateGroupInfo(String groupId, String groupName, String announcement) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            group.setGroupName(groupName);
            group.setAnnouncement(announcement);
            groupRepository.save(group);
            return "✅ 群信息已更新";
        }
        return "❌ 群组不存在";
    }

    public String deleteGroup(String groupId) {
        if (groupRepository.existsById(groupId)) {
            groupRepository.deleteById(groupId);
            return "✅ 群聊已删除";
        }
        return "❌ 群组不存在";
    }

    public String setAdmin(String groupId, String userId, boolean isAdmin) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            if (isAdmin) {
                group.addAdmin(userId);
            } else {
                group.removeAdmin(userId);
            }
            groupRepository.save(group);
            return "✅ 成员 " + userId + (isAdmin ? " 已被设为管理员" : " 已被取消管理员权限");
        }
        return "❌ 群组不存在";
    }
}
