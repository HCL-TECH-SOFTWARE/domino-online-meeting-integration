/* ========================================================================== *
 * Copyright (c) 2021 HCL America, Inc.                       *
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.CharStreams;
import com.hcl.labs.domi.metrics.DOMIStatistics;
import com.hcl.labs.domi.metrics.DOMIStatisticsHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;

/**
 * @author Paul Withers
 *         Generic utilities
 */
public class DOMIUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DOMIUtils.class);

  static final String TLS_TYPE = "TLSType";
  static final String TLS_TYPE_PFX = "pfx";
  static final String TLS_TYPE_JKS = "jks";
  static final String TLS_TYPE_PEM = "pem";
  static final String TLS_TYPE_INVALID = "Invalid TLS security type (pft,jks,pem): {}";

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


  /**
   * Constructing options for HTTP server
   *
   * @param portType    which port constant to use to retrieve from config
   * @param portDefault default port, if nothing in config
   * @param config      config from which to retrieve the port
   * @return HttpServerOptions to apply to server
   */
  public static HttpServerOptions getServerOptions(final String portType, final int portDefault,
      final JsonObject config) {

    final HttpServerOptions options = new HttpServerOptions();
    final int port = config.getInteger(portType, portDefault);
    final String tlsFile = config.getString(DOMIConstants.CONFIG_TLSFILE, "null");
    final String tlsPassword = config.getString(DOMIConstants.CONFIG_TLSPASSWORD, "null");
    final String pemCert = config.getString(DOMIConstants.CONFIG_PEMCERT, "null");
    final boolean isHTTPS = !"null".equalsIgnoreCase(tlsFile) &&
        (!"null".equalsIgnoreCase(tlsPassword) || !"null".equalsIgnoreCase(pemCert));

    options.setSsl(isHTTPS);
    options.setPort(port);

    if (!isHTTPS) {
      return options;
    }

    // Now configure TLS capabilities
    options.setUseAlpn(true).setOpenSslEngineOptions(new OpenSSLEngineOptions());
    DOMIUtils.addTlsKeysToServerOptions(options, tlsFile, tlsPassword, pemCert, config);

    // Protocols add
    DOMIUtils.getEnabledValuesFromConfig("enabledProtocols", config)
        .forEach(options::addEnabledSecureTransportProtocol);

    // Protocols remove
    DOMIUtils.getEnabledValuesFromConfig("removeInsecureProtocols", config)
        .forEach(options::removeEnabledSecureTransportProtocol);

    // Ciphers
    DOMIUtils.getEnabledValuesFromConfig("cipher", config).forEach(cipher -> {
      try {
        options.addEnabledCipherSuite(cipher);
      } catch (final Exception e) {
        DOMIUtils.LOGGER.warn("Invalid cipher: {} ({})", cipher, e);
      }
    });

    return options;
  }

  /**
   * Returns all keys where the value is "true" - used to switch on/off features
   *
   * @param key    - value to look for
   * @param config
   * @return Set of values that are enables
   */
  public static Set<String> getEnabledValuesFromConfig(final String key, final JsonObject config) {
    return config.getJsonObject(key, new JsonObject()).stream()
        .filter(candidate -> Boolean.parseBoolean(String.valueOf(candidate.getValue())))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  /**
   * Configures TLS security from 3 available options pfx, jks, pem
   *
   * @param options     - HTTP Server options
   * @param tlsFile     - file name of tls key
   * @param tlsPassword - password to open the key
   * @param pemCert     - File with PEM Server certificate
   * @param config      - JsonObject from which to retrieve configurations
   */
  private static void addTlsKeysToServerOptions(final HttpServerOptions options,
      final String tlsFile, final String tlsPassword, final String pemCert,
      final JsonObject config) {
    final String tlsType = config.getString(DOMIUtils.TLS_TYPE, DOMIUtils.TLS_TYPE_PFX);
    switch (tlsType) {
      case TLS_TYPE_PFX:
        final PfxOptions pfx = new PfxOptions()
            .setPath(tlsFile)
            .setPassword(tlsPassword);
        options.setPfxKeyCertOptions(pfx);
        break;
      case TLS_TYPE_JKS:
        final JksOptions jksOptions = new JksOptions()
            .setPath(tlsFile)
            .setPassword(tlsPassword);
        options.setKeyStoreOptions(jksOptions);
        break;
      case TLS_TYPE_PEM:
        final PemKeyCertOptions pemOptions = new PemKeyCertOptions()
            .setKeyPath(tlsFile)
            .setCertPath(pemCert);
        options.setPemKeyCertOptions(pemOptions);
        break;

      default /* none */ :
        DOMIUtils.LOGGER.error(DOMIUtils.TLS_TYPE_INVALID, tlsType);
    }

  }
}
