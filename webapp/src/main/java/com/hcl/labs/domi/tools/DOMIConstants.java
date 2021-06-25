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

  public static final String ZOOM_AUTHORIZE_URL = "https://zoom.us/oauth/authorize";
  public static final String ZOOM_CALLBACK_ROUTE = "/zoomCallback";
  public static final String ZOOM_CLIENT_ID = "ZOOM_CLIENT_ID";
  public static final String ZOOM_CLIENT_SECRET = "ZOOM_CLIENT_SECRET";
  public static final String ZOOM_REVOCATION_URL = "https://zoom.us/oauth/revoke";
  public static final String ZOOM_PATH = "/dominoZoom/";
  public static final String ZOOM_REFRESH_ROUTE = "/dominoZoomRefresh";
  public static final String ZOOM_REVOKE_ROUTE = "/dominoZoomRevoke";
  public static final String ZOOM_SCOPES = "meeting:write";
  public static final String ZOOM_TOKEN_URL = "https://zoom.us/oauth/token";
  public static final String ZOOM_LABEL = "Zoom";

  public static final String TEAMS_AUTHORIZE_URL =
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/authorize";
  public static final String TEAMS_CALLBACK_ROUTE = "/teamsCallback";
  public static final String TEAMS_CLIENT_ID = "TEAMS_CLIENT_ID";
  public static final String TEAMS_CLIENT_SECRET = "TEAMS_CLIENT_SECRET";
  public static final String TEAMS_REVOCATION_URL = "";
  public static final String TEAMS_PATH = "/dominoTeams/";
  public static final String TEAMS_REFRESH_ROUTE = "/dominoTeamsRefresh";
  public static final String TEAMS_REVOKE_ROUTE = "";
  public static final String TEAMS_SCOPES =
      "User.Read OnlineMeetings.ReadWrite offline_access";
  public static final String TEAMS_TOKEN_URL =
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/token";
  public static final String TEAMS_LABEL = "Microsoft Teams";

  public static final String WEBEX_AUTHORIZE_URL = "https://webexapis.com/v1/authorize";
  public static final String WEBEX_CALLBACK_ROUTE = "/webexCallback";
  public static final String WEBEX_CLIENT_ID = "WEBEX_CLIENT_ID";
  public static final String WEBEX_CLIENT_SECRET = "WEBEX_CLIENT_SECRET";
  public static final String WEBEX_REVOCATION_URL = "";
  public static final String WEBEX_PATH = "/dominoWebex/";
  public static final String WEBEX_REFRESH_ROUTE = "/dominoWebexRefresh";
  public static final String WEBEX_REVOKE_ROUTE = "";
  public static final String WEBEX_SCOPES =
      "meeting:schedules_write";
  public static final String WEBEX_TOKEN_URL = "https://webexapis.com/v1/access_token";
  public static final String WEBEX_LABEL = "Webex";

  public static final String GTM_AUTHORIZE_URL = "https://api.getgo.com/oauth/v2/authorize";
  public static final String GTM_CALLBACK_ROUTE = "/gtmCallback";
  public static final String GTM_CLIENT_ID = "GTM_CLIENT_ID";
  public static final String GTM_CLIENT_SECRET = "GTM_CLIENT_SECRET";
  public static final String GTM_REVOCATION_URL = "";
  public static final String GTM_PATH = "/dominoGTM/";
  public static final String GTM_REFRESH_ROUTE = "/dominoGTMRefresh";
  public static final String GTM_REVOKE_ROUTE = "";
  public static final String GTM_SCOPES = "collab:";
  public static final String GTM_TOKEN_URL = "https://api.getgo.com/oauth/v2/token";
  public static final String GTM_LABEL = "GoToMeeting";

  public static final String METRIC_TOTAL_HTTP = "domi.token.requests.total";
  public static final String METRIC_GRANT_TYPE_AUTH_CODE = "authorization_code";
  public static final String METRIC_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
  public static final String METRIC_GRANT_TYPE_REVOKE_TOKEN = "revoke_token";

  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_TYPE_JSON = "application/json";

}
