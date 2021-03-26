package com.toptal.backend.payload.response;

import com.toptal.backend.model.Meal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link Meal} response template
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse implements Serializable {

    private static final long serialVersionUID = 5926468513005110766L;

    private Long id;
    private String time;
    private Integer calories;
    private String text;

    public MealResponse(Meal meal) {
        this.id = meal.getId();
        this.time = meal.getTime().toString();
        this.calories = meal.getCalories();
        this.text = meal.getText();
    }
}
