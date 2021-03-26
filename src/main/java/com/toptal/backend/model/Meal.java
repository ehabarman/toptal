package com.toptal.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * Meal table
 *
 * @author ehab
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(columnDefinition = "TIME")
    LocalTime time;

    @NotNull
    private int calories;

    @NotNull
    @NotBlank
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    public Meal(LocalTime time, int calories, String text, Record record) {
        this.time = time;
        this.calories = calories;
        this.text = text;
        this.record = record;
    }
}
