---
layout: default
title: Data Flow
parent: Overview
nav_order: 3
has_children: false
last_modified_date: 2021.03.02
---

## Data Flow

Sensitive information is passed around in many locations during the process flow. This document covers the various pieces of data and how they are stored.

### Client IDs and Secrets

Communication with the OAuth meeting services (except Sametime) requires an OAuth application to be deployed on the relevant service. The are used only within the web application. They need to be passed in as environment variables when the application loads and are held only in memory while the application runs. There are no endpoints that expose them. They are passed as an Authorization header in server-side HTTP requests to the relevant meeting service and are never exposed to browser clients.

### Usernames and Passwords (Not Sametime)

Usernames and passwords are not received or used by any HCL-developed technology in the solution. They are entered only in the OAuth login pages of the relevant meeting services.

### Usernames and Passwords (Sametime)

For Sametime, a username and password is entered into a Notes Client UI to initially set up the service. They are not stored, but passed directly to Sametime in an HTTP request (REST service call) running via LotusScript on the Notes Client.

### Short-lived Codes for OAuth Tokens

The web application has endpoints to which the OAuth meeting services send a short-lived code. This is extracted from the query string and sent back in a server-side HTTP request (REST service call) to the relevant meeting service. It is not stored in the web application.

### Access Tokens and Refresh Tokens

#### Web Application

When the web application sends a server-side HTTP request with the short-lived code, the response contains an Authorization token and Refresh token. They are stored temporarily in the user session and printed to the browser. There are no persistent sessions in the web application, so the tokens are not stored beyond the life of the request.

When HCL Notes Client sends a LotusScript HTTP request to the web application to refresh the tokens, it receives the refresh token in the HTTP request's Authorization header, extracts it and sends it in an HTTP request's Authorization header to the relevant OAuth meeting service. The response contains an Authorization token and Refresh token and other metadata, which is echoed back to the user without modification. There are no persistent sessions in the web application, so the tokens are not stored beyond the life of the request.

#### Web Browser

The web application renders a web page with a refresh token, if returned by the current valid OAuth dance. If the OAuth dance fails, no token is displayed.

#### Notes Client

Authorization and Refresh token are stored in the meeting credentials. This is not stored encrypted. The Authorization token is only valid for a short time (usually one hour following a successful authentication / refresh). The Refresh token can only be used by sending to the web application. Domino security handles access to the mail database. Further restrictions could be applied, but the right approach will depend on what is needed to support delegated access to mail databases.

When the Notes Client creates / updates / deletes an online meeting, the HTTP request is sent via LotusScript from the client. So the Notes Client is the only application where the Authorization token is used.

Where the service offers more than just meeting functionality (e.g. Teams) the token scopes are restricted to CRUD operations on meetings.