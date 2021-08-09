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

package com.hcl.labs.domi.providers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.hcl.labs.domi.RefreshTokenHandler;
import com.hcl.labs.domi.RevokeTokenHandler;
import com.hcl.labs.domi.tools.DOMIConstants;
import com.hcl.labs.domi.tools.DOMIProvider;
import com.hcl.labs.domi.tools.DOMIUtils;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.OAuth2AuthHandler;

/**
 * @author Paul Withers
 *         Factory class for a specific online meeting provider
 */
public class OnlineMeetingProviderFactoryHolder implements OnlineMeetingProviderFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineMeetingProviderFactoryHolder.class);
  private final String clientId; // Client ID to use when connecting to the OAuth provider
  private final String clientSecret; // Client secret to use when connecting to the OAuth provider
  private final String providerName; // Provider name, used in messages and web page
  private final String hostName; // Protocol, host and port for this server

  /**
   * Constructor
   * 
   * @param clientId     of OAuth application for this provider
   * @param clientSecret of OAuth application for this provider
   * @param providerName for online meeting provider
   * @param hostName     for this server, to use in redirect URLs etc
   */
  public OnlineMeetingProviderFactoryHolder(final String clientId, final String clientSecret,
      final String providerName, final String hostName) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.providerName = providerName;
    this.hostName = hostName;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.OnlineMeetingProviderFactory#getClientId()
   */
  @Override
  public String getClientId() {
    return this.clientId;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.OnlineMeetingProviderFactory#getClientSecret()
   */
  @Override
  public String getClientSecret() {
    return this.clientSecret;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.OnlineMeetingProviderFactory#getProviderName()
   */
  @Override
  public String getProviderName() {
    return this.providerName;
  }

  /*
   * (non-Javadoc)
   * @see com.hcl.labs.project_dill.OnlineMeetingProviderFactory#getHostName()
   */
  @Override
  public String getHostName() {
    return this.hostName;
  }

  /*
   * (non-Javadoc)
   * @see
   * com.hcl.labs.project_dill.OnlineMeetingProviderFactory#createAndEnableRoutes(io.vertx.core.
   * Vertx, io.vertx.ext.web.Router, java.lang.String, java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, io.vertx.core.json.JsonObject)
   */
  @Override
  public void createAndEnableRoutes(final OnlineMeetingProviderParameters params) {
    if (StringUtils.isEmpty(getClientId()) || StringUtils.isEmpty(getClientSecret())) {
      OnlineMeetingProviderFactoryHolder.LOGGER.warn(
          "***************\n** {} client or secret not configured, endpoints will not be activated ** \n****************",
          getProviderName());
    } else {
      final OAuth2Auth oauthProvider = createOAuthRoutesAndHandler(params);

      // Add revoke route, if needed
      if (StringUtils.isNotEmpty(params.revokeRoute) && StringUtils.isNotEmpty(params.revocationUrl)) {
        final RevokeTokenHandler revokeHandler = new RevokeTokenHandler(oauthProvider, getProviderName());
        params.router.route(params.revokeRoute).handler(revokeHandler);
        if (OnlineMeetingProviderFactoryHolder.LOGGER.isInfoEnabled()) {
          OnlineMeetingProviderFactoryHolder.LOGGER.info(
              "Enabled OAuth token revocation for {} at {}",
              getProviderName(), getHostName() + params.refreshRoute);
        }
      }

      final RefreshTokenHandler refreshHandler = new RefreshTokenHandler(oauthProvider, getProviderName());
      params.router.route(HttpMethod.GET, params.refreshRoute).handler(refreshHandler);
      if (OnlineMeetingProviderFactoryHolder.LOGGER.isInfoEnabled()) {
        OnlineMeetingProviderFactoryHolder.LOGGER.info("Enabled OAuth token refresh for {} at {}",
            getProviderName(), getHostName() + params.refreshRoute);
      }
    }
  }

  /**
   * Create all end points for OAuth dance and display of initial access and
   * refresh tokens
   * 
   * @param params OnlineMeetingProviderParameters object holding all required
   *               settings
   * @return OAuth2Auth authentication provider
   */
  private OAuth2Auth createOAuthRoutesAndHandler(final OnlineMeetingProviderParameters params) {
    final String indexRoot = params.path + "index.html";
    final OAuth2Options options = new OAuth2Options()
        .setFlow(OAuth2FlowType.AUTH_CODE)
        .setClientID(getClientId())
        .setClientSecret(getClientSecret())
        .setAuthorizationPath(params.authUrl)
        .setTokenPath(params.tokenUrl)
        .setExtraParameters(params.extraParams)
        .setScopeSeparator(" ");

    // Add revocation URL if needed
    if (StringUtils.isNotEmpty(params.revocationUrl)) {
      options.setRevocationPath(params.revocationUrl);
    }

    // Zoom doesn't follow IETF standards for revoke, so we need overridden classes
    final OAuth2Auth oAuthProvider = DOMIProvider.ZOOM.REVOKE_ROUTE.equals(params.revokeRoute)
        ? new ZoomOAuth2AuthProviderImpl(params.vertx, options)
        : OAuth2Auth.create(params.vertx, options);

    final OAuth2AuthHandler oAuthHandler = OAuth2AuthHandler.create(params.vertx, oAuthProvider,
        getHostName() + params.callbackRoute);

    if (OnlineMeetingProviderFactoryHolder.LOGGER.isInfoEnabled()) {
      OnlineMeetingProviderFactoryHolder.LOGGER.info("Enabled OAuth callback for {} at {}",
          getProviderName(), getHostName() + params.callbackRoute);
    }

    oAuthHandler.setupCallback(params.router.route())
        .withScope(params.scopes);
    params.router.route(params.path + "*").handler(oAuthHandler);
    if (OnlineMeetingProviderFactoryHolder.LOGGER.isInfoEnabled()) {
      OnlineMeetingProviderFactoryHolder.LOGGER.info("Enabled OAuth validation for {} on paths {}",
          getProviderName(), getHostName() + params.path + "*");
    }

    // OAuth handler
    params.router.route(indexRoot).handler(ctx -> oAuthTokenHandler(ctx,
        getHostName() + indexRoot));
    return oAuthProvider;
  }

  /**
   * Handler to generate page with access and refresh tokens for this online
   * meeting provider
   * 
   * @param ctx      current RoutingContext
   * @param location to override URL in location bar
   */
  @SuppressWarnings("resource")
  private void oAuthTokenHandler(final RoutingContext ctx, final String location) {
    final HttpServerResponse response = ctx.response();
    DOMIUtils.incrementRequestCounter(DOMIConstants.METRIC_TOTAL_HTTP, getProviderName(),
        DOMIConstants.METRIC_GRANT_TYPE_AUTH_CODE);
    response.putHeader(DOMIConstants.CONTENT_TYPE, "text/html")
        .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
        .putHeader("Pragma", "no-cache")
        .putHeader(HttpHeaders.EXPIRES, "0");

    final String pageResource = DOMIConstants.TOKEN_PAGE;
    try (InputStream inputStream = DOMIUtils.class.getResourceAsStream(pageResource);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      final MustacheFactory mustFact = new DefaultMustacheFactory();
      final Map<String, String> details = new ConcurrentHashMap<>();
      details.put("OAUTH_TYPE", getProviderName());
      details.put("REFRESH_TOKEN",
          ctx.user().principal().getString("refresh_token", "RETRIEVING REFRESH TOKEN FAILED"));
      details.put("LOCATION", location);
      final Mustache must = mustFact.compile(reader, "TOKEN");
      final StringWriter writer = new StringWriter();
      must.execute(writer, details);
      response.end(writer.toString());
    } catch (Exception e) {
      OnlineMeetingProviderFactoryHolder.LOGGER.error("Could not read {} {}", pageResource, e);
      ctx.fail(500);
    }

  }

}
