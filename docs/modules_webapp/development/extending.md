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

**The Client ID and secret must be passed as environment variables and cannot be hard-coded into this application, for security reasons.**

### Changes Required

Release 1.0.5 makes extending DOMI's web application easier than ever before. For a standard OAuth provider, you only need to make changes in one place: `com.hcl.labs.domi.tools.DOMIProvider`. This is an enum that contains all the information required to set up a meeting provider.

If the meeting provider has an endpoint to support revocation of a token, use the constructor with 11 parameters. If not, the constructor taking 9 parameters can be used. The enum name can be arbitrary, it is only used internally.

The parameters to pass are:
- A label for the meeting provider. This will be used on the tokens page, so should be designed to be recognisable for end users and can contain spaces.  
- The URL on the meeting provider's REST API to do the OAuth dance and retrieve a short-lived code. This will be the first URL the user will need to go to, which will prompt them to enter their credentials. 
- The callback route, an endpoint on this server that the user should be redirected back to after they have entered their credentials. This will be included as a query string parameter to the XXX_AUTHORIZE_URL as URL to redirect back to. This endpoint, along with the host name, will also need to be entered on the meeting provider's side. As part of their validation, they will verify that the redirect URL sent with the XXX_AUTHORIZE_URL matches one you've told them to expect.  
-  The scopes you need from the OAuth process. Only include the minimum scopes you need to create, update and delete meetings.  
-  The path followed by "index.html" that the HCL Notes Online Meeting Provider Credentials form should launch to retrieve a token. This will also be used to catch phishing attempts to redirect the token to a malicious third party.  
-  The token URL, the URL on the meeting provider's REST API to swap:  
   -  either the short-lived code for an access token and refresh token.
   -  or the refresh token for a new access token and refresh token.  
-  The refresh route, an endpoint on this server that HCL Notes should call to refresh a token.  
-  The environment variable to look for to get the relevant Client ID.  
-  The environment variable to look for to get the relevant Client secret.  
-  The revoke route, an endpoint on this server that HCL Notes should call to revoke a token. Only include this if the meeting provider supports revoking tokens.  
-  The URL on the meeting provider's REST API to revoke tokens. Not all OAUth APIs support this functionality. Only include this if the meeting provider supports revoking tokens.  

### Exceptions

In the case of Webex, as well as having the client ID and secret encoded in the Authorization header, they also require the client ID and secret in the body of the POST request to the token URL. As a result, in the MainVerticle's `createMeetingProviderRoutes` method there is an if statement to add extra information to the `extraParams` JsonObject.

### Testing

If successful, when the DOMI web application starts up, the console will log information for each DOMIProvider:  
- "Enabled OAuth callback" for the callback route.
- "Enabled OAuth validation" for the path on this server to retrieve the token.  
- "Enabled OAuth token revocation" for the revoke route (only logged if a revoke route is provided).  
- "Enabled OAuth token refresh" for the refresh route.  