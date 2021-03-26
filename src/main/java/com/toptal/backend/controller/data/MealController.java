package com.toptal.backend.controller.data;

import com.toptal.backend.controller.SharedUtils;
import com.toptal.backend.errors.CustomExceptions.InternalHttpRequestException;
import com.toptal.backend.errors.CustomExceptions.NoFoodMatchesException;
import com.toptal.backend.errors.CustomExceptions.ResourceNotFoundException;
import com.toptal.backend.model.Record;
import com.toptal.backend.service.data.MealService;
import com.toptal.backend.service.data.RecordService;
import com.toptal.backend.service.data.UserService;
import com.toptal.backend.util.Constants;
import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.model.Meal;
import com.toptal.backend.model.User;
import com.toptal.backend.payload.request.MealRequest;
import com.toptal.backend.payload.response.MealResponse;
import com.toptal.backend.util.helpers.DateTimeUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for {@link Meal} api calls
 *
 * @author ehab
 */
@RestController
@RequestMapping("/api/user/{username}/record/{recordId}")
@Slf4j
@Api(tags = "Meals API")
public class MealController {

    public static final String X_APP_ID = "x-app-id";
    public static final String X_APP_KEY = "x-app-key";
    public static final String QUERY = "query";
    public static final String NF_CALORIES = "nf_calories";
    public static final String FOODS = "foods";

    @Value("${nutritionix.api.appId}")
    private String appId;

    @Value("${nutritionix.api.appKey}")
    private String appKey;

    @Value("${nutritionix.api.url}")
    private String nutritionixQueryApi;

    @Autowired
    private UserService userService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private MealService mealService;

    @Autowired
    private RestTemplate restTemplate;


    /**
     * Creates a new meal inside a record
     * Accepts date in the format "H:m"
     */
    @PostMapping("/meal")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public MealResponse createMeal(@PathVariable String username, @PathVariable Long recordId,
                                   @RequestBody MealRequest mealRequest, Principal principal)
            throws MissingServletRequestParameterException {
        Record record = getRecordByOwnerNameAndId(username, recordId);
        validateMealRequest(mealRequest);
        LocalTime time = DateTimeUtil.buildTime(mealRequest.getTime());
        Meal meal = new Meal(time, mealRequest.getCalories(), mealRequest.getText(), record);
        return new MealResponse(mealService.save(meal));
    }

    /**
     * Delete meal from record
     */
    @DeleteMapping("/meal/{mealId}")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Map<String, Object> deleteMeal(@PathVariable String username, @PathVariable Long recordId,
                                          @PathVariable Long mealId, Principal principal) {
        Record record = getRecordByOwnerNameAndId(username, recordId);
        if (!mealService.existsByRecordAndId(record, mealId)) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_MEAL_URL, username, recordId, mealId));
        }
        Meal meal = mealService.findMealById(mealId);
        mealService.delete(meal);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Meal was deleted successfully");
        response.put("status", HttpStatus.OK);
        return response;
    }

    /**
     * Get meal details
     */
    @GetMapping("/meal/{mealId}")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public MealResponse getMeal(@PathVariable String username, @PathVariable Long recordId, @PathVariable Long mealId,
                                Principal principal) {
        Record record = getRecordByOwnerNameAndId(username, recordId);
        Meal meal = mealService.findMealByRecordAndId(record, mealId);
        if (meal == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_MEAL_URL, username, recordId, mealId));
        }
        return new MealResponse(meal);
    }

    /**
     * Get all meals in a record
     */
    @GetMapping("/meals")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<MealResponse> getMeals(@PathVariable String username, @PathVariable Long recordId, Principal principal,
                                       @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                       @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                       @RequestParam(name = "where", required = false) String whereFilter) {
        Record record = getRecordByOwnerNameAndId(username, recordId);
        boolean doPaging = SharedUtils.isValidForPagination(pageSize, pageNumber);
        String query = SharedUtils.processWhereFilterToMYSQLWhereClause(whereFilter);
        return mealService.executeDynamicMealsQuery(record, query, pageNumber, pageSize, doPaging);
    }

    /**
     * Update meal data
     */
    @PatchMapping("/meal/{mealId}")
    @PreAuthorize("#username == #principal.getName() or hasRole('USER_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public MealResponse updateMeal(@PathVariable String username, @PathVariable Long recordId,
                                   @PathVariable Long mealId, @RequestBody MealRequest mealRequest, Principal principal) {
        Record record = getRecordByOwnerNameAndId(username, recordId);
        Meal meal = mealService.findMealByRecordAndId(record, mealId);
        if (meal == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_MEAL_URL, username, recordId, mealId));
        }
        if (mealRequest.isCaloriesPresent()) {
            if (mealRequest.isCaloriesValid()) {
                meal.setCalories(mealRequest.getCalories());
            } else {
                throw new IllegalArgumentException("calories can't have a negative value");
            }
        }
        if (mealRequest.isTimePresent()) {
            LocalTime time = DateTimeUtil.buildTime(mealRequest.getTime());
            meal.setTime(time);
        }
        if (mealRequest.isTextPresent()) {
            meal.setText(mealRequest.getText());
        }
        return new MealResponse(mealService.save(meal));
    }


    /**
     * Get record with its id and owner username
     */
    private Record getRecordByOwnerNameAndId(String username, Long recordId) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_USER_URL, username));
        }

        Record record = recordService.findRecordByUserAndId(user, recordId);
        if (record == null) {
            throw new ResourceNotFoundException(StringUtil.format(Constants.API_RECORD_URL, username, recordId));
        }
        return record;
    }

    /**
     * Validates mealRequest body
     */
    private void validateMealRequest(MealRequest mealRequest) throws MissingServletRequestParameterException {
        if (!mealRequest.isTextPresent()) {
            throw new MissingServletRequestParameterException("text", "String");
        }
        if (!mealRequest.isTimePresent()) {
            throw new MissingServletRequestParameterException("time", "String");
        }
        if (!mealRequest.isCaloriesPresent()) {
            int calories = getNutritionixData(mealRequest.getText());
            mealRequest.setCalories(calories);
        } else if (!mealRequest.isCaloriesValid()) {
            throw new IllegalArgumentException("calories can't have a negative value");
        }
    }


    private int getNutritionixData(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_APP_ID, appId);
        headers.add(X_APP_KEY, appKey);
        Map<String, String> query = new HashMap<>();
        query.put(QUERY, text);
        HttpEntity<Map<String, String>> request = new HttpEntity(query, headers);

        try {
            ResponseEntity<Map> result = restTemplate.postForEntity(nutritionixQueryApi, request, Map.class);
            List<Map<String, Object>> foods = (List<Map<String, Object>>) result.getBody().get(FOODS);
            int sum = 0;
            for (Map<String, Object> food : foods) {
                sum += ((Number) food.get(NF_CALORIES)).intValue();
            }
            return sum;
        } catch (Exception e) {
            if (((HttpStatusCodeException) e).getRawStatusCode() == 404) {
                throw new NoFoodMatchesException("We couldn't match any of your foods");
            }
            throw new InternalHttpRequestException(StringUtil.format("Error while requesting data from %s\n details: %s",
                    nutritionixQueryApi, e.getMessage()));
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
