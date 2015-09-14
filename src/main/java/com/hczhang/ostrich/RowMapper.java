package com.hczhang.ostrich;

import com.datastax.driver.core.Row;

/**
 * Created by steven on 4/11/14.
 */
public interface RowMapper<T> {

    T mapRow(Row row, int rowNum);
}
