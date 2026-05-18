package com.devsync.devsync_server.message.model;

import com.devsync.devsync_server.message.channel.Channel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    // Populated ONLY when type == MessageType.CODE (e.g., "java", "javascript")
    @Column(name = "code_language")
    private String codeLanguage;

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // Kept as raw ID to preserve loose service coupling

    // Many messages belong to one channel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    // Populated ONLY if it's a 1:1 direct message session
    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}