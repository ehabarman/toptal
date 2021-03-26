package com.toptal.backend.payload.request;

import com.toptal.backend.util.helpers.StringUtil;
import com.toptal.backend.model.Meal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link Meal} request template
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MealRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005110766L;

    private String time;
    private Integer calories;
    private String text;

    public boolean isTimePresent() {
        return StringUtil.isntNullNorWhiteSpace(time);
    }

    public boolean isCaloriesPresent() {
        return calories != null;
    }

    public boolean isCaloriesValid() {
        return isCaloriesPresent() && calories >= 0;
    }

    public boolean isTextPresent() {
        return StringUtil.isntNullNorWhiteSpace(text);
    }

}
