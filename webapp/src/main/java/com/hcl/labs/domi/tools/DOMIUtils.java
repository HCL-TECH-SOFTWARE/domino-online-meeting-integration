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

package com.hcl.labs.domi.tools;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.CharStreams;
import com.hcl.labs.domi.metrics.DOMIStatistics;
import com.hcl.labs.domi.metrics.DOMIStatisticsHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Paul Withers
 *         Generic utilities
 */
public class DOMIUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DOMIUtils.class);

  /**
   * Names all parameters that can be overwritten / defined by environment
   * variables
   *
   * @return JsonArray
   */
  public static JsonArray getEnvironmentParamsToRead() {
    final JsonArray result = new JsonArray();
    result.add(DOMIConstants.CONFIG_DEBUG)
        .add(DOMIConstants.CONFIG_LOG_DIR)
        .add(DOMIConstants.CONFIG_PORT)
        .add(DOMIConstants.CONFIG_TLSFILE)
        .add(DOMIConstants.CONFIG_TLSPASSWORD)
        .add(DOMIConstants.ZOOM_CLIENT_ID)
        .add(DOMIConstants.ZOOM_CLIENT_SECRET)
        .add(DOMIConstants.TEAMS_CLIENT_ID)
        .add(DOMIConstants.TEAMS_CLIENT_SECRET)
        .add(DOMIConstants.WEBEX_CLIENT_ID)
        .add(DOMIConstants.WEBEX_CLIENT_SECRET)
        .add(DOMIConstants.GTM_CLIENT_ID)
        .add(DOMIConstants.GTM_CLIENT_SECRET)
        .add(DOMIConstants.CONFIG_HOSTNAME);

    return result;
  }

  /**
   * @param resourceName to load as a JsonObject
   * @return a JsonObject or an empty JsonObject if no resource found
   */
  public static JsonObject getJsonFromResource(final String resourceName) {

    final String rawJson = DOMIUtils.getStringFromResource(resourceName);
    JsonObject retObj = new JsonObject();
    if (rawJson != null) {
      try {
        retObj = new JsonObject(rawJson);
      } catch (final Exception t) {
        DOMIUtils.LOGGER.error(String.format("Failed to load resource %s", resourceName), t);
      }
    }
    // If we got here, things went wrong
    return retObj;
  }

  /**
   * @param resourceName to load as a string
   * @return a String containing the text in the resource
   */
  public static String getStringFromResource(final String resourceName) {

    String retStr = null;

    try (InputStream inStream = DOMIUtils.class.getResourceAsStream(resourceName)) {
      retStr = CharStreams.toString(new InputStreamReader(inStream, StandardCharsets.UTF_8));
    } catch (final Exception e) {
      DOMIUtils.LOGGER.error("Could not read {} {}", resourceName, e);
    }

    return retStr;
  }

  /**
   * Increment metrics counter, creating the counter if required
   *
   * @param counterName name of the counter to increment
   * @param providerName OAuth provider for the request
   * @param grantType whether the request was for an authorization code or refresh token
   */
  public static void incrementRequestCounter(final String counterName, final String providerName,
      final String grantType) {
    final DOMIStatistics stats = DOMIStatisticsHolder.INSTANCE;
    Counter counter = (Counter) stats.getMetricsMap().get(counterName);
    if (null == counter) {
      final List<Tag> metricsTags = new ArrayList<>();
      metricsTags.add(Tag.of("provider", StringUtils.replace(providerName, " ", "")));
      metricsTags.add(Tag.of("grant_type", grantType));
      counter = stats.getOrCreateCounter(counterName, metricsTags);
    }
    counter.increment(1.0);
  }
}
