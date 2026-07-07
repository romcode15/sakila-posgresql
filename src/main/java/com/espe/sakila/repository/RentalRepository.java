package com.espe.sakila.repository;

import com.espe.sakila.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByUserUserIdAndReturnDateIsNull(Integer userId);

    List<Rental> findByUserUserId(Integer userId);

    List<Rental> findByReturnDateIsNull();
}
