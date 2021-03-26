---
layout: default
title: Extending The Web Application
parent: Development
grand_parent: DOMI OAuth Web Application
nav_order: 2
has_children: false
last_modified_date: 2021.03.15
---

## Extending the Web Application

The web application is designed to be extended to support other OAuth-enabled meeting providers. To do this, you will need:  
- A Client ID and secret for the OAuth application you have set up for the relevant meeting provider.  
- URLs for requesting a short-lived code, converting that to a token, refreshing the token and (if supported) revoking the token.  
- Details of the REST API endpoints for requesting, refreshing and (if supported) revoking OAuth tokens.

**The Client ID and secret should never be hard-coded into this application, for security reasons.**

### Changes Required

Changes will need to be made in three places:

#### 1. Constants

**com.hcl.domi.utils.DOMIConstants** holds key information for the meeting provider. The constants for a specific meeting provider are grouped together. Replacing `XXXX` with a prefix for the relevant meeting provider, you will need to add:  
- **XXXX_AUTHORIZE_URL**: the URL on the meeting provider's REST API to do the OAuth dance and retrieve a short-lived code. This will be the first URL the user will need to go to, which will prompt them to enter their credentials.  
- **XXXX_CALLBACK_ROUTE**: the endpoint on this server that the user should be redirected back to after they have entered their credentials. This will be included as a query string parameter to the XXX_AUTHORIZE_URL as URL to redirect back to. This endpoint, along with the host name, will also need to be entered on the meeting provider's side. As part of their validation, they will verify that the redirect URL sent with the XXX_AUTHORIZE_URL matches one you've told them to expect.  
- **XXXX_CLIENT_ID**: the environment variable to look for to get the relevant Client ID.  
- **XXXX_CLIENT_SECRET**: the environment variable to look for to get the relevant Client secret.  
- **XXX_REVOCATION_URL**: the URL on the meeting provider's REST API to revoke tokens. Not all OAUth APIs support this functionality. Only include this if the meeting provider supports revoking tokens.  
- **XXXX_PATH**: the path followed by "index.html" that the HCL Notes Online Meeting Provider Credentials form should launch to retrieve a token.  
- **XXXX_REFRESH_ROUTE**: the endpoint on this server that HCL Notes should call to refresh a token.  
- **XXX_REVOKE_ROUTE**: the endpoint on this server that HCL Notes should call to revoke a token. Only include this if the meeting provider supports revoking tokens.  
- **XXXX_SCOPES**: the scopes you need from the OAuth process. Only include the minimum scopes you need to create, update and delete meetings.  
- **XXXX_TOKEN_URL**: the URL on the meeting provider's REST API to swap the short-lived code for a token.  
- **XXXX_LABEL**: a label for the meeting provider. This will be used on the tokens page, so should be designed to be recognisable for end users and can contain spaces.

#### 2. Loading Environment Variables

Only specific environment variables are added to the config. These are defined in `DOMIUtils.getEnvironmentParamsToRead()`. If you add a new OAuth meeting provider, add the constants for the client ID and secret to the `result` JsonArray.

#### 3. Registering the OAuth Provider

Vertx has classes for handling OAuth dances which minimise the amount of code you need to enter. If the meeting provider follows OAuth standards, you should be able to use the standard classes without modification. Where providers don't follow standards, changes are needed - and this was done for Zoom.

To add a new provider, the changes just need making to `MainVerticle.createMeetingProviderRoutes()`.

1. Add final String variables for the client ID and secret, using the existing ones as examples. If the value for client ID or secret is blank, the endpoints will not be added for that provider.  
2. Add an additional block creating an `OnlineMeetingProviderFactory` and `OnlineMeetingProviderParameterBuilder`, and calling the factory's `createAndEnableRoutes()` method. The `extraParams` JsonObject contains any additional content for the body for the call to the XXXX_TOKEN_URL. In the case of Webex, as well as having the client ID and secret encoded in the Authorization header, they also require the client ID and secret in the body of the POST request - hence the extraParams object passed into `webexBuilder`.

If successful, when the DOMI web application starts up, the console will log:  
- OAuth validation for the XXXX_PATH URL.  
- Token revocation for the XXXX_REVOKE_ROUTE (if not blank).  
- Token refresh for the XXXX_REFRESH_ROUTE.  
- OAuth callback for the XXXX_CALLBACK_ROUTE.