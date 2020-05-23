package com.toptal.toptal.backend.payload.response;

import com.toptal.toptal.backend.model.Meal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Record response template
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private long id;
    private String date;
    private List<Meal> mealList;
    private Boolean exceededCaloriesLimit;

    public RecordResponse(long id, String date) {
        this.id = id;
        this.date = date;
    }
}
