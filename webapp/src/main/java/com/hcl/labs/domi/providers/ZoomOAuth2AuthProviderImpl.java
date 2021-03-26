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

package com.hcl.labs.domi.providers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.impl.OAuth2API;
import io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl;

/**
 * @author Paul Withers
 *         OAuth2AuthProvider implementation for Zoom, to handle non-standard revoke API
 */
public class ZoomOAuth2AuthProviderImpl extends OAuth2AuthProviderImpl {

  private final OAuth2API revokeApi;

  /**
   * Constructor
   * 
   * @param vertx current Vertx instance
   * @param config OAuth2Options configuration for this OAuth2API
   */
  public ZoomOAuth2AuthProviderImpl(Vertx vertx, OAuth2Options config) {
    super(vertx, config);
    this.revokeApi = new ZoomOAuth2API(vertx, config);
  }

  /*
   * (non-Javadoc)
   * @see io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl#revoke(io.vertx.ext.auth.User,
   * java.lang.String, io.vertx.core.Handler)
   */
  @Override
  public OAuth2Auth revoke(User user, String tokenType, Handler<AsyncResult<Void>> handler) {
    this.revokeApi.tokenRevocation(tokenType, user.principal().getString(tokenType), handler);
    return this;
  }

}
