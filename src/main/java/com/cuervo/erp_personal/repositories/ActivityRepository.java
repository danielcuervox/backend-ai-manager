package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{
}
