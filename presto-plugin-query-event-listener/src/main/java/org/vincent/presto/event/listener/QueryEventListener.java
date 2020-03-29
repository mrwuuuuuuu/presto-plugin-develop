package org.vincent.presto.event.listener;

import com.facebook.presto.spi.eventlistener.EventListener;
import com.facebook.presto.spi.eventlistener.QueryCompletedEvent;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * @author Vincent Wu
 * @date 2020/3/28 15:51
 */
@Slf4j
@NoArgsConstructor
public class QueryEventListener implements EventListener {

  private final static Gson GSON = new GsonBuilder().create();

  private static int querySessionThresholdMillis;

  private final static Map<String, String> CONFIG_MAP = Maps.newHashMap();


  public QueryEventListener(Map<String, String> config) {
    CONFIG_MAP.putAll(config);
    querySessionThresholdMillis = Integer.parseInt(Optional.ofNullable(config.get(QueryEventConstants.QUERY_SESSION_THRESHOLD_MILLIS_CONFIG))
            .orElse(QueryEventConstants.QUERY_SESSION_THRESHOLD_MILLIS_DEFAULT));
  }

  @Override
  public void queryCompleted(QueryCompletedEvent queryCompletedEvent) {
    QueryEvent queryEvent = getQueryEventWithConditions(queryCompletedEvent);
    log.debug("QueryEvent info ------------------> :{}", GSON.toJson(queryEvent));
    RedisUtils.getInstance(CONFIG_MAP).publish(QueryEventConstants.QUERY_EVENT_REDIS_MSG_CHANNEL_CONFIG, GSON.toJson(queryEvent));
  }

  private QueryEvent getQueryEventWithConditions(QueryCompletedEvent queryCompletedEvent) {
    QueryEvent queryEvent = QueryEvent.builder()
            .queryId(queryCompletedEvent.getMetadata().getQueryId())
            .query(queryCompletedEvent.getMetadata().getQuery())
            .queryState(queryCompletedEvent.getMetadata().getQueryState())
            .source(queryCompletedEvent.getContext().getSource().orElse(QueryEventConstants.EMPTY_RESULTS))
            .catalog(queryCompletedEvent.getContext().getCatalog().orElse(QueryEventConstants.EMPTY_RESULTS))
            .remoteClientAddr(queryCompletedEvent.getContext().getRemoteClientAddress().orElse(QueryEventConstants.EMPTY_RESULTS))
            .user(queryCompletedEvent.getContext().getUser())
            .principal(queryCompletedEvent.getContext().getPrincipal().orElse(QueryEventConstants.EMPTY_RESULTS))
            .createTime(queryCompletedEvent.getCreateTime().toEpochMilli())
            .executionStartTime(queryCompletedEvent.getExecutionStartTime().toEpochMilli())
            .endTime(queryCompletedEvent.getEndTime().toEpochMilli())
            .cpuTime(queryCompletedEvent.getStatistics().getCpuTime().toMillis())
            .wallTime(queryCompletedEvent.getStatistics().getWallTime().toMillis())
            .queuedTime(queryCompletedEvent.getStatistics().getQueuedTime().toMillis())
            .analysisTime(queryCompletedEvent.getStatistics().getAnalysisTime().orElse(Duration.ZERO).toMillis())
            .distributedPlanningTime(queryCompletedEvent.getStatistics().getDistributedPlanningTime().orElse(Duration.ZERO).toMillis())
            .peakUserMemoryBytes(queryCompletedEvent.getStatistics().getPeakUserMemoryBytes())
            .peakTotalNonRevocableMemoryBytes(queryCompletedEvent.getStatistics().getPeakTotalNonRevocableMemoryBytes())
            .cumulativeMemory(queryCompletedEvent.getStatistics().getCumulativeMemory())
            .totalRows(queryCompletedEvent.getStatistics().getTotalRows())
            .totalBytes(queryCompletedEvent.getStatistics().getTotalBytes())
            .completedSplits(queryCompletedEvent.getStatistics().getCompletedSplits()).build();

    queryEvent.setExecutionTime(queryEvent.getEndTime() - queryEvent.getExecutionStartTime());
    queryEvent.setElapsedTime(queryEvent.getEndTime() - queryEvent.getCreateTime());


    if (QueryEventConstants.QUERY_STATE_FAILED.equals(queryEvent.getQueryState()) && queryCompletedEvent.getFailureInfo().isPresent()) {
      queryEvent.setFailureInfo(GSON.toJson(queryCompletedEvent.getFailureInfo().get()));
    } else {
      queryEvent.setFailureInfo(QueryEventConstants.EMPTY_RESULTS);
    }

    if (queryEvent.getElapsedTime() > querySessionThresholdMillis) {
      queryEvent.setPlan(queryCompletedEvent.getMetadata().getPlan().orElse(QueryEventConstants.EMPTY_RESULTS));
    } else {
      queryEvent.setPlan(QueryEventConstants.EMPTY_RESULTS);
    }
    return queryEvent;
  }


}
