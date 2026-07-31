package com.cuervo.erp_personal.models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports")
@Data // <--- Lombok hace la magia
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;
    @Enumerated(EnumType.STRING)
    private ReportStyle usedStyle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public String getPhoto() {
        if (this.usedStyle != null) {
            return this.usedStyle.getPhoto();
        }
        return "default.png";
    }
}
