---
layout: default
title: Managing Credentials
parent: DOMI LotusScript Architecture
nav_order: 2
has_children: false
last_modified_date: 2021.03.24
---

## Managing Credentials

To understand the OAuth flow, review the [Process Flow page]({{'/modules_overview/process_flow' | relative_url}}).

### Getting an OAuth Token

The process for getting an OAuth token just performs a `NotesUIWorkspace.URLOpen()` to launch the request to retrieve a token in a browser. The action button in Online Meeting Credentials form calls the **(DOMI_getOAuthTokenFE)** agent, which calls `getOAuthToken()` in **domiUtilsFE** Script Library.

### Validating an OAuth Token

Pasting a token into the Online Meeting Credentials form triggers **(DOMI_validateOauthTokenFE)** agent, which calls `domiValidateOAuthToken()` in **domiUtilsFE** Script Library. This uses `DominoOnlineMeetingIntegrator.refreshAuthToken()` to make a request to the DOMI web application with the refresh token, which will return valid access refresh tokens.

The *Validate OAuth Token* action button does the same.

### Revoking an OAuth Token

The *Revoke OAuth Token* action button triggers **(DOMI_revokeOAuthTokenFE)** agent, which calls `domiRevokeOAuthToken()` in **domiUtilsFE** Script Library. This runs `DominoOnlineMeetingIntegrator.refreshAuthToken()` and then `DominoOnlineMeetingIntegrator.revokeAuthToken()` to revoke the OAuth token.

### Getting a Sametime Token

Sametime does not use OAuth, but does return access and refresh tokens. The process requires two calls:  
- An initial call to **/refresh** endpoint to receive a `set-cookie` header with an **_csrf** token and an `x-csrf-token` header.
- A call to **/login** endpoint to retrieve sametimeJwt and sametimeRefresh tokens.