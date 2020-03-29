package org.vincent.presto.event.listener;

import com.facebook.presto.spi.eventlistener.EventListener;
import com.facebook.presto.spi.eventlistener.EventListenerFactory;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Vincent Wu
 * @date 2020/3/28 15:49
 */
@Slf4j
public class QueryEventListenerFactory implements EventListenerFactory {

  private final static String CHECK_FILE_ERROR_MSG = "config is null, please check the required file 'event-listener.properties'";
  private final static String CHECK_CONFIG_ERROR_MSG = "[%s] must be required in 'event-listener.properties'";

  private final static String EVENT_NAME = "query-event-listener";

  @Override
  public String getName() {
    return EVENT_NAME;
  }

  @Override
  public EventListener create(Map<String, String> config) {
    log.debug("config info ------> {}", config);
    Preconditions.checkNotNull(config, CHECK_FILE_ERROR_MSG);
    Preconditions.checkNotNull(config.get(QueryEventConstants.REDIS_CLUSTER_NODES_CONFIG), String.format(CHECK_CONFIG_ERROR_MSG, QueryEventConstants.REDIS_CLUSTER_NODES_CONFIG));
    return new QueryEventListener(config);
  }
}