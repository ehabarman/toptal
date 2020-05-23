package com.toptal.toptal.backend.controller.data;

import com.toptal.toptal.backend.errors.CustomExceptions.AlreadyUsedException;
import com.toptal.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.toptal.backend.model.Record;
import com.toptal.toptal.backend.model.User;
import com.toptal.toptal.backend.payload.request.RecordRequest;
import com.toptal.toptal.backend.payload.response.RecordResponse;
import com.toptal.toptal.backend.query.MYSQLQueryTransform;
import com.toptal.toptal.backend.service.data.RecordService;
import com.toptal.toptal.backend.service.data.UserService;
import com.toptal.toptal.backend.util.helpers.CollectionUtil;
import com.toptal.toptal.backend.util.helpers.DateTimeUtil;
import com.toptal.toptal.backend.util.helpers.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for {@link Record} api calls
 *
 * @author ehab
 */
@RestController
@RequestMapping("/api/user/{username}")
public class RecordController {

    private final static String API_USER = "/api/user/";

    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @GetMapping("/records")
    public List<RecordResponse> getUserRecords(@PathVariable String username,
                                               @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                               @RequestParam(name = "search", required = false) String search){
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(API_USER + username);
        }

        boolean doPaging = isValidForPagination(pageSize, pageNumber);
        List<Record> records;
        if(StringUtil.isNullOrEmpty(search)) {
            records = recordService.findAllRecordByUser(user, pageNumber, pageSize, doPaging);
        }
        else {
            MYSQLQueryTransform mysqlQueryTransform = new MYSQLQueryTransform();
            String query = mysqlQueryTransform.transformQuery(search);
            records = recordService.executeDynamicQuery(user.getId(), query, pageNumber, pageSize, doPaging);
        }

        return transformRecordsToResponses(records);
    }

    private List<RecordResponse> transformRecordsToResponses(List<Record> records) {
        if (CollectionUtil.isNullOrEmpty(records)) {
            return new ArrayList<>();
        }
       return CollectionUtil.transformList(records,
               record -> new RecordResponse(record.getId(), record.getDate().toString()));
    }

    @GetMapping("/record/{recordId}")
    public String getRecord(@PathVariable String username, @PathVariable int recordId){
        return "record/{recordId}";
    }

    /**
     * Creates a new record for the user
     * Accepts date in the format "yyyy-MM-dd"
     */
    @PostMapping("/record")
    @PostAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public RecordResponse createRecord(@PathVariable String username, @RequestBody RecordRequest recordRequest, Principal principal)
            throws MissingServletRequestParameterException {
        User user = getRecordsOwner(username);

        if(StringUtil.isNullOrEmpty(recordRequest.getDate())) {
            throw new MissingServletRequestParameterException("date", "String");
        }
        LocalDate date = DateTimeUtil.buildDate(recordRequest.getDate());

        Record record = recordService.findRecordByUserAndDate(user, date);
        if(record != null) {
            throw new AlreadyUsedException("date", recordRequest.getDate());
        }
        Record newRecord = recordService.createRecord(user, date);
        return new RecordResponse(newRecord.getId(), newRecord.getDate().toString());
    }

    /**
     * Looks for the user who owns the records in the database
     */
    private User getRecordsOwner(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(API_USER + username);
        }
        return user;
    }

    /**
     * Validate pagination values
     */
    private boolean isValidForPagination(Integer pageSize, Integer pageNumber) {
        return pageSize != null && pageSize > 0 && pageNumber != null && pageNumber >= 0;
    }

}