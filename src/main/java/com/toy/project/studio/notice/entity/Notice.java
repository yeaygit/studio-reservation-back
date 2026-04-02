package com.toy.project.studio.notice.entity;

import java.time.LocalDate;

import com.toy.project.studio.config.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("pk")
    private Long id;

    @Comment("Notice title")
    @Column(nullable = false, length = 200)
    private String title;

    @Comment("Notice content")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Comment("Active flag")
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Comment("Popup flag")
    @Column(name = "is_popup", nullable = false)
    @Builder.Default
    private boolean isPopup = false;

    @Comment("Popup start date")
    @Column(name = "popup_start_date")
    private LocalDate popupStartDate;

    @Comment("Popup end date")
    @Column(name = "popup_end_date")
    private LocalDate popupEndDate;

    public void update(
            String title,
            String content,
            boolean isPopup,
            LocalDate popupStartDate,
            LocalDate popupEndDate
    ) {
        this.title = title;
        this.content = content;
        this.isPopup = isPopup;
        this.popupStartDate = popupStartDate;
        this.popupEndDate = popupEndDate;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
