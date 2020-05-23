package com.toptal.toptal.backend.repository;

import com.toptal.toptal.backend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Meal CRUD Jpa
 *
 * @author ehab
 */
public interface MealRepository extends JpaRepository<Meal, Long> {
}
