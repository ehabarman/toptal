package com.toptal.backend.query;

import com.toptal.backend.util.helpers.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Replace Query operations by SQL standard operations syntax
 *
 * @author ehab
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class QueryTransform implements QueryOperations {

    public String transformQuery(String query) {
        if (StringUtil.isNullOrEmpty(query)) {
            return query;
        }

        String[][] operationsMapping = getOperationsMapping();

        for (String[] operationMap : operationsMapping) {
            query = query.replaceAll(StringUtil.format("\\b%s\\b", operationMap[0]), operationMap[1]);
        }

        return query;
    }


}
