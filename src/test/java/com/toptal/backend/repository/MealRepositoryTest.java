package com.toptal.backend.repository;

import com.toptal.backend.model.Meal;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import com.toptal.backend.util.helpers.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MealRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private MealRepository mealRepository;

    @Test
    public void addRecordsToUsers_deleteRecordFromUsers() {
        User user1 = new User("test1", "test1");
        userRepository.save(user1);
        LocalDate date1 = DateTimeUtil.buildDate("1996-6-12");
        LocalDate date2 = DateTimeUtil.buildDate("1996-6-13");
        Record record1 = new Record(date1, user1);
        Record record2 = new Record(date2, user1);
        recordRepository.save(record1);
        recordRepository.save(record2);
        LocalTime time1 = DateTimeUtil.buildTime("4:50");
        LocalTime time2 = DateTimeUtil.buildTime("5:50");
        Meal meal1 = new Meal(time1, 0, "meal1", record1);
        Meal meal2 = new Meal(time2, 0, "meal2", record2);
        mealRepository.save(meal1);
        mealRepository.save(meal2);

        Assert.assertTrue(mealRepository.existsByRecordAndId(record1, 1L));
        Assert.assertTrue(mealRepository.existsByRecordAndId(record2, 2L));

        mealRepository.delete(mealRepository.findMealById(1L));
        mealRepository.delete(mealRepository.findMealByRecordAndId(record2, 2L));

        Assert.assertFalse(mealRepository.existsByRecordAndId(record1, 1L));
        Assert.assertFalse(mealRepository.existsByRecordAndId(record2, 2L));
    }

}
