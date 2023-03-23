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

import java.util.ArrayList;
import java.util.List;
import io.vertx.core.json.JsonArray;

/**
 * @author Devin Olson, Paul Withers
 *         Enum for all meeting providers supported
 */
public enum DOMIProvider {

  /**
   * Goto Meeting
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute,
   * client_id_envVar_label, client_secret_envVar_label)
   */
  GTM("GoToMeeting",
      "https://api.getgo.com/oauth/v2/authorize",
      "/gtmCallback",
      "collab:",
      "/dominoGTM/",
      "https://api.getgo.com/oauth/v2/token",
      "/dominoGTMRefresh",
      "GTM_CLIENT_ID",
      "GTM_CLIENT_SECRET"),

  /**
   * Microsoft Teams
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute,
   * client_id_envVar_label, client_secret_envVar_label)
   * Removed scope
   */
  TEAMS("Microsoft Teams",
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/authorize",
      "/teamsCallback",
      "User.Read OnlineMeetings.ReadWrite offline_access openid",
      "/dominoTeams/",
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/token",
      "/dominoTeamsRefresh",
      "TEAMS_CLIENT_ID",
      "TEAMS_CLIENT_SECRET"),

  /**
   * Webex
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute,
   * client_id_envVar_label, client_secret_envVar_label)
   */
  WEBEX("Webex",
      "https://webexapis.com/v1/authorize",
      "/webexCallback",
      "meeting:schedules_write",
      "/dominoWebex/",
      "https://webexapis.com/v1/access_token",
      "/dominoWebexRefresh",
      "WEBEX_CLIENT_ID",
      "WEBEX_CLIENT_SECRET"),

  /**
   * Zoom
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute,
   * client_id_envVar_label, client_secret_envVar_label, revokeRoute, revocationURL)
   */
  ZOOM("Zoom",
      "https://zoom.us/oauth/authorize",
      "/zoomCallback",
      "meeting:write",
      "/dominoZoom/",
      "https://zoom.us/oauth/token",
      "/dominoZoomRefresh",
      "ZOOM_CLIENT_ID",
      "ZOOM_CLIENT_SECRET",
      "/dominoZoomRevoke",
      "https://zoom.us/oauth/revoke");

  /**
   * Label for the Provider
   */
  public final String LABEL;

  /**
   * Authorize URL for the Provider
   */
  public final String AUTHORIZE_URL;

  /**
   * Callback Route for the Provider
   */
  public final String CALLBACK_ROUTE;

  /**
   * Requested Scopes for the Provider
   */
  public final String SCOPES;

  /**
   * Path for the Provider
   */
  public final String PATH;

  /**
   * Token URL for the Provider
   */
  public final String TOKEN_URL;

  /**
   * Environment variable name for the client ID
   */
  public final String CLIENT_ID_ENV;

  /**
   * Environment variable name for the client secret
   */
  public final String CLIENT_SECRET_ENV;

  /**
   * Refresh Route for the Provider
   */
  public final String REFRESH_ROUTE;

  /**
   * Revoke Route for the Provider
   */
  public final String REVOKE_ROUTE;

  /**
   * Revocation URL for the Provider
   */
  public final String REVOCATION_URL;

  DOMIProvider(final String label, final String authorizeURL, final String callbackRoute,
      final String scopes,
      final String path, final String tokenURL, final String refreshRoute, final String clientIdEnv,
      final String clientSecretEnv) {

    this.LABEL = label;
    this.AUTHORIZE_URL = authorizeURL;
    this.CALLBACK_ROUTE = callbackRoute;
    this.SCOPES = scopes;
    this.PATH = path;
    this.TOKEN_URL = tokenURL;
    this.REFRESH_ROUTE = refreshRoute;
    this.CLIENT_ID_ENV = clientIdEnv;
    this.CLIENT_SECRET_ENV = clientSecretEnv;
    this.REVOKE_ROUTE = "";
    this.REVOCATION_URL = "";
  }


  DOMIProvider(final String label, final String authorizeURL, final String callbackRoute,
      final String scopes,
      final String path, final String tokenURL, final String refreshRoute, final String clientIdEnv,
      final String clientSecretEnv, final String revokeRoute,
      final String revocationURL) {

    this.LABEL = label;
    this.AUTHORIZE_URL = authorizeURL;
    this.CALLBACK_ROUTE = callbackRoute;
    this.SCOPES = scopes;
    this.PATH = path;
    this.TOKEN_URL = tokenURL;
    this.REFRESH_ROUTE = refreshRoute;
    this.CLIENT_ID_ENV = clientIdEnv;
    this.CLIENT_SECRET_ENV = clientSecretEnv;
    this.REVOKE_ROUTE = revokeRoute;
    this.REVOCATION_URL = revocationURL;
  }

  /**
   * Gets the path for each DOMIProvider.
   * 
   * @return Paths for the DOMIProviders
   */
  public static final List<String> getPaths() {
    final List<String> result = new ArrayList<>();
    for (DOMIProvider dp : DOMIProvider.values()) {
      result.add(dp.PATH);
    }
    return result;
  }

  /**
   * Gets the client ID environment variable names for each DOMIProvider
   * 
   * @return Client ID environment variable names for the DOMIProviders
   */
  public static final JsonArray getClientIDEnvs() {
    final JsonArray result = new JsonArray();
    for (DOMIProvider dp : DOMIProvider.values()) {
      result.add(dp.CLIENT_ID_ENV);
    }
    return result;
  }

  /**
   * Gets the client secret environment variable names for each DOMIProvider
   * 
   * @return Client secret environment variable names for the DOMIProviders
   */
  public static final JsonArray getClientSecretEnvs() {
    final JsonArray result = new JsonArray();
    for (DOMIProvider dp : DOMIProvider.values()) {
      result.add(dp.CLIENT_SECRET_ENV);
    }
    return result;
  }

}
