package com.toptal.backend.payload.request;

import com.toptal.backend.model.Record;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link Record} request template
 *
 * @author ehab
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005110666L;

    String date;
}
