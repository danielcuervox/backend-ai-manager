package com.cuervo.erp_personal.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Enumerated(EnumType.STRING)
    private ReportStyle usedStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getPhoto() {
        if (this.usedStyle != null) {
            return this.usedStyle.getPhoto();
        }
        return "default.png";
    }
}
