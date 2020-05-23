package com.toptal.toptal.backend.repository;

import com.toptal.toptal.backend.model.Record;
import com.toptal.toptal.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Record CRUD Jpa
 *
 * @author ehab
 */
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByUser(User user);

    Record findRecordByDate(LocalDate date);

    Record findRecordByUserAndDate(User user, LocalDate date);

    Record findRecordByUserAndId(User user, Long id);

    Page<Record> findAllByUser(User user, Pageable pageable);

}
