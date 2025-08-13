package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.ShippingAddress;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    List<ShippingAddress> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);

    Optional<ShippingAddress> findByUserAndIsDefaultTrue(User user);

    @Modifying
    @Query("UPDATE ShippingAddress sa SET sa.isDefault = false WHERE sa.user = :user")
    void clearDefaultForUser(@Param("user") User user);

    @Query("SELECT COUNT(sa) FROM ShippingAddress sa WHERE sa.user = :user")
    long countByUser(@Param("user") User user);
}
