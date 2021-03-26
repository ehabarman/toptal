package com.toptal.backend.repository;

import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Record CRUD Jpa
 *
 * @author ehab
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    Record findRecordByUserAndDate(User user, LocalDate date);

    Record findRecordByUserAndId(User user, Long id);

}
