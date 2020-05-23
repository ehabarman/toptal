package com.toptal.toptal.backend.service.data;

import com.toptal.toptal.backend.model.Record;
import com.toptal.toptal.backend.model.User;
import com.toptal.toptal.backend.repository.RecordRepository;
import com.toptal.toptal.backend.util.helpers.DateTimeUtil;
import com.toptal.toptal.backend.util.helpers.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.List;

/**
 * Record entity service
 *
 * @author ehab
 */
@Component
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    private final JdbcTemplate jdbcTemplate;

    public RecordService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Record findRecordByUserAndDate(User user, LocalDate date) {
        return recordRepository.findRecordByUserAndDate(user, date);
    }

    /**
     * Get all records belonging to the given user
     */
    public List<Record> findAllRecordByUser(User user) {
        if (user == null) {
            return null;
        }
        return recordRepository.findAllByUser(user);
    }

    /**
     * Get all records belonging to the given user
     * Supports paging
     */
    public List<Record> findAllRecordByUser(User user, Integer pageNumber, Integer pageSize, boolean doPaging) {
        if (user == null) {
            return null;
        }
        if(doPaging) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return recordRepository.findAllByUser(user, pageable).getContent();
        }
        return recordRepository.findAllByUser(user);
    }

    public Record findRecordByDate(LocalDate date) {
        return recordRepository.findRecordByDate(date);
    }

    public Record findRecordByUserAndId(User user, long recordId) {
        return recordRepository.findRecordByUserAndId(user, recordId);
    }

    public Record createRecord(User user, LocalDate date) {
        Record newRecord = new Record(date, user);
        return recordRepository.save(newRecord);
    }

    /**
     * Executes a typed query for records
     */
    public List<Record> executeDynamicQuery(long userId, String query, Integer pageNumber, Integer pageSize,
                                            boolean doPaging) {
        String queryWhereClause;
        if (StringUtil.isNullOrEmpty(query)) {
            queryWhereClause = StringUtil.format("WHERE user_id = %s",userId);
        }
        else {
            queryWhereClause = StringUtil.format("WHERE user_id = %s AND (%s)",userId, query);
        }

        String completeQuery = StringUtil.format("SELECT * FROM records %s", queryWhereClause);
        if(doPaging) {
            completeQuery = StringUtil.format("%s LIMIT %s, %s", completeQuery, pageNumber * pageSize, pageSize);
        }

        try {
            return jdbcTemplate.query(completeQuery, (rs, rowNum) -> {
                Record record = new Record();
                record.setDate(DateTimeUtil.buildDate(rs.getString("date")));
                record.setId(Long.parseLong(rs.getString("id")));
                return record;
            });
        } catch (Exception e) {
            throw new RuntimeException("Invalid Search query");
        }
    }

}
