package com.easychat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easychat.entity.Message;
import com.easychat.repository.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(String senderId, String receiverId, String content) {
        Message message = new Message(senderId, receiverId, content);
        return messageRepository.save(message);
    }

    public List<Message> getChatHistory(String userId1, String userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(userId1, userId2);
    }

    public List<Message> getUnreadMessages(String receiverId) {
        return messageRepository.findByReceiverIdAndReadFalse(receiverId);
    }

    public void markMessagesAsRead(String receiverId) {
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndReadFalse(receiverId);
        unreadMessages.forEach(msg -> msg.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }
}
