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

/**
 * @author Paul Withers
 *         Constants to avoid hard codings
 */
public class DOMIConstants {

  public static final String CONFIG_DIR = "domiconfig.d";

  public static final String CONFIG_DEBUG = "DEBUG";
  public static final String CONFIG_HOSTNAME = "OAUTH_HOSTNAME";
  public static final String CONFIG_PORT = "PORT";
  public static final String CONFIG_METRICSPORT = "METRICSPORT";
  public static final String CONFIG_LOG_DIR = "LOG_DIR";

  public static final String CONFIG_TLSFILE = "TLSFile";
  public static final String CONFIG_TLSPASSWORD = "TLSPassword";
  public static final String CONFIG_PEMCERT = "PEMCert";

  public static final String CONFIG_PROPERTY_RESOURCENAME = "/domi.properties";

  public static final String TOKEN_PAGE = "/meetings.mustache";

  public static final String METRIC_TOTAL_HTTP = "domi.token.requests.total";
  public static final String METRIC_GRANT_TYPE_AUTH_CODE = "authorization_code";
  public static final String METRIC_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
  public static final String METRIC_GRANT_TYPE_REVOKE_TOKEN = "revoke_token";

  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_TYPE_JSON = "application/json";

}
