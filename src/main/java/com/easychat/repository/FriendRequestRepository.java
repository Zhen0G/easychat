package com.easychat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.easychat.entity.FriendRequest;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, String status);
    Optional<FriendRequest> findBySenderIdAndReceiverId(String senderId, String receiverId);
    boolean existsBySenderIdAndReceiverId(String senderId, String receiverId);
}
