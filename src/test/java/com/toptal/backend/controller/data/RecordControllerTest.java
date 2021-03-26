package com.toptal.backend.controller.data;

import com.toptal.backend.SpringRunnerWithDataProvider;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.Role;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.request.RecordRequest;
import com.toptal.backend.payload.response.RecordResponse;
import com.toptal.backend.security.RoleType;
import com.toptal.backend.util.helpers.DateTimeUtil;
import com.toptal.backend.util.helpers.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.*;

@RunWith(SpringRunnerWithDataProvider.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RecordControllerTest extends BaseControllerTest {

    private void initUsers() {
        Role useRole = roleService.getRoleByName(RoleType.USER.name());
        User user = new User("user", encoder.encode(PASSWORD), new ArrayList<>(Collections.singletonList(useRole)));
        userService.saveUser(user);
    }

    @Test
    public void createGetAndDeleteRecordsTest() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";
        User user = userService.findByUsername(ownerUsername);

        LocalDate[] dates = {DateTimeUtil.buildDate("1996-12-6"), DateTimeUtil.buildDate("1996-12-7")};
        for (LocalDate date : dates) {
            ResponseEntity<RecordResponse> response = postRecord(ownerUsername, date.toString(), token);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assert.assertNotNull(recordService.findRecordByUserAndDate(user, date));
        }

        setTokenHeader(token);

        for (LocalDate date : dates) {
            Record record = recordService.findRecordByUserAndDate(user, date);
            String getRecordInfoUrl = StringUtil.format(recordPath, port, ownerUsername, record.getId());
            ResponseEntity<RecordResponse> response = restTemplate.getForEntity(getRecordInfoUrl, RecordResponse.class);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        for (LocalDate date : dates) {
            Record record = recordService.findRecordByUserAndDate(user, date);
            String deleteRecordUrl = StringUtil.format(recordPath, port, ownerUsername, record.getId());
            restTemplate.delete(deleteRecordUrl);
            record = recordService.findRecordByUserAndDate(user, date);
            Assert.assertNull(record);
        }

        for (int id = 1; id <= dates.length; id++) {
            String getRecordInfoUrl = StringUtil.format(recordPath, port, ownerUsername, id);
            ResponseEntity<RecordResponse> response = restTemplate.getForEntity(getRecordInfoUrl, RecordResponse.class);
            Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    public void invalidRequestsTest() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";

        ResponseEntity<RecordResponse> missingDateResponse = postRecord(ownerUsername, "", token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, missingDateResponse.getStatusCode());

        ResponseEntity<RecordResponse> wrongDate = postRecord(ownerUsername, "9999-9999-9999", token);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, wrongDate.getStatusCode());

        ResponseEntity<RecordResponse> successfulCreation = postRecord(ownerUsername, "1996-6-12", token);
        Assert.assertEquals(HttpStatus.OK, successfulCreation.getStatusCode());

        ResponseEntity<RecordResponse> failAlreadyExist = postRecord(ownerUsername, "1996-6-12", token);
        Assert.assertEquals(HttpStatus.CONFLICT, failAlreadyExist.getStatusCode());

        setTokenHeader(token);

        String getRecordInfoUrl = StringUtil.format(recordPath, port, ownerUsername, 555);
        ResponseEntity<RecordResponse> nonExistentRecord = restTemplate.getForEntity(getRecordInfoUrl, RecordResponse.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, nonExistentRecord.getStatusCode());
    }

    @Test
    public void listRecordsTest() {
        initUsers();
        String token = getTokenFromLogin(systemAdminUsername, systemAdminPassword);
        String ownerUsername = "user";

        String[] dates = {
                "1996-12-6",
                "1996-12-7",
                "1996-12-8",
                "1996-12-9",
                "1996-12-1",
        };

        for (String date : dates) {
            postRecord(ownerUsername, date, token);
        }

        setTokenHeader(token);
        String getRecordInfoUrl = StringUtil.format(recordsApiPath, port, ownerUsername);
        ResponseEntity<List> response = restTemplate.getForEntity(getRecordInfoUrl, List.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(dates.length, response.getBody().size());

    }

    private ResponseEntity<RecordResponse> postRecord(String username, String date, String token) {
        String ownerUrl = StringUtil.format(recordApiPath, port, username);
        RecordRequest recordRequest = new RecordRequest(date);
        HttpHeaders headers = buildHeaders(token);
        HttpEntity<Map<String, String>> request = new HttpEntity(recordRequest, headers);
        return restTemplate.postForEntity(ownerUrl, request, RecordResponse.class);
    }

}
