package com.easychat.controller;

import com.easychat.entity.Message;
import com.easychat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 获取两个用户之间的聊天记录（支持分页）
     */
    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestParam String senderId, 
            @RequestParam String receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Message> messages = messageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                        senderId, receiverId, receiverId, senderId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
                );

        return ResponseEntity.ok(messages);
    }

    /**
     * 获取某个用户的所有聊天记录（按时间排序）
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Message>> getUserMessages(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Message> messages = messageRepository
                .findBySenderIdOrReceiverId(
                        userId, userId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
                );

        return ResponseEntity.ok(messages);
    }

    /**
     * 删除消息（可选功能）
     */
    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {
        messageRepository.deleteById(messageId);
        return ResponseEntity.ok("消息已删除");
    }
}
