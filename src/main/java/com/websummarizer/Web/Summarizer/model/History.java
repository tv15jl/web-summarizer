package com.websummarizer.Web.Summarizer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Domain object for Affiliate (represents a row in table "history")
 */
@Entity
@Table(name = "history")
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    public History(String historyContent){
        this.historyContent=historyContent;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HID")
    private Long id;

    @Column(name = "history_content", length = 100000)
    private String historyContent;

    @Column(name = "input_content", length = 100000)
    private String inputText;

    @Column(name = "short_link", length = 1000)
    private String short_link;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "UID")
    private User user;


}