package com.toptal.backend.DTO.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class RecordDTO {

    private Long id;
    private LocalDate date;
    private Integer totalCalories;
    private Boolean limitFlag;

}
