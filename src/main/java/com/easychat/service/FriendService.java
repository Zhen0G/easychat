package com.easychat.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easychat.entity.Friend;
import com.easychat.entity.FriendRequest;
import com.easychat.repository.FriendRepository;
import com.easychat.repository.FriendRequestRepository;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    /**
     * 发送好友请求
     */
    public String sendFriendRequest(String senderId, String receiverId) {
        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    
        if (existingRequest.isPresent()) {
            // ✅ 如果好友请求已存在，更新时间
            FriendRequest request = existingRequest.get();
            request.setCreatedAt(Instant.now().toString());
            request.setStatus("PENDING"); // 确保状态正确
            friendRequestRepository.save(request);
            return "好友请求已更新!";
        }
    
        // ✅ 如果没有请求，则创建新的
        FriendRequest newRequest = new FriendRequest(senderId, receiverId, "PENDING", Instant.now().toString());
        friendRequestRepository.save(newRequest);
        return "好友请求发送成功!";
    }
    
    
    /**
     * 接受好友请求
     */
    public String acceptFriendRequest(String senderId, String receiverId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);

        if (friendRequestOpt.isPresent()) {
            FriendRequest friendRequest = friendRequestOpt.get();
            friendRequest.setStatus("ACCEPTED");
            friendRequestRepository.save(friendRequest);

            // 双向添加好友
            Friend friend1 = new Friend(senderId, receiverId, Instant.now().toString());
            Friend friend2 = new Friend(receiverId, senderId, Instant.now().toString());
            friendRepository.save(friend1);
            friendRepository.save(friend2);

            // 删除好友请求
            friendRequestRepository.delete(friendRequest);

            return "好友请求已接受!";
        }
        return "好友请求未找到!";
    }

    /**
     * 拒绝好友请求
     */
    public String rejectFriendRequest(String senderId, String receiverId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);

        if (friendRequestOpt.isPresent()) {
            friendRequestRepository.delete(friendRequestOpt.get());
            return "好友请求已拒绝!";
        }
        return "好友请求未找到!";
    }

    /**
     * 获取用户的好友列表
     */
    public List<Friend> getFriends(String userId) {
        return friendRepository.findByUserId(userId);
    }

    /**
     * 删除好友
     */
    public String removeFriend(String userId, String friendId) {
        List<Friend> friendships = friendRepository.findByUserId(userId);

        boolean removed = friendships.removeIf(f -> f.getFriendId().equals(friendId));
        if (removed) {
            friendRepository.deleteByUserIdAndFriendId(userId, friendId);
            friendRepository.deleteByUserIdAndFriendId(friendId, userId);
            return "好友已删除!";
        }
        return "该好友不存在!";
    }
}
