package com.example.falful.repo;

import com.example.falful.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer> {
    @Query(value = "SELECT * FROM customerorder where order_user_Id=?1", nativeQuery = true)
    List<Order> findorderById(Integer id);
}
