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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;

/**
 * This holds statistics about the current Keep session
 *
 * @author Paul Stephen Withers
 */
public interface DOMIStatistics {

  /**
   * For statistic actions: add or remove
   * to make it less confusing than a boolean
   */
  enum StatisticAction {
    /**
     * Add one
     */
    INCREMENT,
    /**
     * Remove one
     */
    DECREMENT,
    /**
     * leave it
     */
    UNCHANGED;
  }

  /**
   * Creates a counter or timer for a specific registry, currently only Prometheus.
   *
   * @param key to create metric with
   * @param metricsTags to create metric with
   * @param type of Meter to create. Only Timer and Counter are currently supported
   * @param registry registry into which to create the metric
   * @return Meter created
   */
  Meter createMetricForRegistry(final String key, final List<Tag> metricsTags,
      final Meter.Type type, MeterRegistry registry);

  /**
   * @return Map of metrics
   */
  Map<String, Meter> getMetricsMap();

  /**
   * Gets or creates a counter. Use this if tags require minimal computation to
   * pass
   *
   * @param key to create metric with
   * @param metricsTags to create metric with
   * @return Meter(Counter) created
   */
  Counter getOrCreateCounter(final String key, final List<Tag> metricsTags);

  /**
   * Gets or creates a counter. Use this if tags require minimal computation to
   * pass
   *
   * @param key to create metric with
   * @param metricsTags to create metric with
   * @return Meter(Timer) created
   */
  Timer getOrCreateTimer(final String key, final List<Tag> metricsTags);

  /**
   * Resets the statistics to start over. Mainly used when restarting keep
   *
   * @return fluent
   */
  DOMIStatistics reset();

}
