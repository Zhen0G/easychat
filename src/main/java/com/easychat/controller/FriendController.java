package com.easychat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easychat.dto.FriendRequest;
import com.easychat.entity.Friend;
import com.easychat.service.FriendService;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    @Autowired
    private FriendService friendService;

    /**
     * 发送好友请求
     */
    @PostMapping("/send-request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest request) {
        return ResponseEntity.ok(friendService.sendFriendRequest(request.getSenderId(), request.getReceiverId()));
    }

    /**
     * 接受好友请求
     */
    @PostMapping("/accept-request")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody FriendRequest request) {
        return ResponseEntity.ok(friendService.acceptFriendRequest(request.getSenderId(), request.getReceiverId()));
    }

    /**
     * 拒绝好友请求
     */
    @PostMapping("/reject-request")
    public ResponseEntity<String> rejectFriendRequest(@RequestBody FriendRequest request) {
        return ResponseEntity.ok(friendService.rejectFriendRequest(request.getSenderId(), request.getReceiverId()));
    }

    /**
     * 获取好友列表
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Friend>> getFriends(@PathVariable String userId) {
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFriend(
        @RequestParam String userId, 
        @RequestParam String friendId) {
        
        return ResponseEntity.ok(friendService.removeFriend(userId, friendId));
    }

}
