/* ========================================================================== *
 * Copyright (c) 2021 HCL ( http://www.hcl.com/ )                       *
 *                            All rights reserved.                            *
 * ========================================================================== *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 * ========================================================================== */

package com.hcl.labs.domi.metrics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.vertx.micrometer.backends.BackendRegistries;

/**
 * Holder for Keep statistics
 *
 * @author Paul Stephen Withers
 */
public enum DOMIStatisticsHolder implements DOMIStatistics {
  /**
   * Singleton holder for statistic data
   */
  INSTANCE;

  static final Logger LOGGER = LoggerFactory.getLogger(DOMIStatisticsHolder.class);
  // Map containing metrics created
  private final Map<String, Meter> keepMetrics = new ConcurrentHashMap<>();

  /**
   * Creates a counter or timer
   *
   * @param key to create metric with
   * @param metricsTags to create metric with
   * @param type of Meter to create. Only Timer and Counter are currently supported
   * @return Meter created
   */
  private Meter createMetric(final String key, final List<Tag> metricsTags, final Meter.Type type) {
    final MeterRegistry registry = BackendRegistries.getDefaultNow();

    return this.createMetricForRegistry(key, metricsTags, type, registry);
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.metrics.DillStatistics#createMetricForRegistry(java.lang.String,
   * java.util.List, io.micrometer.core.instrument.Meter.Type,
   * io.micrometer.core.instrument.MeterRegistry)
   */
  @Override
  public Meter createMetricForRegistry(final String key, final List<Tag> metricsTags,
      final Meter.Type type, final MeterRegistry registry) {
    Meter retMeter = null;
    switch (type) {
      case COUNTER:
        final Counter counter = registry.counter(key, metricsTags);
        this.keepMetrics.put(key, counter);
        retMeter = counter;
        break;
      case TIMER:
        final Timer timer = registry.timer(key, metricsTags);
        this.keepMetrics.put(key, timer);
        retMeter = timer;
        break;
      default:
        DOMIStatisticsHolder.LOGGER
            .error("Attempt to add unexpected Meter type {}: {}", type, key);
        break;
    }
    return retMeter;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.metrics.DillStatistics#getMetricsMap()
   */
  @Override
  public Map<String, Meter> getMetricsMap() {
    return this.keepMetrics;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.metrics.DillStatistics#getOrCreateCounter(java.lang.String,
   * java.util.List)
   */
  @Override
  public Counter getOrCreateCounter(final String key, final List<Tag> metricsTags) {
    return (Counter) this.getOrCreateMetric(key, metricsTags, Meter.Type.COUNTER);
  }

  /**
   * Get existing metric or initialise one
   *
   * @param key for the metric
   * @param metricsTags list of tags
   * @param type Counter or Timer
   * @return Meter
   */
  private Meter getOrCreateMetric(final String key, final List<Tag> metricsTags,
      final Meter.Type type) {
    final String flattenedTags = metricsTags.stream()
        .map(Tag::getValue)
        .collect(Collectors.joining(","));
    Meter meter = this.getMetricsMap().get(key + "~" + flattenedTags);
    if (null == meter) {
      meter = this.createMetric(key, metricsTags, type);
    }
    return meter;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.metrics.DillStatistics#getOrCreateTimer(java.lang.String,
   * java.util.List)
   */
  @Override
  public Timer getOrCreateTimer(final String key, final List<Tag> metricsTags) {
    return (Timer) this.getOrCreateMetric(key, metricsTags, Meter.Type.TIMER);
  }

  /**
   * @param key for the metric
   * @param metricsTags list of tags
   */
  public void incrementCounter(final String key, final List<Tag> metricsTags) {
    final Counter counter = (Counter) this.createMetric(key, metricsTags, Type.COUNTER);
    counter.increment(1.0);
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.metrics.DillStatistics#reset()
   */
  @Override
  public DOMIStatistics reset() {
    this.keepMetrics.clear();
    return this;
  }

}
