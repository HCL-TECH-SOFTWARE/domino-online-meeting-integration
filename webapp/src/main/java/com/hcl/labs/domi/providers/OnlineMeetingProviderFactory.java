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

/**
 * @author Paul Withers
 *         Interface for Factory class for online meeting providers
 */
public interface OnlineMeetingProviderFactory {

  /**
   * @return client ID of OAuth application for this provider
   */
  String getClientId();

  /**
   * @return client secret of OAuth application for this provider
   */
  String getClientSecret();

  /**
   * @return name of the online meeting provider
   */
  String getProviderName();

  /**
   * @return host name for this server
   */
  String getHostName();

  /**
   * Creates and registers route for callback from OAuth for this provider, hooking it into certain
   * paths. Also creates the route for refreshing tokens.
   * 
   * @param params OnlineMeetingProviderParameters object holding all required settings
   */
  void createAndEnableRoutes(OnlineMeetingProviderParameters params);

}
