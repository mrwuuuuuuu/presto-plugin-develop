package org.vincent.presto.event.listener;

import lombok.Builder;
import lombok.Data;

/**
 * @author Vincent Wu
 * @date 2020/3/28 16:46
 */
@Builder
@Data
public class QueryEvent {
  private String queryId;
  private String query;
  private String plan;
  private String queryState;

  private String user;
  private String principal;
  private String source;
  private String remoteClientAddr;
  private String catalog;

  private Long createTime;
  private Long executionStartTime;
  private Long endTime;
  private Long elapsedTime;
  private Long executionTime;

  private Long cpuTime;
  private Long wallTime;
  private Long queuedTime;
  private Long analysisTime;
  private Long distributedPlanningTime;
  private Long peakUserMemoryBytes;
  private Long peakTotalNonRevocableMemoryBytes;
  private Double cumulativeMemory;
  private Long totalRows;
  private Long totalBytes;
  private Integer completedSplits;

  private String failureInfo;
}
