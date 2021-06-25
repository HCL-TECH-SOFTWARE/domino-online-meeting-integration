---
layout: default
title: Running from Eclipse
parent: DOMI OAuth Web Application
nav_order: 2
has_children: false
last_modified_date: 2021.03.09
---
## Running from Eclipse

The application can be tested from the source repository, once downloaded.

### Pre_Requisites
Running the web application requires Maven and Java 8. The dependencies are all available on Maven Central.

### Eclipse Run / Debug Configuration

1. Import the project into Eclipse by choosing File > Import.
2. In the subsequent dialog prompting to select an import wizard, choose Maven > Existing Maven Projects.  
![Import]({{'/assets/images/webapp/import_maven.png' | relative_url}})
3. For the root directory, navigate to the webapp directory of the repository. Only one project will be available to select, the pom.xml of com.hcl.labs:com.hcl.labs.domi. Select this and click Finish.
4. You will then need to run a Maven install to ensure you have all dependencies. This can be done either from Eclipse (right-click the project and select Run As > Maven install) or the command line (`mvn install` in the webapp directory.)

After a successful install, you are ready to run the application. All Client IDs and Client Secrets are passed as environment variables.

1. Use the toolbar or menus to create a Run Configuration / Debug Configuration. The configuration template to use is a Java Application.
2. The main class to run will be `com.hcl.labs.domi.MainVerticle`.  
![Launch Main]({{'/assets/images/webapp/launch_main.png' | relative_url}})
3. On the Environment tab, enter the following environment variables, with the relevant client ID and client secrets.  
![Launch Envs]({{'/assets/images/webapp/launch_envs.png' | relative_url}})

|Environment Variable  |Description       |
|----------------------|------------------|
|GTM_CLIENT_ID         |Client ID for your GoToMeeting OAuth application, setup on GoToMeeting (LogMeIn Developers) site|
|GTM_CLIENT_SECRET     |Client secret for your GoToMeeting OAuth application|
|OAUTH_HOSTNAME        |Host and port the web application is available on, e.g. "http://localhost:8878". This will be appended with the relevant callback path (e.g. "/zoomCallback") during the OAuth dance as the redirect URL. It will need to match the callback URL set up in the OAuth application on the relevant online meeting providers developer console.|
|TEAMS_CLIENT_ID       |Client ID for your Teams OAuth application, setup in the Azure Portal|
|TEAMS_CLIENT_SECRET   |Client secret for your Teams OAuth application|
|WEBEX_CLIENT_ID       |Client ID for your Webex OAuth application, setup on Webex for Developers site|
|WEBEX_CLIENT_SECRET   |Client secret for your Webex OAuth application|
|ZOOM_CLIENT_ID        |Client ID for your Zoom OAuth application, setup on Zoom App Marketplace site|
|ZOOM_CLIENT_SECRET    |Client secret for your Zoom OAuth application|

If a service is not being used, just leave the relevant client ID and secret environment variables blank. Only services with a completed client ID and secret will be enabled at startup. Otherwise the endpoints will not be loaded and will result in a 404 error.

### Startup

If successfully configured, the application will start up when you use the Run / Debug Configuration. The console will confirm which OAuth endpoints have been loaded and the URLs to use for them.

- OAuth callback URL needs to match what is set up in the OAuth application on the relevant online meeting provider's developer site.
- OAuth validation means a token will be loaded for any URL at the relevant path. The URL DOMI's Notes integration uses ends "/index.html". Attempting to use anything else will perform the OAuth dance and go to "/index.html" regardless.
- OAuth token refresh is the URL DOMI's Notes integration will call to refresh tokens.
- OAuth logout is the URL to revoke access for DOMI.

### Landing Page

A simple landing page has been added to the web application, available at the base URL (e.g. "http://localhost:8878").

### Metrics

Metrics, configured for Prometheus, are exposed on port 8879. This gives stats for specific endpoints used (number of calls, duration), as well as for Vertx generally.