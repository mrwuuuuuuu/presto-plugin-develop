package org.vincent.presto.event.listener;

import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.eventlistener.EventListenerFactory;
import com.google.common.collect.ImmutableList;

/**
 * @author Vincent Wu
 * @date 2020/3/28 15:50
 */
public class QueryEventPlugin implements Plugin {
  @Override
  public Iterable<EventListenerFactory> getEventListenerFactories() {
    EventListenerFactory listenerFactory = new QueryEventListenerFactory();
    return ImmutableList.of(listenerFactory);
  }
}
