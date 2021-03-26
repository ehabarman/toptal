package com.toptal.backend.repository;

import com.toptal.backend.model.Record;
import com.toptal.backend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Meal CRUD Jpa
 *
 * @author ehab
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    Meal findMealById(Long id);

    Meal findMealByRecordAndId(Record record, Long id);

    boolean existsByRecordAndId(Record record, Long id);
}
