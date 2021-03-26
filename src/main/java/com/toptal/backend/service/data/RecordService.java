package com.toptal.backend.service.data;

import com.toptal.backend.model.Record;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.DTO.data.RecordDTO;
import com.toptal.backend.model.User;
import com.toptal.backend.repository.RecordRepository;
import com.toptal.backend.util.helpers.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RecordService extends BaseService {

    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TOTAL_CALORIES = "total_calories";
    private static final String DAILY_LIMIT = "daily_limit";

    @Autowired
    private RecordRepository recordRepository;

    private final JdbcTemplate jdbcTemplate;

    public RecordService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Record findRecordByUserAndDate(User user, LocalDate date) {
        return recordRepository.findRecordByUserAndDate(user, date);
    }

    public void delete(Record record) {
        if(record != null) {
            recordRepository.deleteById(record.getId());
        }
    }

    public Record findRecordByUserAndId(User user, long recordId) {
        return recordRepository.findRecordByUserAndId(user, recordId);
    }

    public Record saveRecord(User user, LocalDate date) {
        Record newRecord = new Record(date, user);
        return recordRepository.save(newRecord);
    }

    public Record saveRecord(Record record) {
        return recordRepository.save(record);
    }

    /**
     * Executes a native query for a user's records
     * The query returns a list of RecordDTO(id, date, total_calories, daily_limit)
     *
     * @param whereClause Is used as a custom where clause
     * @param doPaging    Indicates if {@param pageNumber} and {@param pageSize} should be used for paging the query result
     */
    public List<RecordDTO> executeDynamicRecordsQuery(User user, String whereClause, Integer pageNumber, Integer pageSize, boolean doPaging) {
        String completeQuery = StringUtil.format(
                "SELECT id, date, total_calories, daily_limit " +
                        "FROM ( " +
                        "SELECT records.id as id, date, " +
                        "COALESCE(SUM(meals.calories), 0) AS total_calories, " +
                        "CASE WHEN COALESCE(SUM(meals.calories), 0) > %s THEN false ELSE true END daily_limit " +
                        "FROM records LEFT JOIN meals ON records.id = meals.record_id " +
                        "WHERE records.user_id = %s " +
                        "GROUP BY records.id, records.date " +
                        "ORDER BY records.id ASC) record_day", user.getCalLimit(), user.getId());

        completeQuery = appendWhereClause(completeQuery, whereClause);
        completeQuery = appendPagingClause(completeQuery, pageNumber, pageSize, doPaging);

        try {
            return jdbcTemplate.query(completeQuery, (rs, rowNum) -> {
                RecordDTO record = new RecordDTO();
                record.setDate(DateTimeUtil.buildDate(rs.getString(DATE)));
                record.setId(Long.parseLong(rs.getString(ID)));
                record.setTotalCalories(Integer.parseInt(rs.getString(TOTAL_CALORIES)));
                record.setLimitFlag(Boolean.valueOf(rs.getString(DAILY_LIMIT)));
                return record;
            });
        } catch (Exception e) {
            badSqlGrammerCustomHandle(e);
            throw new RuntimeException("Invalid Search query");
        }
    }

}
