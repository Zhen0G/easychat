package com.easychat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.easychat.entity.Message;

public interface MessageRepository extends MongoRepository<Message, String> {

    // 获取两个用户之间的聊天记录（时间升序）
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampAsc(String senderId, String receiverId);

    // 获取未读消息
    List<Message> findByReceiverIdAndReadFalse(String receiverId);

    // 获取最新 50 条消息（按时间降序）
    List<Message> findTop50BySenderIdOrReceiverIdOrderByTimestampDesc(String senderId, String receiverId);

    // 获取两个用户之间的聊天记录（支持分页）
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            String senderId, String receiverId, 
            String receiverId2, String senderId2, 
            Pageable pageable);

    // 获取某个用户的所有聊天记录（支持分页）
    List<Message> findBySenderIdOrReceiverId(String senderId, String receiverId, Pageable pageable);
}
