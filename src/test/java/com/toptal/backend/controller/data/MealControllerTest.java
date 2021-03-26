package com.toptal.backend.controller.data;

import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.Role;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.request.MealRequest;
import com.toptal.backend.payload.response.MealResponse;
import com.toptal.backend.security.RoleType;
import com.toptal.backend.util.helpers.DateTimeUtil;
import com.toptal.backend.util.helpers.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalTime;
import java.util.*;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MealControllerTest extends BaseControllerTest {

    private Record initUsersAndRecords() {
        String username = "user";
        Role useRole = roleService.getRoleByName(RoleType.USER.name());
        User user = new User(username, encoder.encode(PASSWORD), new ArrayList<>(Collections.singletonList(useRole)));
        user = userService.saveUser(user);
        Record record = new Record(DateTimeUtil.buildDate("1996-6-12"), user);
        return recordService.saveRecord(record);
    }

    @Test
    public void createGetAndDeleteMealsTest() {
        Record record = initUsersAndRecords();
        long recordId = record.getId();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";
        LocalTime[] times = {
                DateTimeUtil.buildTime("1:00"),
                DateTimeUtil.buildTime("2:00")
        };

        List<Long> mealsIds = new ArrayList<>();

        for (LocalTime time : times) {
            ResponseEntity<MealResponse> response = postMeal(ownerUsername, recordId, time.toString(), "test", 0, token);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assert.assertNotNull(mealService.findMealById(Objects.requireNonNull(response.getBody()).getId()));
            mealsIds.add(response.getBody().getId());
        }

        setTokenHeader(token);

        for (Long mealId : mealsIds) {
            String getRecordInfoUrl = StringUtil.format(mealPath, port, ownerUsername, recordId, mealId);
            ResponseEntity<MealResponse> response = restTemplate.getForEntity(getRecordInfoUrl, MealResponse.class);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        for (Long mealId : mealsIds) {
            String deleteRecordUrl = StringUtil.format(mealPath, port, ownerUsername, recordId, mealId);
            restTemplate.delete(deleteRecordUrl);
            Assert.assertNull(mealService.findMealById(mealId));
        }

        for (Long mealId : mealsIds) {
            String getRecordInfoUrl = StringUtil.format(mealPath, port, ownerUsername, recordId, mealId);
            ResponseEntity<MealResponse> response = restTemplate.getForEntity(getRecordInfoUrl, MealResponse.class);
            Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

    }

    @Test
    public void invalidRequestsTest() {
        Record record = initUsersAndRecords();
        long recordId = record.getId();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";

        ResponseEntity<MealResponse> missingTimeResponse = postMeal(ownerUsername, recordId, "", "test", 0, token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, missingTimeResponse.getStatusCode());

        ResponseEntity<MealResponse> wrongTime = postMeal(ownerUsername, recordId, "99:99", "test", 0, token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, wrongTime.getStatusCode());

        ResponseEntity<MealResponse> missingText = postMeal(ownerUsername, recordId, "6:6", "", 0, token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, missingText.getStatusCode());

        ResponseEntity<MealResponse> invalidCalories = postMeal(ownerUsername, recordId, "6:6", "calories", -1, token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, invalidCalories.getStatusCode());

        ResponseEntity<MealResponse> unknownFood = postMeal(ownerUsername, recordId, "99:99", "invalidFoodTextForInvalidResult", null, token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, unknownFood.getStatusCode());

        setTokenHeader(token);

        String getRecordInfoUrl = StringUtil.format(mealPath, port, ownerUsername, recordId, 555);
        ResponseEntity<MealResponse> response = restTemplate.getForEntity(getRecordInfoUrl, MealResponse.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        MealRequest mealRequest = new MealRequest();
        ResponseEntity<MealResponse> noMealForPatching = patchMeal(ownerUsername, recordId, mealRequest, token, 555);
        Assert.assertEquals(HttpStatus.NOT_FOUND, noMealForPatching.getStatusCode());

        ResponseEntity<MealResponse> success = postMeal(ownerUsername, recordId, "1:00", "test", 0, token);
        Assert.assertEquals(HttpStatus.OK, success.getStatusCode());

        mealRequest.setCalories(-1);
        ResponseEntity<MealResponse> negativeCaloriesInPatching = patchMeal(ownerUsername, recordId, mealRequest, token, success.getBody().getId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, negativeCaloriesInPatching.getStatusCode());

    }

    @Test
    public void listMealsTest() {
        Record record = initUsersAndRecords();
        long recordId = record.getId();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";

        String[][] timesAndFood = {
                {"1:00", "I ate 1 eggs at breakfast"},
                {"2:00", "I ate 2 eggs at lunch"},
                {"3:00", "I ate 3 eggs at dinner"},
                {"4:00", "I ate 4 eggs"}
        };

        for (String[] timeAndFood : timesAndFood) {
            postMeal(ownerUsername, recordId, timeAndFood[0], timeAndFood[1], null, token);
        }

        setTokenHeader(token);
        String getMealInfoUrl = StringUtil.format(mealsApiPath, port, ownerUsername, recordId);
        ResponseEntity<List> response = restTemplate.getForEntity(getMealInfoUrl, List.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(timesAndFood.length, Objects.requireNonNull(response.getBody()).size());

    }

    @Test
    public void updateMealTest() {
        Record record = initUsersAndRecords();
        long recordId = record.getId();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";

        ResponseEntity<MealResponse> success = postMeal(ownerUsername, recordId, "1:00", "test", 0, token);
        Assert.assertEquals(HttpStatus.OK, success.getStatusCode());

        MealRequest mealRequest = new MealRequest();
        mealRequest.setCalories(1000);
        ResponseEntity<MealResponse> successfulPatch = patchMeal(ownerUsername, recordId, mealRequest, token, success.getBody().getId());
        Assert.assertEquals(HttpStatus.OK, successfulPatch.getStatusCode());

    }

    private ResponseEntity<MealResponse> patchMeal(String ownerUsername, long recordId, MealRequest mealRequest, String token, long mealId) {
        String url = StringUtil.format(mealPath, port, ownerUsername, recordId, mealId);
        HttpHeaders headers = buildHeaders(token);
        HttpEntity request = new HttpEntity(mealRequest, headers);
        return restTemplate.exchange(url, HttpMethod.PATCH, request, MealResponse.class);
    }

    private ResponseEntity<MealResponse> postMeal(String ownerUsername, long recordId, String time, String text, Integer calories, String token) {
        String url = StringUtil.format(mealApiPath, port, ownerUsername, recordId);
        MealRequest mealRequest = new MealRequest(time, calories, text);
        HttpHeaders headers = buildHeaders(token);
        HttpEntity<Map<String, String>> request = new HttpEntity(mealRequest, headers);
        return restTemplate.postForEntity(url, request, MealResponse.class);
    }
}
