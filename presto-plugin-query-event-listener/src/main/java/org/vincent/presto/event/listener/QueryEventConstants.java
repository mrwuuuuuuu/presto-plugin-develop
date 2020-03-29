package org.vincent.presto.event.listener;

/**
 * @author Vincent Wu
 * @date 2020/3/28 11:06
 */
public class QueryEventConstants {

  public final static String QUERY_STATE_FAILED = "FAILED";

  public final static String EMPTY_RESULTS = "empty_results";

  public final static String QUERY_SESSION_THRESHOLD_MILLIS_CONFIG = "query.session.threshold.millis";

  public final static String QUERY_SESSION_THRESHOLD_MILLIS_DEFAULT = "30000";

  public final static String QUERY_EVENT_REDIS_MSG_CHANNEL_CONFIG = "presto_query_event_msg_channel";

  public static final String REDIS_CLUSTER_NODES_CONFIG = "redis.cluster.nodes";

  public static final String REDIS_MIN_IDLE_CONFIG = "redis.min.idle";

  public static final String REDIS_MIN_IDLE_DEFAULT = "1";

  public static final String REDIS_MAX_IDLE_CONFIG = "redis.max.idle";

  public static final String REDIS_MAX_IDLE_DEFAULT = "5";

  public static final String REDIS_MAX_WAIT_CONFIG = "redis.max.wait";

  public static final String REDIS_MAX_WAIT_DEFAULT = "5000";

}
