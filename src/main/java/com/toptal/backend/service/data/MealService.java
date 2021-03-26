package com.toptal.backend.service.data;

import com.toptal.backend.model.Record;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.model.Meal;
import com.toptal.backend.payload.response.MealResponse;
import com.toptal.backend.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@link Meal} entity service
 *
 * @author ehab
 */
@Component
public class MealService extends BaseService {

    private static final String TEXT = "text";
    private static final String ID = "id";
    private static final String TIME = "time";
    private static final String CALORIES = "calories";

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Meal save(Meal meal) {
        return mealRepository.save(meal);
    }

    public Meal findMealById(Long mealId) {
        return mealRepository.findMealById(mealId);
    }

    public Meal findMealByRecordAndId(Record record, Long id) {
        return mealRepository.findMealByRecordAndId(record, id);
    }

    public boolean existsByRecordAndId(Record record, Long mealId) {
        return mealRepository.existsByRecordAndId(record, mealId);
    }

    public void delete(Meal meal) {
        mealRepository.delete(meal);
    }

    /**
     * Executes a native query for a record's meals
     *
     * @param whereClause Is used as a custom where clause
     * @param doPaging    Indicates if {@param pageNumber} and {@param pageSize} should be used for paging the query result
     */
    public List<MealResponse> executeDynamicMealsQuery(Record record, String whereClause, Integer pageNumber, Integer pageSize,
                                                       boolean doPaging) {
        String completeQuery = StringUtil.format(
                "SELECT id, time, calories, text " +
                        "FROM ( " +
                        "SELECT id, time, calories, text " +
                        "FROM meals " +
                        "WHERE record_id = %s " +
                        "ORDER BY id ASC) record_meals", record.getId());

        completeQuery = appendWhereClause(completeQuery, whereClause);
        completeQuery = appendPagingClause(completeQuery, pageNumber, pageSize, doPaging);

        try {
            return jdbcTemplate.query(completeQuery, (rs, rowNum) -> {
                MealResponse meal = new MealResponse();
                meal.setTime(rs.getString(TIME));
                meal.setId(Long.parseLong(rs.getString(ID)));
                meal.setCalories(Integer.parseInt(rs.getString(CALORIES)));
                meal.setText(rs.getString(TEXT));
                return meal;
            });
        } catch (Exception e) {
            badSqlGrammerCustomHandle(e);
            throw new RuntimeException("Invalid Search query");
        }
    }
}
