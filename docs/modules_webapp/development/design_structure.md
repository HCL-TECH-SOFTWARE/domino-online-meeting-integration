---
layout: default
title: Design Structure
parent: Development
grand_parent: DOMI OAuth Web Application
nav_order: 1
has_children: false
last_modified_date: 2021.03.15
---

## Design Structure

The application is a Java application built on Vert.x, with various files in resources.

### Package Structure

The Java structure is:  
- **com.hcl.domi**: the Vert.x verticle deployed and custom handlers.  
  - **Mainverticle**: the main verticle that gets loaded, creating and registering the web server and metrics server.  
  - **RefreshTokenHandler**: the handler for requests to refresh tokens. The handler extracts the token, calls the refresh method of the relevant OAuth provider and echoes back the response.  
  - **RevokeTokenHandler**: the handler to revoke tokens. The handler extracts the token, calls the revoke method of the relevant OAuth provider and returns a 200 status code.  
- **com.hcl.domi.metrics**: the classes for Prometheus metrics.  
  - **DOMIStatistics**: an interface for managing metrics.  
  - **DOMIStatisticsHolder**: an enum implementing the DOMIStatistics interface.  
- **com.hcl.domi.providers**: the classes for managing OAuth providers.  
  - **OnlineMeetingProviderFactory**: an interface for holding details for a specific OAuth provider - client ID, client secret, provider name and host name for this web server. This is also where all routes are created for the meeting provider, using the `createAndEnableRoutes` method.  
  - **OnlineMeetingProviderFactoryHolder**: a class implementing the OnlineMeetingProviderFactory interface.  
  - **OnlineMeetingProviderParameterBuilder**: a builder class to receive, validate and build all parameters required to be passed into `OnlineMeetingProviderFactory.createAndEnableRoutes()`.  
  - **OnlineMeetingProviderParameters**: a class to hold all parameters to be passed into `OnlineMeetingProviderFactory.createAndEnableRoutes()`.  
  - **ZoomOAuth2API**: a class extending OAuth2API because Zoom's revoke API expects the token to be passed as a query parameter rather than the IETF standard of including it in the POST body.  
  - **ZoomOAuth2ProviderImpl**: a class extending OAuth2AuthProviderImpl to allow code to call an overloaded `tokenRevocation()` method.
- **com.hcl.domi.tools**: the generic classes for use elsewhere.  
  - **DOMIConstants**: all constants used throughout the application.  
  - **DOMIException**: a class for exceptions within this application.
  - **DOMIProvider**: enum for variable information relating to a meeting provider.
  - **DOMIUtils**: static methods for use throughout the application.

### Resources

The files here are:  
- webroot/index.html: a landing page in case anyone navigates to the server itself.  
- config.json: the configuration for the Vert.x application.  
- domi.properties: a properties file extracting values from the Maven configuration. 
- error.html: a generic HTML error page.  
- log4j.properties: a properties file for logging settings.  
- meetings.mustache: an HTML file with mustache variables, used to merge in the refresh token and meeting provider details. The resulting HTML is displayed in response to a successful OAuth dance.
- notfound.html: an HTML page for 404 errors.