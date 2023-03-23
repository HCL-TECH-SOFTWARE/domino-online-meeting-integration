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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.impl.OAuth2API;

/**
 * @author Paul Withers
 *         OAuth2API implementation for Zoom. Zoom's token revocation does not follow IETF
 *         standards. Instead of token and tokenType in body of the POST request, it expects the
 *         accessToken in the query string. So a separate class is required to override that method.
 */
public class ZoomOAuth2API extends OAuth2API {

  final OAuth2Options config;

  /**
   * @param vertx current Vertx instance
   * @param config OAuth options
   */
  public ZoomOAuth2API(Vertx vertx, OAuth2Options config) {
    super(vertx, config);
    this.config = config;
  }


  /**
   * Revoke an obtained access or refresh token.
   * Unlike https://tools.ietf.org/html/rfc7009, Zoom requires token as query parameter
   */
  @Override
  public Future<Void> tokenRevocation(String tokenType, String token) {
    if (token == null) {
      return Future.failedFuture("Cannot revoke null token");
    }

    final JsonObject headers = new JsonObject();

    final boolean confidentialClient =
        this.config.getClientId() != null && this.config.getClientSecret() != null;

    if (confidentialClient) {
      String basic = this.config.getClientId() + ":" + this.config.getClientSecret();
      headers.put("Authorization",
          "Basic " + Base64.getEncoder().encodeToString(basic.getBytes(StandardCharsets.UTF_8)));
    }

    final JsonObject tmp = this.config.getHeaders();
    if (tmp != null) {
      headers.mergeIn(tmp);
    }

    final String query = "?token=" + token;

    final Buffer payload = null;
    // specify preferred accepted accessToken type
    headers.put("Accept", "application/json,application/x-www-form-urlencoded;q=0.9");

    return fetch(
        HttpMethod.POST,
        this.config.getRevocationPath() + query,
        headers,
        payload)
            .compose(reply -> {
              if (reply.body() == null) {
                return Future.failedFuture("No Body");
              }

              return Future.succeededFuture();
            });
  }

}
