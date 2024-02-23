package com.example.falful.repo;

import com.example.falful.entity.Items;
import com.example.falful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemsRepo extends JpaRepository<Items, Integer> {
    @Query(value = "select * from items where price=?1", nativeQuery = true)
    Optional<User> findByPrice(String price);

    @Query(value = "SELECT * FROM items ORDER BY RANDOM() LIMIT 6", nativeQuery = true)
    List<Items> findFourRandomData();
}
