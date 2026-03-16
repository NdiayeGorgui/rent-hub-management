package com.smartiadev.messaging_service.service;

import com.smartiadev.messaging_service.client.UserClient;
import com.smartiadev.messaging_service.dto.ConversationResponse;
import com.smartiadev.messaging_service.dto.MessageRequest;
import com.smartiadev.messaging_service.dto.MessageResponse;
import com.smartiadev.messaging_service.dto.SendMessageRequest;
import com.smartiadev.messaging_service.entity.Conversation;
import com.smartiadev.messaging_service.entity.Message;
import com.smartiadev.messaging_service.repository.ConversationRepository;
import com.smartiadev.messaging_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserClient userClient;

    public MessageResponse sendMessage(UUID senderId, MessageRequest request) {

        UUID receiverId = request.receiverId();

        // 1️⃣ vérifier conversation
        Conversation conversation = conversationRepository
                .findConversationBetweenUsersAndItem(senderId, receiverId, request.itemId())
                .orElseGet(() -> {

                    Conversation newConversation = Conversation.builder()
                            .user1Id(senderId)
                            .user2Id(receiverId)
                            .itemId(request.itemId())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return conversationRepository.save(newConversation);
                });

        // 3️⃣ créer message
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .itemId(request.itemId())
                .conversation(conversation)
                .content(request.content())
                .timestamp(LocalDateTime.now())
                .read(false)
                .supportMessage(false)
                .build();

        messageRepository.save(message);

        // 4️⃣ mettre à jour la conversation (IMPORTANT)
        conversation.setLastMessage(request.content());
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastSenderId(senderId);

        conversationRepository.save(conversation);

        return new MessageResponse(
                message.getId(),
                conversation.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getItemId(),
                message.getContent(),
                message.getTimestamp(),
                message.isRead()
        );
    }

    public List<MessageResponse> getConversationMessages(Long conversationId) {

        return messageRepository
                .findByConversationIdOrderByTimestampAsc(conversationId)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getConversation().getId(),
                        m.getSenderId(),
                        m.getReceiverId(),
                        m.getItemId(),
                        m.getContent(),
                        m.getTimestamp(),
                        m.isRead()
                ))
                .toList();
    }

    public List<ConversationResponse> getUserConversations(UUID userId) {

        List<Conversation> conversations =
                conversationRepository
                        .findByUser1IdOrUser2IdOrderByLastMessageAtDesc(userId, userId);

        return conversations.stream()
                .map(conversation -> {

                    long unread = messageRepository
                            .countByConversationIdAndReceiverIdAndReadFalse(
                                    conversation.getId(),
                                    userId
                            );

                    var user1 = userClient.getUserById(conversation.getUser1Id());
                    var user2 = userClient.getUserById(conversation.getUser2Id());

                    return new ConversationResponse(
                            conversation.getId(),
                            conversation.getUser1Id(),
                            conversation.getUser2Id(),
                            user1.username(),
                            user2.username(),
                            conversation.getItemId(),
                            conversation.getLastMessage(),
                            conversation.getLastMessageAt(),
                            conversation.getLastSenderId(),
                            unread
                    );

                })
                .toList();
    }

    public void markAsRead(Long messageId) {

        messageRepository.findById(messageId).ifPresent(message -> {
            System.out.println("MARK AS READ called for message: " + messageId);

            message.setRead(true);

            messageRepository.save(message);

        });

    }

    public Message sendSupportMessage(UUID senderId, MessageRequest request) {

        // UUID du support (admin)
        UUID supportId = UUID.fromString("5b2c28b5-a71a-4699-bd8e-36871037c778");

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(supportId)
                .itemId(null)
                .content(request.content())
                .timestamp(LocalDateTime.now())
                .read(false)
                .supportMessage(true)
                .build();

        return messageRepository.save(message);

    }

    public long getUnreadMessagesCount(UUID userId) {

        return messageRepository.countByReceiverIdAndReadFalse(userId);

    }

    public MessageResponse sendMessage(SendMessageRequest request) {

        UUID senderId = getCurrentUserId();

        Conversation conversation = conversationRepository
                .findConversationBetweenUsersAndItem(
                        senderId,
                        request.getReceiverId(),
                        request.getItemId()
                )
                .orElseGet(() -> {

                    Conversation newConv = new Conversation();
                    newConv.setUser1Id(senderId);
                    newConv.setUser2Id(request.getReceiverId());
                    newConv.setItemId(request.getItemId());
                    newConv.setCreatedAt(LocalDateTime.now());

                    return conversationRepository.save(newConv);
                });

        Message message = new Message();

        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setItemId(request.getItemId());
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setSupportMessage(false);

        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                conversation.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getItemId(),
                message.getContent(),
                message.getTimestamp(),
                message.isRead()
        );
    }

    private UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return UUID.fromString(authentication.getName());
    }
}