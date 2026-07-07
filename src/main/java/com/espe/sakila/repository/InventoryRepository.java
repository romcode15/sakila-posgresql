package com.espe.sakila.repository;

import com.espe.sakila.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.film.filmId = :filmId " +
           "AND i.inventoryId NOT IN " +
           "(SELECT r.inventory.inventoryId FROM Rental r WHERE r.returnDate IS NULL)")
    Optional<Inventory> findFirstAvailableByFilmId(@Param("filmId") Integer filmId);

    long countByFilmFilmId(Integer filmId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.inventory.film.filmId = :filmId AND r.returnDate IS NULL")
    long countActiveRentalsByFilmId(@Param("filmId") Integer filmId);

    List<Inventory> findByFilmFilmId(Integer filmId);
}
