package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductEntity p set p.stock = p.stock - :quantity where p.id = :id and p.stock >= :quantity")
    int decrementStock(@Param("id") String id, @Param("quantity") int quantity);

}
