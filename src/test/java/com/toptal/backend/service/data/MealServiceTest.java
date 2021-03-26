package com.toptal.backend.service.data;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Meal;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.response.MealResponse;
import com.toptal.backend.util.helpers.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MealServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private MealService mealService;

    @Test
    public void createFindDeleteTest() {
        User user = userService.saveUser(new User("user", "password"));
        LocalDate date = DateTimeUtil.buildDate("1996-6-12");
        Record record = recordService.saveRecord(user, date);
        LocalTime time = DateTimeUtil.buildTime("5:5");
        Meal meal = new Meal(time, 0, "test", record);
        meal = mealService.save(meal);
        Assert.assertNotNull(mealService.findMealById(meal.getId()));
        Assert.assertNotNull(mealService.findMealByRecordAndId(record, meal.getId()));
        mealService.delete(meal);
        Assert.assertNull(mealService.findMealById(meal.getId()));
        Assert.assertNull(mealService.findMealByRecordAndId(record, meal.getId()));
    }

    @DataProvider
    public static Object[][] executeDynamicMealsQueryDataProvider() {
        User user = new User("queryTestUser", "password");
        Record record = new Record(DateTimeUtil.buildDate("1996-6-12"), user);
        List<Meal> meals = Arrays.asList(
                new Meal(DateTimeUtil.buildTime("5:1"), 1, "test1", record),
                new Meal(DateTimeUtil.buildTime("5:2"), 2, "test2", record),
                new Meal(DateTimeUtil.buildTime("5:3"), 3, "test3", record),
                new Meal(DateTimeUtil.buildTime("5:4"), 4, "test4", record),
                new Meal(DateTimeUtil.buildTime("5:5"), 5, "test5", record)
        );

        return new Object[][]{
                {record, "", null, null, false, user, 5, false, "", meals},
                {record, "", 0, 3, true, user, 3, false, "", meals},
                {record, "", 1, 3, true, user, 2, false, "", meals},
                {record, "time > '05:2:0'", null, null, false, user, 3, false, "", meals},
                {record, "time != '5:03:0'", null, null, false, user, 4, false, "", meals},
                {record, "time eq '5:00'", null, null, false, user, 0, true, "BadWhereGrammerException", meals},
                {record, "time2 != '5:1'", null, null, false, user, 0, true, "BadWhereGrammerException", meals}
        };
    }


    @UseDataProvider("executeDynamicMealsQueryDataProvider")
    @Test
    public void executeDynamicMealsQueryTest(Record record, String whereClause, Integer pageNumber, Integer pageSize, boolean doPaging,
                                             User user, int expected, boolean shouldThrowException, String exceptionClassName,
                                             List<Meal> meals) {
        initMeals(user, record, meals);
        try {
            List<MealResponse> result = mealService.executeDynamicMealsQuery(record, whereClause, pageNumber, pageSize, doPaging);
            Assert.assertFalse(shouldThrowException);
            Assert.assertEquals(expected, result.size());
        } catch (Exception exception) {
            Assert.assertTrue(shouldThrowException);
            Assert.assertEquals(exception.getClass().getSimpleName(), exceptionClassName);
        }

    }

    private static boolean areMealsInitialized = false;

    private void initMeals(User user, Record record, List<Meal> meals) {
        if (areMealsInitialized) {
            return;
        }
        areMealsInitialized = true;
        userService.saveUser(user);
        recordService.saveRecord(record);
        for (Meal meal : meals) {
            mealService.save(meal);
        }
    }

}
