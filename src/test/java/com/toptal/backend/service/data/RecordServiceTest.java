package com.toptal.backend.service.data;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.DTO.data.RecordDTO;
import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import com.toptal.backend.util.helpers.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RecordServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @Test
    public void createFindDeleteTest() {
        User user = new User("user", "password");
        userService.saveUser(user);
        LocalDate date1 = DateTimeUtil.buildDate("1992-12-12");
        Record record1 = recordService.saveRecord(user, date1);
        LocalDate date2 = DateTimeUtil.buildDate("1992-12-13");
        Record record2 = recordService.saveRecord(user, date2);

        Record findById1 = recordService.findRecordByUserAndId(user, record1.getId());
        Record findById2 = recordService.findRecordByUserAndId(user, record2.getId());
        Assert.assertEquals(record1.getDate(), findById1.getDate());
        Assert.assertEquals(record2.getDate(), findById2.getDate());

        Record findByDate1 = recordService.findRecordByUserAndDate(user, date1);
        Record findByDate2 = recordService.findRecordByUserAndDate(user, date2);
        Assert.assertEquals(record1.getDate(), findByDate1.getDate());
        Assert.assertEquals(record2.getDate(), findByDate2.getDate());
    }

    @DataProvider
    public static Object[][] executeDynamicRecordsQueryDataProvider() {
        User user = new User("userQueryTest", "password");
        List<LocalDate> dates = Arrays.asList(
                DateTimeUtil.buildDate("1996-6-1"),
                DateTimeUtil.buildDate("1996-6-2"),
                DateTimeUtil.buildDate("1996-6-3"),
                DateTimeUtil.buildDate("1996-6-4"),
                DateTimeUtil.buildDate("1996-6-5")
        );
        return new Object[][]{
                {user, "", null, null, false, dates, 5, false, ""},
                {user, "", 0, 3, true, dates, 3, false, ""},
                {user, "", 1, 3, true, dates, 2, false, ""},
                {user, "date = '1996-6-1'", null, null, false, dates, 1, false, ""},
                {user, "daily_limit = 'true'", null, null, false, dates, 5, false, ""},
                {user, "date > '1996-6-2'", null, null, false, dates, 3, false, ""},
                {user, "date2 > '1996-6-2'", null, null, false, dates, 3, true, "BadWhereGrammerException"},
                {user, "date eq '1996-6-2'", null, null, false, dates, 3, true, "BadWhereGrammerException"},
        };
    }

    @UseDataProvider("executeDynamicRecordsQueryDataProvider")
    @Test
    public void executeDynamicRecordsQueryTest(User user, String whereClause, Integer pageNumber, Integer pageSize,
                                               boolean doPaging, List<LocalDate> dates, int expected,
                                               boolean shouldThrowException, String exceptionClassName) {
        initRecords(user, dates);
        try {
            List<RecordDTO> result = recordService.executeDynamicRecordsQuery(user, whereClause, pageNumber, pageSize, doPaging);
            Assert.assertFalse(shouldThrowException);
            Assert.assertEquals(expected, result.size());
        } catch (Exception exception) {
            Assert.assertTrue(shouldThrowException);
            Assert.assertEquals(exception.getClass().getSimpleName(), exceptionClassName);
        }


    }

    private static boolean isInitialized = false;

    private void initRecords(User user, List<LocalDate> dates) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        userService.saveUser(user);
        for (LocalDate date : dates) {
            recordService.saveRecord(user, date);
        }
    }
}
