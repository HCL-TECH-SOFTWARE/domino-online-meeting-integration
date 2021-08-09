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

import com.hcl.labs.domi.tools.DOMIConstants;
import com.hcl.labs.domi.tools.DOMIUtils;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Paul Withers
 *         Handlers for revoking OAuth tokens. Bearer token expected on incoming. If included, the
 *         token is revoked with the relevant provider
 */
public class RevokeTokenHandler extends RefreshTokenHandler {

  /**
   * @param oAuthProvider current OAUth provider config
   * @param providerName  OAuth provider name
   */
  public RevokeTokenHandler(OAuth2Auth oAuthProvider, String providerName) {
    super(oAuthProvider, providerName);
  }

  /*
   * (non-Javadoc)
   * @see io.vertx.ext.web.handler.impl.BodyHandlerImpl#handle(io.vertx.ext.web.RoutingContext)
   */
  @Override
  public void handle(final RoutingContext ctx) {
    DOMIUtils.incrementRequestCounter(DOMIConstants.METRIC_TOTAL_HTTP, getProviderName(),
        DOMIConstants.METRIC_GRANT_TYPE_REFRESH_TOKEN);

    // Future to extract headers, revoke the token (we're actually revoking the access token, but
    // storing it
    this.extractToken(ctx, "access_token")
        .compose(promise -> getOAuthProvider().revoke(ctx.user(), "access_token"))
        .onSuccess(v -> this.returnSuccess(ctx))
        .onFailure(err -> this.endWithError(ctx, 400, err.getMessage()));
  }

  /**
   * Echoes back the JWT token in the response
   *
   * @param ctx RoutingContext for the request
   */
  private void returnSuccess(final RoutingContext ctx) {
    final HttpServerResponse response = this.createJsonResponse(ctx);
    response.end();
  }

}
