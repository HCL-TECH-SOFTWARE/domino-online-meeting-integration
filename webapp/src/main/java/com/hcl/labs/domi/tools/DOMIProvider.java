package com.hcl.labs.domi.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DOMIProvider {

  GTM(DOMIConstants.GTM_LABEL,
      DOMIConstants.GTM_AUTHORIZE_URL,
      DOMIConstants.GTM_CALLBACK_ROUTE,
      DOMIConstants.GTM_SCOPES,
      DOMIConstants.GTM_PATH,
      DOMIConstants.GTM_TOKEN_URL,
      DOMIConstants.GTM_REFRESH_ROUTE,
      DOMIConstants.GTM_REVOKE_ROUTE,
      DOMIConstants.GTM_REVOCATION_URL),
  TEAMS(DOMIConstants.TEAMS_LABEL,
      DOMIConstants.TEAMS_AUTHORIZE_URL,
      DOMIConstants.TEAMS_CALLBACK_ROUTE,
      DOMIConstants.TEAMS_SCOPES,
      DOMIConstants.TEAMS_PATH,
      DOMIConstants.TEAMS_TOKEN_URL,
      DOMIConstants.TEAMS_REFRESH_ROUTE,
      DOMIConstants.TEAMS_REVOKE_ROUTE,
      DOMIConstants.TEAMS_REVOCATION_URL),
  WEBEX(DOMIConstants.WEBEX_LABEL,
      DOMIConstants.WEBEX_AUTHORIZE_URL,
      DOMIConstants.WEBEX_CALLBACK_ROUTE,
      DOMIConstants.WEBEX_SCOPES,
      DOMIConstants.WEBEX_PATH,
      DOMIConstants.WEBEX_TOKEN_URL,
      DOMIConstants.WEBEX_REFRESH_ROUTE,
      DOMIConstants.WEBEX_REVOKE_ROUTE,
      DOMIConstants.WEBEX_REVOCATION_URL),
  ZOOM(DOMIConstants.ZOOM_LABEL,
      DOMIConstants.ZOOM_AUTHORIZE_URL,
      DOMIConstants.ZOOM_CALLBACK_ROUTE,
      DOMIConstants.ZOOM_SCOPES,
      DOMIConstants.ZOOM_PATH,
      DOMIConstants.ZOOM_TOKEN_URL,
      DOMIConstants.ZOOM_REFRESH_ROUTE,
      DOMIConstants.ZOOM_REVOKE_ROUTE,
      DOMIConstants.ZOOM_REVOCATION_URL);

  private final Map<String, String> _map;

  private DOMIProvider(final String label, final String authorizeURL, final String callbackRoute, final String scopes,
      final String path, final String tokenURL, final String refreshRoute, final String revokeRoute,
      final String revocationURL) {

    this._map = new HashMap<>();
    this._map.put("Label", label);
    this._map.put("AuthorizeURL", authorizeURL);
    this._map.put("CallbackRoute", callbackRoute);
    this._map.put("Scopes", scopes);
    this._map.put("Path", path);
    this._map.put("TokenURL", tokenURL);
    this._map.put("RefreshRoute", refreshRoute);
    this._map.put("RevokeRoute", revokeRoute);
    this._map.put("RevocationURL", revocationURL);
  }

  /**
   * @return AuthorizeURL for the Provider
   */
  public final String getAuthorizeURL() {
    return this._map.getOrDefault("AuthorizeURL", "");
  }

  /**
   * @return CallbackRoute for the Provider
   */
  public final String getCallbackRoute() {
    return this._map.getOrDefault("CallbackRoute", "");
  }

  /**
   * @return Scopes for the Provider
   */
  public final String getScopes() {
    return this._map.getOrDefault("Scopes", "");
  }

  /**
   * @return Path for the Provider
   */
  public final String getPath() {
    return this._map.getOrDefault("Path", "");
  }

  /**
   * @return TokenURL for the Provider
   */
  public final String getTokenURL() {
    return this._map.getOrDefault("TokenURL", "");
  }

  /**
   * @return RefreshRoute for the Provider
   */
  public final String getRefreshRoute() {
    return this._map.getOrDefault("RefreshRoute", "");
  }

  /**
   * @return RevokeRoute for the Provider
   */
  public final String getRevokeRoute() {
    return this._map.getOrDefault("RevokeRoute", "");
  }

  /**
   * @return RevocationURL for the Provider
   */
  public final String getRevocationURL() {
    return this._map.getOrDefault("RevocationURL", "");
  }

  /**
   * Gets the path for each DOMIProvider.
   * 
   * @return Paths for the DOMIProviders
   */
  public static final List<String> getPaths() {
    final List<String> result = new ArrayList<>();
    for (DOMIProvider dp : DOMIProvider.values()) {
      result.add(dp.getPath());
    }
    return result;
  }

}
