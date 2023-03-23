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

import com.hcl.labs.domi.tools.DOMIException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * @author Paul Withers
 *         Builder to create online meeting provider parameters
 */
public class OnlineMeetingProviderParameterBuilder {

  private static final String MISSING = "OnlineMeetingProviderParameterBuilder is missing %s";
  Vertx vertx;
  Router router;
  String authUrl;
  String tokenUrl;
  String revocationUrl;
  String callbackRoute;
  String refreshRoute;
  String revokeRoute;
  String scopes;
  String path;
  JsonObject extraParams;

  /**
   * Validate, build and return parameters for an online meeting provider
   * 
   * @return OnlineMeetingProviderParameters
   * @throws DOMIException which would typically be missing parameters
   */
  public OnlineMeetingProviderParameters build() throws DOMIException {

    this.checkIfAnyParamIsMissing();

    return new OnlineMeetingProviderParameters(this.vertx, this.router, this.authUrl, this.tokenUrl,
        this.revocationUrl, this.callbackRoute, this.refreshRoute, this.revokeRoute, this.path,
        this.scopes,
        this.extraParams);
  }

  /**
   * @param passedVertx instance
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder vertx(final Vertx passedVertx) {
    this.vertx = passedVertx;
    return this;
  }

  /**
   * @param passedRouter for the server
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder router(final Router passedRouter) {
    this.router = passedRouter;
    return this;
  }

  /**
   * @param passedAuthUrl on the provider to do OAuth dance to retrieve short-lived code
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder authUrl(final String passedAuthUrl) {
    this.authUrl = passedAuthUrl;
    return this;
  }

  /**
   * @param passedTokenUrl on the provider to exchange short-lived code for tokens
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder tokenUrl(final String passedTokenUrl) {
    this.tokenUrl = passedTokenUrl;
    return this;
  }

  /**
   * @param passedRevocationUrl on the provider for revoking tokens
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder revocationUrl(final String passedRevocationUrl) {
    this.revocationUrl = passedRevocationUrl;
    return this;
  }

  /**
   * @param passedCallbackRoute endpoint on this server for OAuth dance to redirect to with
   *        short-lived
   *        code
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder callbackRoute(final String passedCallbackRoute) {
    this.callbackRoute = passedCallbackRoute;
    return this;
  }

  /**
   * @param passedRefreshRoute endpoint on this server for refreshing tokens
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder refreshRoute(final String passedRefreshRoute) {
    this.refreshRoute = passedRefreshRoute;
    return this;
  }

  /**
   * @param passedRevokeRoute endpoint on this server for revoking tokens
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder revokeRoute(final String passedRevokeRoute) {
    this.revokeRoute = passedRevokeRoute;
    return this;
  }

  /**
   * @param passedScopes to apply for OAuth access
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder scopes(final String passedScopes) {
    this.scopes = passedScopes;
    return this;
  }

  /**
   * @param passedPath on this server to apply OAuth restrictions to
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder path(final String passedPath) {
    this.path = passedPath;
    return this;
  }

  /**
   * @param passedExtraParams to add to body of HttpRequest for token
   * @return fluent
   */
  public OnlineMeetingProviderParameterBuilder extraParams(final JsonObject passedExtraParams) {
    this.extraParams = passedExtraParams;
    return this;
  }


  /**
   * Checks if any of the parameters isn't set yet
   *
   * @throws Exception
   */
  private void checkIfAnyParamIsMissing() throws DOMIException {
    if (null == this.vertx) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING, "the vertx instance");
    }

    if (null == this.router) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING, "the server's router");
    }

    if (null == this.authUrl) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "authorize URL for the provider");
    }

    if (null == this.tokenUrl) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "token URL for the provider");
    }

    // revocationUrl can be blank - GoToMeeting for example has not way to revoke an app

    if (null == this.callbackRoute) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "callback route on this server for the provider");
    }

    if (null == this.refreshRoute) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "refresh route on this server for the provider");
    }

    // revoke route can be blank - GoToMeeting for example has not way to revoke an app

    if (null == this.path) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "path on this server for the provider");
    }

    if (null == this.scopes) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "scopes for the provider");
    }

    if (null == this.extraParams) {
      throw new DOMIException(OnlineMeetingProviderParameterBuilder.MISSING,
          "extra parameters for the token URL body");
    }
  }

}
