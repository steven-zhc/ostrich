package com.hczhang.ostrich;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;

/**
 * Created by steven on 10/27/14.
 */
public interface BatchPreparedStatementSetter {
    /**
     * Set parameter values on the given PreparedStatement.
     * @param ps the PreparedStatement to invoke bind methods on
     * @param i index of the statement we're issuing in the batch, starting from 0
     */
    BoundStatement setValues(PreparedStatement ps, int i);

    /**
     * Return the size of the batch.
     * @return the number of statements in the batch
     */
    int getBatchSize();
}
