package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {

    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") String id);

    @EntityGraph(attributePaths = "items")
    List<OrderEntity> findByUserId(String userId);

}
