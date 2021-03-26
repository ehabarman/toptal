package com.toptal.backend.payload.response;

import com.toptal.backend.model.Record;
import com.toptal.backend.util.helpers.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Record} response template
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private Long id;
    private String date;
    private Integer totalCalories;
    private Boolean exceededCaloriesLimit;

    public RecordResponse(Long id, String date) {
        this.id = id;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (id != null) {
            map.put("id", id);
        }
        if (StringUtil.isntNullNorEmpty(date)) {
            map.put("date", date);
        }
        if (totalCalories != null) {
            map.put("total_calories", totalCalories);
        }
        if (id != null) {
            map.put("daily_limit", exceededCaloriesLimit);
        }
        return map;
    }
}
