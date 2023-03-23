package com.hcl.labs.domi.providers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.impl.OAuth2AuthHandlerImpl;

public class TeamsOAuth2AuthHandlerImpl extends OAuth2AuthHandlerImpl {
  private final List<String> scopes;

  public TeamsOAuth2AuthHandlerImpl(Vertx vertx, OAuth2Auth authProvider, String callbackURL) {
    super(vertx, authProvider, callbackURL);
    this.scopes = new ArrayList<>();
  }

  public TeamsOAuth2AuthHandlerImpl(Vertx vertx, OAuth2Auth authProvider, String callbackURL,
      String realm) {
    super(vertx, authProvider, callbackURL, realm);
    this.scopes = new ArrayList<>();
  }

  @Override
  public OAuth2AuthHandler withScope(String scope) {
    this.scopes.add(scope);
    this.extraParams(new JsonObject().put("scopes", this.scopes));
    return this;
  }

  @Override
  public OAuth2AuthHandler withScopes(List<String> scopes) {
    this.scopes.addAll(scopes);
    this.extraParams(new JsonObject().put("scopes", this.scopes));
    return this;
  }

  private static final Set<String> OPENID_SCOPES = new HashSet<>();

  static {
    OPENID_SCOPES.add("openid");
    OPENID_SCOPES.add("profile");
    OPENID_SCOPES.add("email");
    OPENID_SCOPES.add("phone");
    OPENID_SCOPES.add("offline");
    OPENID_SCOPES.add("offline_access"); // Additional for O365
  }

  /**
   * The default behavior for post-authentication
   */
  @Override
  public void postAuthentication(RoutingContext ctx) {
    // Additional, moved from base class constructor
    boolean openId = scopes != null && scopes.contains("openid");

    // the user is authenticated, however the user may not have all the required scopes
    if (scopes != null && scopes.size() > 0) {
      final User user = ctx.user();
      if (user == null) {
        // bad state
        ctx.fail(403, new IllegalStateException("no user in the context"));
        return;
      }

      if (user.principal().containsKey("scope")) {
        final String passed_scopes = user.principal().getString("scope");
        if (passed_scopes != null) {
          // user principal contains scope, a basic assertion is required to ensure that
          // the scopes present match the required ones
          for (String scope : this.scopes) {
            // do not assert openid scopes if openid is active
            if (openId && OPENID_SCOPES.contains(scope)) {
              continue;
            }

            int idx = passed_scopes.indexOf(scope);
            if (idx != -1) {
              // match, but is it valid?
              if ((idx != 0 && passed_scopes.charAt(idx - 1) != ' ') ||
                  (idx + scope.length() != passed_scopes.length()
                      && passed_scopes.charAt(idx + scope.length()) != ' ')) {
                // invalid scope assignment
                ctx.fail(403, new IllegalStateException("principal scope != handler scopes"));
                return;
              }
            } else {
              // invalid scope assignment
              ctx.fail(403, new IllegalStateException("principal scope != handler scopes"));
              return;
            }
          }
        }
      }
    }
    ctx.next();
  }


}
