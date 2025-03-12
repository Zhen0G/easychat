package com.easychat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easychat.entity.Group;
import com.easychat.service.GroupService;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    // 创建群聊
    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(groupService.createGroup(request.get("groupName"), request.get("ownerId")));
    }

    // 添加群成员
    @PostMapping("/add-member")
    public ResponseEntity<String> addMember(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(groupService.addMember(request.get("groupId"), request.get("userId")));
    }

    // 移除群成员
    @PostMapping("/remove-member")
    public ResponseEntity<String> removeMember(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(groupService.removeMember(request.get("groupId"), request.get("userId")));
    }

    // 获取用户所有的群聊
    @GetMapping("/{userId}")
    public ResponseEntity<List<Group>> getUserGroups(@PathVariable String userId) {
        return ResponseEntity.ok(groupService.getUserGroups(userId));
    }

    // 获取群成员
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    // 修改群信息（名称/公告）
    @PostMapping("/update")
    public ResponseEntity<String> updateGroupInfo(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(groupService.updateGroupInfo(request.get("groupId"), request.get("groupName"), request.get("announcement")));
    }

    // 删除群聊
    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.deleteGroup(groupId));
    }

    // 设置/取消管理员
    @PostMapping("/set-admin")
    public ResponseEntity<String> setAdmin(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(groupService.setAdmin(request.get("groupId"), request.get("userId"), Boolean.parseBoolean(request.get("isAdmin"))));
    }
}
