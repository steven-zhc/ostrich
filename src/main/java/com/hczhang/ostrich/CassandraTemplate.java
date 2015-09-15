package com.hczhang.ostrich;

import com.datastax.driver.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by steven on 4/11/14.
 * A template class, inspired by spring jdbc template.
 */
public class CassandraTemplate {

    private static final long NO_DATA = 0;

    private static Map<String, PreparedStatement> pss = new ConcurrentHashMap<String, PreparedStatement>();

    private Session session;

    public CassandraTemplate(CassandraSessionFactory factory) {
        this.session = factory.getSession();
    }

    protected CassandraTemplate(Session session) {
        this.session = session;
    }

    private PreparedStatement getPrepareStatement(String cql) {
        PreparedStatement pstmt = null;
        if (pss.containsKey(cql)) {
            pstmt = pss.get(cql);
        } else {
            pstmt = session.prepare(cql);
            pss.put(cql, pstmt);
        }
        return pstmt;
    }

    public ResultSet batchExec(final String cql, BatchPreparedStatementSetter setter) {
        PreparedStatement pstmt = getPrepareStatement(cql);
        BatchStatement batch = new BatchStatement();

        for (int i = 0; i < setter.getBatchSize(); i++) {
            batch.add(setter.setValues(pstmt, i));
        }

        ResultSet rs = session.execute(batch);
        return rs;
    }

    public ResultSet batchExec(final String cql, Object[][] args) {
        PreparedStatement pstmt = getPrepareStatement(cql);
        BatchStatement batch = new BatchStatement();

        for (Object[] row : args) {
            batch.add(pstmt.bind(row));
        }
        ResultSet rs = session.execute(batch);

        return rs;
    }

    public ResultSet execute(final String cql, Object... args) {
        PreparedStatement pstmt = getPrepareStatement(cql);
        BoundStatement bs = new BoundStatement(pstmt);
        bs.bind(args);
        ResultSet rs = session.execute(bs);

        return  rs;
    }

    public ResultSet execute(final String cql) {
        ResultSet rs = session.execute(cql);

        return rs;
    }

    public ResultSetFuture executeAsync(final String cql) {
        ResultSetFuture rs = session.executeAsync(cql);
        return rs;
    }

    public ResultSetFuture executeAsync(final String cql, Object... args) {
        PreparedStatement pstmt = getPrepareStatement(cql);
        BoundStatement bs = new BoundStatement(pstmt);
        bs.bind(args);

        ResultSetFuture rs = session.executeAsync(bs);
        return rs;
    }

    public <T> T queryForObject(final String cql, RowMapper<T> mapper) {
        ResultSet rs = session.execute(cql);

        Row row = rs.one();

        if (row == null) {
            return null;
        }

        return mapper.mapRow(row, 0);
    }

    public <T> T queryForObject(final String cql, RowMapper<T> mapper, Object... args) {
        ResultSet rs = session.execute(cql, args);

        Row row = rs.one();

        if (row == null) {
            return null;
        }
        return mapper.mapRow(row, 0);
    }

    private <T> List<T> parseRow(ResultSet rs, RowMapper<T> mapper) {
        List<T> result = new ArrayList();
        int i = 0;
        for (Row row : rs) {
            result.add(mapper.mapRow(row, i));
            i++;
        }

        return result;
    }

    public <T> List<T> queryForList(final String cql, RowMapper<T> mapper) {

        ResultSet rs = this.execute(cql);

        return this.parseRow(rs, mapper);
    }

    public <T> List<T> queryForList(final String cql, RowMapper<T> mapper, Object... args) {

        ResultSet rs = this.execute(cql, args);

        return this.parseRow(rs, mapper);
    }

    /**
     * The query is expected to be a single row/single column query that results in a long value.
     * @param cql CQL query to execute
     * @param args arguments to bind to the query (leaving it to the PreparedStatement to guess the
     *             corresponding CQL type); may also contain SqlParameterValue objects which indicate
     *             not only the argument value but also the CQL type and optionally the scale
     * @return the long value, or 0 in case of SQL NULL
     */
    public long queryForLong(final String cql, Object... args) {
        ResultSet rs = this.execute(cql, args);

        long rlt = NO_DATA;
        for (Row row : rs) {
            rlt = row.getLong(0);
            break;
        }

        return rlt;
    }


}
