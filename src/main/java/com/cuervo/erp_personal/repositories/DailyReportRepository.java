package com.cuervo.erp_personal.repositories;
import com.cuervo.erp_personal.models.DailyReport;
import com.cuervo.erp_personal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
public interface DailyReportRepository extends JpaRepository<DailyReport, Long>{
    Optional<DailyReport> findByDate(LocalDate date);
    Optional<DailyReport> findByDateAndUser(LocalDate date, User user);
}
