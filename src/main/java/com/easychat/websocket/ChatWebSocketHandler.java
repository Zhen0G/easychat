package com.easychat.websocket;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.easychat.entity.Friend;
import com.easychat.entity.FriendRequest;
import com.easychat.entity.Group;
import com.easychat.entity.GroupMessage;
import com.easychat.entity.Message;
import com.easychat.repository.FriendRepository;
import com.easychat.repository.FriendRequestRepository;
import com.easychat.repository.GroupMessageRepository;
import com.easychat.repository.GroupRepository;
import com.easychat.repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        System.out.println("✅ 用户已连接: " + userId);
        sessions.put(userId, session);

        // 发送未读消息和聊天记录
        sendChatHistory(userId, session);

        // 发送未处理的好友请求
        List<FriendRequest> pendingRequests = friendRequestRepository.findByReceiverIdAndStatus(userId, "PENDING");
        for (FriendRequest request : pendingRequests) {
            sendJsonMessage(userId, "friend_request", request.getSenderId(), "");
        }

        // 通知所有好友：用户上线
        notifyFriendsOnlineStatus(userId, true);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = getUserId(session);
        System.out.println("📩 收到消息: " + message.getPayload() + " 来自: " + senderId);

        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            String type = jsonNode.get("type").asText();

            switch (type) {
                case "message":
                    handleChatMessage(jsonNode, senderId);
                    break;
                case "friend_request":
                    handleFriendRequest(jsonNode, senderId);
                    break;
                case "friend_accept":
                    handleFriendAccept(jsonNode, senderId);
                    break;
                case "get_history":
                    getChatHistory(jsonNode, senderId);
                    break;
                case "file":
                    handleFileMessage(jsonNode, senderId);
                    break;
                case "group_message":
                    handleGroupMessage(jsonNode, senderId);
                    break;
                
                default:
                    System.err.println("❌ 未知消息类型: " + type);
            }
        } catch (Exception e) {
            System.err.println("❌ 消息解析失败: " + e.getMessage());
        }
    }

    private void handleFileMessage(JsonNode jsonNode, String senderId) throws Exception {
        String receiverId = jsonNode.get("receiverId").asText();
        String fileUrl = jsonNode.get("fileUrl").asText();
        String timestamp = Instant.now().toString();
    
        // 存储到数据库
        Message message = new Message(senderId, receiverId, fileUrl);
        messageRepository.save(message);
    
        // 发送 WebSocket 消息
        if (sessions.containsKey(receiverId)) {
            sendJsonMessage(receiverId, "file", senderId, fileUrl, timestamp);
        }
    }
    
    private void handleChatMessage(JsonNode jsonNode, String senderId) throws Exception {
        String receiverId = jsonNode.get("receiverId").asText();
        String text = jsonNode.get("message").asText();
        String timestamp = Instant.now().toString();

        // 存储消息
        Message message = new Message(senderId, receiverId, text);
        messageRepository.save(message);

        if (sessions.containsKey(receiverId)) {
            sendJsonMessage(receiverId, "message", senderId, text, timestamp);
        }
    }

    private void handleGroupMessage(JsonNode jsonNode, String senderId) throws Exception {
        String groupId = jsonNode.get("groupId").asText();
        String text = jsonNode.get("message").asText();

        // 存储消息
        GroupMessage message = new GroupMessage(groupId, senderId, text);
        groupMessageRepository.save(message);

        // 获取群成员并发送消息
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            for (String memberId : group.getMembers()) {
                if (!memberId.equals(senderId) && sessions.containsKey(memberId)) {
                    sendJsonMessage(memberId, "group_message", senderId, text);
                }
            }
        }
    }

    private void handleFriendRequest(JsonNode jsonNode, String senderId) throws Exception {
        String receiverId = jsonNode.get("receiverId").asText();
        String createdAt = Instant.now().toString();

        if (friendRequestRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            System.out.println("⚠️ 已发送好友请求，忽略...");
            return;
        }

        FriendRequest friendRequest = new FriendRequest(senderId, receiverId, "PENDING", createdAt);
        friendRequestRepository.save(friendRequest);

        if (sessions.containsKey(receiverId)) {
            sendJsonMessage(receiverId, "friend_request", senderId, "");
        }
    }

    private void handleFriendAccept(JsonNode jsonNode, String senderId) throws Exception {
        String receiverId = jsonNode.get("receiverId").asText();
        String createdAt = Instant.now().toString();

        Optional<FriendRequest> requestOpt = friendRequestRepository.findBySenderIdAndReceiverId(receiverId, senderId);
        if (requestOpt.isPresent()) {
            FriendRequest request = requestOpt.get();
            request.setStatus("ACCEPTED");
            friendRequestRepository.save(request);

            // 添加好友
            friendRepository.save(new Friend(senderId, receiverId, createdAt));
            friendRepository.save(new Friend(receiverId, senderId, createdAt));

            sendJsonMessage(receiverId, "friend_accepted", senderId, "");
            sendJsonMessage(senderId, "friend_accepted", receiverId, "");
        } else {
            System.out.println("❌ 未找到好友请求");
        }
    }

    private void getChatHistory(JsonNode jsonNode, String userId) throws Exception {
        String friendId = jsonNode.get("friendId").asText();
        
        // 获取最近 50 条聊天记录（分页）
        List<Message> history = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            userId, friendId, friendId, userId, PageRequest.of(0, 50)
        );

        if (history.isEmpty()) {
            sendJsonMessage(userId, "system", "server", "No chat history found.");
            return;
        }

        for (Message msg : history) {
            sendJsonMessage(userId, "message", msg.getSenderId(), msg.getContent(), msg.getTimestamp().toString());
        }
    }

    private void sendChatHistory(String userId, WebSocketSession session) throws Exception {
        // 先发送未读消息
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndReadFalse(userId);
        for (Message msg : unreadMessages) {
            sendJsonMessage(userId, "message", msg.getSenderId(), msg.getContent(), msg.getTimestamp().toString());
            msg.setRead(true);
            messageRepository.save(msg);
        }

        // 再发送历史消息
        getChatHistory(objectMapper.readTree("{\"friendId\": \"all\"}"), userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            notifyFriendsOnlineStatus(userId, false);
        }
        System.out.println("❌ 用户断开连接: " + userId + " 状态: " + status);
    }

    private String getUserId(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query != null && query.contains("=")) {
                return query.split("=")[1];
            }
        } catch (Exception e) {
            System.err.println("⚠️ 获取用户 ID 失败: " + e.getMessage());
        }
        return "anonymous";
    }

    private void notifyFriendsOnlineStatus(String userId, boolean isOnline) {
        List<Friend> friends = friendRepository.findByUserId(userId);
        String status = isOnline ? "online" : "offline";

        for (Friend friend : friends) {
            sendJsonMessage(friend.getFriendId(), "friend_status", userId, status);
        }
    }

    private void sendJsonMessage(String receiverId, String type, String from, String content) {
        sendJsonMessage(receiverId, type, from, content, Instant.now().toString());
    }

    private void sendJsonMessage(String receiverId, String type, String from, String content, String timestamp) {
        WebSocketSession session = sessions.get(receiverId);
        if (session != null && session.isOpen()) {
            try {
                String json = String.format("{\"type\": \"%s\", \"from\": \"%s\", \"content\": \"%s\", \"timestamp\": \"%s\"}", type, from, content, timestamp);
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
