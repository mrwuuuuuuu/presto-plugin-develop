package org.vincent.presto.event.listener;

import com.google.common.collect.Sets;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Vincent Wu
 * @date 2020/3/29 17:50
 */
@Slf4j
@NoArgsConstructor
public class RedisUtils {

  private static RedisUtils instance;

  private JedisCluster jedisCluster;

  public static RedisUtils getInstance(Map<String, String> configMap) {
    if (instance == null) {
      syncInit(configMap);
    }
    return instance;
  }

  private static synchronized void syncInit(Map<String, String> configMap) {
    if (instance == null) {
      instance = new RedisUtils(configMap);
    }
  }

  private RedisUtils(Map<String, String> configMap) {
    RedisProperties redisProperties = new RedisProperties(configMap);
    this.jedisCluster = new JedisCluster(clusterNodes(redisProperties), poolConfig(redisProperties));
  }

  private Set<HostAndPort> clusterNodes(RedisProperties redisProperties) {
    Set<HostAndPort> nodes = Sets.newHashSet();
    String[] hostAndPorts = redisProperties.clusterNodes.split(",");
    for (String hostAndPort : hostAndPorts) {
      String host = hostAndPort.split(":")[0];
      int port = Integer.parseInt(hostAndPort.split(":")[1]);
      nodes.add(new HostAndPort(host, port));
    }
    return nodes;
  }

  private GenericObjectPoolConfig poolConfig(RedisProperties redisProperties) {
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    poolConfig.setMinIdle(redisProperties.minIdle);
    poolConfig.setMaxIdle(redisProperties.maxIdle);
    poolConfig.setMaxWaitMillis(redisProperties.maxWait);
    return poolConfig;
  }

  public void publish(String channel, String message) {
    try {
      jedisCluster.publish(channel, message);
    } catch (JedisConnectionException e) {
      log.error("redis client exception : ", e);
    }
  }

  private static class RedisProperties {
    String clusterNodes;
    int minIdle;
    int maxIdle;
    int maxWait;

    public RedisProperties(Map<String, String> configMap) {
      this.clusterNodes = Optional.of(configMap.get(QueryEventConstants.REDIS_CLUSTER_NODES_CONFIG)).get();
      this.minIdle = Integer.parseInt(Optional.ofNullable(configMap.get(QueryEventConstants.REDIS_MIN_IDLE_CONFIG)).orElse(QueryEventConstants.REDIS_MIN_IDLE_DEFAULT));
      this.maxIdle = Integer.parseInt(Optional.ofNullable(configMap.get(QueryEventConstants.REDIS_MAX_IDLE_CONFIG)).orElse(QueryEventConstants.REDIS_MAX_IDLE_DEFAULT));
      this.maxWait = Integer.parseInt(Optional.ofNullable(configMap.get(QueryEventConstants.REDIS_MAX_WAIT_CONFIG)).orElse(QueryEventConstants.REDIS_MAX_WAIT_DEFAULT));
    }
  }
}
