package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.models.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUser(User user);
}
