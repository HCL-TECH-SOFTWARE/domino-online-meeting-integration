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

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * @author Paul Withers
 *         Parameters to pass for setting up the online meeting provider routes
 */
public class OnlineMeetingProviderParameters {

  // Current Vertx instance
  public final Vertx vertx;

  // Router for this server
  public final Router router;

  // URL on the provider to do OAuth dance to retrieve short-lived code
  public final String authUrl;

  // URL on the provider to exchange short-lived code for tokens
  public final String tokenUrl;

  // Endpoint on this server for revoking tokens
  public final String revocationUrl;

  // Endpoint on this server for OAuth dance to redirect to with short-lived code
  public final String callbackRoute;

  // Endpoint on this server for refreshing tokens
  public final String refreshRoute;

  // Endpoint on this server for revoking tokens
  public final String revokeRoute;

  // Scopes to apply for OAuth access
  public final String scopes;

  // Path on this server to apply OAuth restrictions to
  public final String path;

  // extraParams to add to body of HttpRequest for token
  public final JsonObject extraParams;

  /**
   * @param vertx current Vertx instance
   * @param router for this server
   * @param authUrl on the provider to do OAuth dance to retrieve short-lived code
   * @param tokenUrl on the provider to exchange short-lived code for tokens
   * @param revocationUrl on the provider for revoking tokens
   * @param callbackRoute endpoint on this server for OAuth dance to redirect to with short-lived
   *        code
   * @param refreshRoute endpoint on this server for refreshing tokens
   * @param revokeRoute endpoint on the provider for revoking tokens
   * @param scopes to apply for OAuth access
   * @param path on this server to apply OAuth restrictions to
   * @param extraParams to add to body of HttpRequest for token
   */
  public OnlineMeetingProviderParameters(Vertx vertx, Router router, String authUrl,
      String tokenUrl, String revocationUrl, String callbackRoute, String refreshRoute,
      String revokeRoute, String path,
      String scopes, JsonObject extraParams) {
    this.vertx = vertx;
    this.router = router;
    this.authUrl = authUrl;
    this.tokenUrl = tokenUrl;
    this.revocationUrl = revocationUrl;
    this.callbackRoute = callbackRoute;
    this.refreshRoute = refreshRoute;
    this.revokeRoute = revokeRoute;
    this.scopes = scopes;
    this.path = path;
    this.extraParams = extraParams;
  }

}
