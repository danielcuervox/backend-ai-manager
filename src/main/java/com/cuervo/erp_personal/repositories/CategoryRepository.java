package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
