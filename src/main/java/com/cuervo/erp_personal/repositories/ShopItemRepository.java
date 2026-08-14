package com.cuervo.erp_personal.repositories;

import com.cuervo.erp_personal.models.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
}
