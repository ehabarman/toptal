package com.toptal.backend.util.helpers;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.toptal.backend.errors.CustomExceptions.InvalidDateException;
import com.toptal.backend.errors.CustomExceptions.InvalidTimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(DataProviderRunner.class)
public class DateTimeUtilTest {

    @DataProvider
    public static Object[][] buildDateDataProvider() {
        return new Object[][]{
                {"1996-6-12", "1996-06-12",false},
                {"2100-1-1", "2100-01-01",false},
                {"invalid", null,true},
                {"19-19-19", null,true},
                {"1999-7-35", null,true},
                {"1999-16-1", null,true},
        };
    }

    @UseDataProvider("buildDateDataProvider")
    @Test
    public void buildDateDataTest(String data, String expected, boolean throwsException) {
        try {
            LocalDate localDate = DateTimeUtil.buildDate(data);
            if(throwsException) {
                Assert.fail("Should throw exception");
            }
            Assert.assertEquals(expected, localDate.toString());
        }
        catch (Exception e) {
            if(!throwsException) {
                Assert.fail("Shouldn't throw exception");
            }
            Assert.assertTrue(e instanceof InvalidDateException);
        }
    }

    @DataProvider
    public static Object[][] buildTimeDataProvider() {
        return new Object[][]{
                {"1:1", "01:01",false},
                {"16:59", "16:59",false},
                {"invalid", null,true},
                {"50", null,true},
                {"25:00", null,true},
                {"0:61", null,true},
        };
    }

    @UseDataProvider("buildTimeDataProvider")
    @Test
    public void buildTimeTest(String data, String expected, boolean throwsException) {
        try {
            LocalTime localDate = DateTimeUtil.buildTime(data);
            if(throwsException) {
                Assert.fail("Should throw exception");
            }
            Assert.assertEquals(expected, localDate.toString());
        }
        catch (Exception e) {
            if(!throwsException) {
                Assert.fail("Shouldn't throw exception");
            }
            Assert.assertTrue(e instanceof InvalidTimeException);
        }
    }
}
