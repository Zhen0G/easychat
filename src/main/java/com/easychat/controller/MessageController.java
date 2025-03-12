package com.easychat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easychat.entity.Message;
import com.easychat.service.MessageService;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestParam String senderId,
                                               @RequestParam String receiverId,
                                               @RequestParam String content) {
        return ResponseEntity.ok(messageService.sendMessage(senderId, receiverId, content));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChatHistory(@RequestParam String userId1, @RequestParam String userId2) {
        return ResponseEntity.ok(messageService.getChatHistory(userId1, userId2));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestParam String receiverId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(receiverId));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<String> markMessagesAsRead(@RequestParam String receiverId) {
        messageService.markMessagesAsRead(receiverId);
        return ResponseEntity.ok("Unread messages marked as read");
    }
}
