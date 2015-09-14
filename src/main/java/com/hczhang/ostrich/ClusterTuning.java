package com.hczhang.ostrich;

import com.datastax.driver.core.Cluster;

import java.util.Map;

/**
 * Created by steven on 10/27/14.
 */
public interface ClusterTuning {

    public void configCluster(Cluster.Builder builder, Map<String, Object> properties);
}
