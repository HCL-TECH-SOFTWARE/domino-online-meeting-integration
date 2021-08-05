package com.hcl.labs.domi.tools;

import java.util.ArrayList;
import java.util.List;

public enum DOMIProvider {

  /**
   * Goto Meeting
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute)
   */
  GTM("GoToMeeting",
      "https://api.getgo.com/oauth/v2/authorize",
      "/gtmCallback",
      "collab:",
      "/dominoGTM/",
      "https://api.getgo.com/oauth/v2/token",
      "/dominoGTMRefresh"),

  /**
   * Microsoft Teams
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute)
   */
  TEAMS("Microsoft Teams",
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/authorize",
      "/teamsCallback",
      "User.Read OnlineMeetings.ReadWrite offline_access",
      "/dominoTeams/",
      "https://login.microsoftonline.com/organizations/oauth2/v2.0/token",
      "/dominoTeamsRefresh"),

  /**
   * Webex
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute)
   */
  WEBEX("Webex",
      "https://webexapis.com/v1/authorize",
      "/webexCallback",
      "meeting:schedules_write",
      "/dominoWebex/",
      "https://webexapis.com/v1/access_token",
      "/dominoWebexRefresh"),

  /**
   * Zoom
   * (label, authorizeURL, callbackRoute, scopes, path, tokenURL, refreshRoute,
   * revokeRoute, revocationURL)
   */
  ZOOM("Zoom",
      "https://zoom.us/oauth/authorize",
      "/zoomCallback",
      "meeting:write",
      "/dominoZoom/",
      "https://zoom.us/oauth/token",
      "/dominoZoomRefresh",
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

  private DOMIProvider(final String label, final String authorizeURL, final String callbackRoute, final String scopes,
      final String path, final String tokenURL, final String refreshRoute) {

    this.LABEL = label;
    this.AUTHORIZE_URL = authorizeURL;
    this.CALLBACK_ROUTE = callbackRoute;
    this.SCOPES = scopes;
    this.PATH = path;
    this.TOKEN_URL = tokenURL;
    this.REFRESH_ROUTE = refreshRoute;
    this.REVOKE_ROUTE = "";
    this.REVOCATION_URL = "";
  }

  private DOMIProvider(final String label, final String authorizeURL, final String callbackRoute, final String scopes,
      final String path, final String tokenURL, final String refreshRoute, final String revokeRoute,
      final String revocationURL) {

    this.LABEL = label;
    this.AUTHORIZE_URL = authorizeURL;
    this.CALLBACK_ROUTE = callbackRoute;
    this.SCOPES = scopes;
    this.PATH = path;
    this.TOKEN_URL = tokenURL;
    this.REFRESH_ROUTE = refreshRoute;
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

}
