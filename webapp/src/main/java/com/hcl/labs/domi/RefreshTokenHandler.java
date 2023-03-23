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

package com.hcl.labs.domi;

import com.google.common.base.Strings;
import com.hcl.labs.domi.tools.DOMIConstants;
import com.hcl.labs.domi.tools.DOMIUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.UserImpl;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.BodyHandlerImpl;

/**
 * @author Paul Withers
 *         Handlers for refreshing OAuth tokens. Bearer token expected on incoming. If included, the
 *         token is refreshed with the relevant provider
 */
public class RefreshTokenHandler extends BodyHandlerImpl {
  private final OAuth2Auth oAuthProvider;
  private final String providerName;

  /**
   * Constructor
   *
   * @param oAuthProvider OAuth2Auth provider seeded client ID and secret
   * @param providerName OAuth provider's name
   */
  public RefreshTokenHandler(final OAuth2Auth oAuthProvider,
      final String providerName) {
    this.oAuthProvider = oAuthProvider;
    this.providerName = providerName;
    this.setHandleFileUploads(false);
  }

  /**
   * @return the OAuth2Auth provider for this endpoint
   */
  protected OAuth2Auth getOAuthProvider() {
    return this.oAuthProvider;
  }

  /**
   * @return the OAuth provider name for this endpoint
   */
  protected String getProviderName() {
    return this.providerName;
  }

  /**
   * Creates a JSON Response with basic headers
   *
   * @param ctx RoutingContext for the request
   * @return HttpServerResponse to send back to the user
   */
  protected HttpServerResponse createJsonResponse(final RoutingContext ctx) {
    final HttpServerResponse response = ctx.response();
    response.putHeader(DOMIConstants.CONTENT_TYPE, DOMIConstants.CONTENT_TYPE_JSON);
    return response;
  }

  /**
   * Ends a request by sending an error code
   *
   * @param ctx Context
   * @param status HTTP error code to return
   * @param errMsg to be returned to the user
   */
  protected void endWithError(final RoutingContext ctx, final int status, final String errMsg) {
    final HttpServerResponse response = this.createJsonResponse(ctx);
    response.setStatusCode(status)
        .setStatusMessage(errMsg)
        .end(errMsg);
  }

  /**
   * Future to verify a bearer token was passed and seed it into the User object
   *
   * @param ctx RoutingContext for the request
   * @param storeKey the property within the User object in which to store the token
   * @return succeeded or failed future
   */
  protected Future<Void> extractToken(final RoutingContext ctx, final String storeKey) {

    final String passedJwt = ctx.request().headers().get(DOMIConstants.HEADER_AUTHORIZATION);
    if (!Strings.isNullOrEmpty(passedJwt)) {
      final String[] split = passedJwt.split(" ");
      if ("Bearer".equals(split[0])) {
        final String token = split[split.length - 1];
        // Pass token to ctx.user.principal - used by oAuthProvider.refresh()
        final User user = new UserImpl(new JsonObject().put(storeKey, token), new JsonObject());
        ctx.setUser(user);
        return Future.succeededFuture();
      }
    }

    return Future.failedFuture("Bearer Authentication missing");
  }

  /*
   * (non-Javadoc)
   * @see io.vertx.ext.web.handler.impl.BodyHandlerImpl#handle(io.vertx.ext.web.RoutingContext)
   */
  @Override
  public void handle(final RoutingContext ctx) {
    DOMIUtils.incrementRequestCounter(DOMIConstants.METRIC_TOTAL_HTTP, this.providerName,
        DOMIConstants.METRIC_GRANT_TYPE_REFRESH_TOKEN);

    // Future to extract headers, refresh token and echo back the refreshed token
    this.extractToken(ctx, "refresh_token")
        .compose(promise -> this.getOAuthProvider().refresh(ctx.user()))
        .onSuccess(user -> this.returnUser(ctx, user))
        .onFailure(err -> this.endWithError(ctx, 400, err.getMessage()));
  }

  /**
   * Echoes back the JWT token in the response
   *
   * @param ctx RoutingContext for the request
   * @param user object returned by the token request
   */
  private void returnUser(final RoutingContext ctx, final User user) {
    final HttpServerResponse response = this.createJsonResponse(ctx);
    response.end(user.principal().encode());
  }

}
