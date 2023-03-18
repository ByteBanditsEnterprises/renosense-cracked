//Raddon On Top!

package org.sqlite.util;

import java.util.*;
import java.util.stream.*;

public class QueryUtils
{
    public static String valuesQuery(final List<String> columns, final List<List<Object>> valuesList) {
        valuesList.forEach(list -> {
            if (list.size() != columns.size()) {
                throw new IllegalArgumentException("values and columns must have the same size");
            }
            else {
                return;
            }
        });
        return "with cte(" + String.join(",", columns) + ") as (values " + valuesList.stream().map(values -> "(" + values.stream().map(o -> {
            if (o instanceof String) {
                return "'" + o + "'";
            }
            else if (o == null) {
                return "null";
            }
            else {
                return o.toString();
            }
        }).collect((Collector<? super Object, ?, String>)Collectors.joining(",")) + ")").collect((Collector<? super Object, ?, String>)Collectors.joining(",")) + ") select * from cte";
    }
}
