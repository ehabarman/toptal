package com.toptal.backend.controller.data;

import com.toptal.backend.DTO.data.RecordDTO;
import com.toptal.backend.controller.SharedUtils;
import com.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.backend.model.Meal;
import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.request.RecordRequest;
import com.toptal.backend.payload.response.RecordResponse;
import com.toptal.backend.service.data.RecordService;
import com.toptal.backend.service.data.UserService;
import com.toptal.backend.util.Constants;
import com.toptal.backend.util.helpers.CollectionUtil;
import com.toptal.backend.util.helpers.DateTimeUtil;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.errors.CustomExceptions.AlreadyUsedException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for {@link Record} api calls
 *
 * @author ehab
 */
@RestController
@RequestMapping("/api/user/{username}")
@Api(tags = "Records APIs")
public class RecordController {

    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @GetMapping("/records")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<Map<String, Object>> getUserRecords(@PathVariable String username, Principal principal,
                                                    @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                    @RequestParam(name = "where", required = false) String whereFilter) {
        User user = getUserByUsername(username);
        boolean doPaging = SharedUtils.isValidForPagination(pageSize, pageNumber);
        String query = SharedUtils.processWhereFilterToMYSQLWhereClause(whereFilter);
        List<RecordDTO> records = recordService.executeDynamicRecordsQuery(user, query, pageNumber, pageSize, doPaging);
        return transformRecordsToResponses(records);
    }

    /**
     * Get record details
     */
    @GetMapping("/record/{recordId}")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Map<String, Object> getRecord(@PathVariable String username, @PathVariable long recordId, Principal principal) {
        User user = getUserByUsername(username);
        Record record = getRecordByUserAndId(user, recordId);
        int totalCalories = record.getMeals().stream().mapToInt(Meal::getCalories).sum();
        RecordResponse recordResponse = new RecordResponse(recordId, record.getDate().toString(), totalCalories,
                totalCalories > user.getCalLimit());
        return recordResponse.toMap();
    }

    /**
     * Creates a new record for the user
     * Accepts date in the format "yyyy-M-d"
     */
    @PostMapping("/record")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public RecordResponse createRecord(@PathVariable String username, @RequestBody RecordRequest recordRequest, Principal principal)
            throws MissingServletRequestParameterException {
        User user = getRecordsOwner(username);

        if (StringUtil.isNullOrEmpty(recordRequest.getDate())) {
            throw new MissingServletRequestParameterException("date", "String");
        }
        LocalDate date = DateTimeUtil.buildDate(recordRequest.getDate());

        Record record = recordService.findRecordByUserAndDate(user, date);
        if (record != null) {
            throw new AlreadyUsedException("date", recordRequest.getDate());
        }
        Record newRecord = recordService.saveRecord(user, date);
        return new RecordResponse(newRecord.getId(), newRecord.getDate().toString());
    }

    /**
     * Delete a record
     */
    @DeleteMapping("/record/{recordId}")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Map<String, Object> deleteRecord(@PathVariable String username, @PathVariable long recordId, Principal principal) {
        User user = getUserByUsername(username);
        Record record = getRecordByUserAndId(user, recordId);
        recordService.delete(record);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Record was deleted successfully");
        response.put("status", HttpStatus.OK);
        return response;
    }

    /**
     * Builds a record respones list from records list
     */
    private List<Map<String, Object>> transformRecordsToResponses(List<RecordDTO> records) {
        if (CollectionUtil.isNullOrEmpty(records)) {
            return new ArrayList<>();
        }

        return CollectionUtil.transformList(records,
                record -> new RecordResponse(record.getId(), record.getDate().toString(),
                        record.getTotalCalories(), record.getLimitFlag())
                        .toMap());
    }

    /**
     * Looks for the user who owns the records in the database
     */
    private User getRecordsOwner(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
        }
        return user;
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
        }
        return user;
    }

    /**
     * Get record by User and id
     */
    public Record getRecordByUserAndId(User user, long id) {
        Record record = recordService.findRecordByUserAndId(user, id);
        if (record == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_RECORD_URL, user.getUsername(), id));
        }
        return record;
    }
}