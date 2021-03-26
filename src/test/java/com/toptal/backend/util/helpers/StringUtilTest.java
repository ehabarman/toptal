package com.toptal.backend.util.helpers;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class StringUtilTest {

    @DataProvider
    public static Object[][] isNullOrWhiteSpaceDataProvider() {
        return new Object[][]{
                {null, true},
                {"", true},
                {" ", true},
                {"   ", true},
                {"\t", true},
                {"     a", false},
                {"b      ", false},
                {"   c   ", false}
        };
    }

    @UseDataProvider("isNullOrWhiteSpaceDataProvider")
    @Test
    public void isNullOrWhiteSpaceTest(String text, boolean expected) {
        boolean actualResult = StringUtil.isNullOrWhiteSpace(text);
        Assert.assertEquals(expected, actualResult);
    }

    @DataProvider
    public static Object[][] isNullOrEmptyDataProvider() {
        return new Object[][]{
                {null, true},
                {"", true},
                {" ", false},
                {"   ", false},
                {"\t", false},
                {"     a", false},
                {"b      ", false},
                {"   c   ", false}
        };
    }

    @UseDataProvider("isNullOrWhiteSpaceDataProvider")
    @Test
    public void isNullOrEmptyTest(String text, boolean expected) {
        boolean actualResult = StringUtil.isNullOrWhiteSpace(text);
        Assert.assertEquals(expected, actualResult);
    }

    @DataProvider
    public static Object[][] isntNullNorWhiteSpaceDataProvider() {
        return new Object[][]{
                {null, false},
                {"", false},
                {" ", false},
                {"   ", false},
                {"\t", false},
                {"     a", true},
                {"b      ", true},
                {"   c   ", true}
        };
    }

    @UseDataProvider("isntNullNorWhiteSpaceDataProvider")
    @Test
    public void isntNullNorWhiteSpaceTest(String text, boolean expected) {
        boolean actualResult = StringUtil.isntNullNorWhiteSpace(text);
        Assert.assertEquals(expected, actualResult);
    }

    @DataProvider
    public static Object[][] appendAllDataProvider() {
        return new Object[][]{
                {new String[]{}, ""},
                {new String[]{null, "", null}, ""},
                {new String[]{"a", "b", "c", "d"}, "abcd"},
                {new String[]{null, "a", " ", null, "b"}, "a b"},
        };
    }

    @UseDataProvider("appendAllDataProvider")
    @Test
    public void appendAllTest(String[] data, String expected) {
        String actualResult = StringUtil.appendAll(data);
        Assert.assertEquals(expected, actualResult);
    }

    @DataProvider
    public static Object[][] formatDataProvider() {
        return new Object[][]{
                {"%s, %s, %s", new String[]{"a", "b", "c"}, "a, b, c"},
                {"%s, test, %s", new String[]{"a", "c"}, "a, test, c"},
                {"test", new String[]{}, "test"},
                {"%s %s %s", new String[]{"a", "b", "c"}, "a b c"},
        };
    }

    @UseDataProvider("formatDataProvider")
    @Test
    public void formatAllTest(String format, String[] data, String expected) {
        String actualResult = StringUtil.format(format, data);
        Assert.assertEquals(expected, actualResult);
    }

}
