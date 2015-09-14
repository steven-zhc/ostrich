package com.hczhang.ostrich;

import com.datastax.driver.core.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * one session (factory) per key space on a cluster
 * Created by steven on 4/11/14.
 */
public class CassandraSessionFactory {
    private static final Logger logger = LoggerFactory.getLogger(CassandraSessionFactory.class);

//    public static final String RETRY_POLICY = "retry.policy";
//    public static final String RECONNECTION_POLICY = "reconnection.policy";
//    public static final String LOAD_BALANCING_POLOICY = "load.balancing.policy";

    private Session session = null;

    private Cluster cluster = null;

    private Map<String, Object> properties;

    public CassandraSessionFactory(String contactPoints, String keySpace, String username, String password) {
        this(contactPoints, keySpace, username, password, new ClusterTuning() {
            @Override
            public void configCluster(Cluster.Builder builder, Map<String, Object> properties) {

            }
        }, new HashMap<String, Object>());
    }

    public CassandraSessionFactory(String contactPoints, String keySpace, String username, String password, ClusterTuning clusterTuning, Map<String, Object> properties) {

        if (StringUtils.isBlank(contactPoints)) {
            throw new IllegalArgumentException("Contact Points cannot be null");
        }

        if (StringUtils.isBlank(keySpace)) {
            throw new IllegalArgumentException("keySpace cannot be null");
        }

        Cluster.Builder builder = Cluster.builder().withCredentials(username, password);
        builder.withProtocolVersion(ProtocolVersion.V2);
        for (String cp : contactPoints.split(",")) {
            builder.addContactPoint(cp);
        }

        clusterTuning.configCluster(builder, properties);

        cluster = builder.build();

        if (logger.isInfoEnabled()) {
            Metadata metadata = cluster.getMetadata();
            logger.info("Connected to cluster: {}", metadata.getClusterName());
            for (Host host : metadata.getAllHosts()) {
                logger.info("DataCenter: {}; Host: {}; Rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
            }
        }

        session = cluster.connect(CassandraSessionFactory.MarshalKeyspace(keySpace));
    }

    public Session getSession() {
        return session;
    }

    public void onShutdown() {
        if (cluster != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Release the resource to Cassandra cluster. ");
            }

            cluster.close();
        }
    }

    private static String MarshalKeyspace(String hostname) {
        if (hostname == null || hostname.equalsIgnoreCase("")) {
            return "";
        }
        return hostname.replace("-", "").replace(".", "");
    }
}
