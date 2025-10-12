package com.example.demo_habit.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String description;

    // ref "habit_status_enum" later
    private String status;

    private boolean star;

    private String tags;

    private Character periodType;

    private long periodCount;

    private long targetCount;

    private long orderIndex;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // Business methods
    public void archive() {
        this.status = "ARCHIVED";
    }

    public void softDelete() {
        this.status = "SOFT_DELETED";
    }

    public void update(String title, String description, String tags, Boolean star,
                      Character periodType, Long periodCount, Long targetCount) {
        this.title = title;
        this.description = description;
        this.tags = tags != null ? tags : "";
        this.star = star != null ? star : false;
        this.periodType = periodType;
        this.periodCount = periodCount != null ? periodCount : 0;
        this.targetCount = targetCount != null ? targetCount : 0;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}

