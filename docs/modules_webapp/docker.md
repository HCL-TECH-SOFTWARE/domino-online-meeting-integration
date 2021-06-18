---
layout: default
title: Running from Docker
parent: DOMI OAuth Web Application
nav_order: 3
has_children: false
last_modified_date: 2021.03.15
---

## Running from Docker

The web application is also available as a Docker image, hclsoftware/domino-domi.

NOTE: You will need to build the Docker image locally, it is not available to download. To do so, follow the instructions on the [development page](../development).
{: .alert .alert-warning}

## Creating a Docker Container

You can create and run a Docker container with the standard `docker run` command, e.g.:

```docker
docker run -it -e OAUTH_HOSTNAME=http://localhost:8878 -e WEBEX_CLIENT_ID=YOUR_CLIENT_ID -e WEBEX_CLIENT_SECRET=YOUR_CLIENT_SECRET -p 8878:8878 -p 8879:8879 hclsoftware/domino-domi
```

### Environment Variables

The environment variables required to configure the application will need to be passed into the Docker container at initialisation.

|Environment Variable  |Description       |
|----------------------|------------------|
|GTM_CLIENT_ID         |Client ID for your GoToMeeting OAuth application, setup on GoToMeeting (LogMeIn Developers) site|
|GTM_CLIENT_SECRET     |Client secret for your GoToMeeting OAuth application|
|OAUTH_HOSTNAME        |Host and port the web application is available on, e.g. "http://localhost:8878". This will be appended with the relevant callback path (e.g. "/zoomCallback") during the OAuth dance as the redirect URL. It will need to match the callback URL set up in the OAuth application on the relevant online meeting providers developer console.|
|TEAMS_CLIENT_ID       |Client ID for your Teams OAuth application, setup in the Azure Portal|
|TEAMS_CLIENT_SECRET   |Client secret for your Teams OAuth application|
|WEBEX_CLIENT_ID       |Client ID for your Webex OAuth application, setup on Cisco Webex for Developers site|
|WEBEX_CLIENT_SECRET   |Client secret for your Webex OAuth application|
|ZOOM_CLIENT_ID        |Client ID for your Zoom OAuth application, setup on Zoom App Marketplace site|
|ZOOM_CLIENT_SECRET    |Client secret for your Zoom OAuth application|

If a service is not being used, just leave the relevant client ID and secret environment variables blank. Only services with a completed client ID and secret will be enabled at startup. Otherwise the endpoints will not be loaded and will result in a 404 error.

### Ports

The ports for the web application and, if required, the metrics server will also need to be exposed. This is done with standard Docker run parameters, `-p HOST_PORT:CONTAINER_PORT`. The container ports are:  
- 8878 for the web server.  
- 8879 for the metrics server.

### Startup

If successfully configured, the application will start up when you start the Docker container. The console will confirm which OAuth endpoints have been loaded and the URLs to use for them.

- OAuth callback URL needs to match what is set up in the OAuth application on the relevant online meeting provider's developer site.
- OAuth validation means a token will be loaded for any URL at the relevant path. The URL DOMI's Notes integration uses ends "/index.html". Attempting to use anything else will perform the OAuth dance and go to "/index.html" regardless.
- OAuth token refresh is the URL DOMI's Notes integration will call to refresh tokens.
- OAuth logout is the URL to revoke access for DOMI.

### Landing Page

A simple landing page has been added to the web application, available at the base URL (e.g. "http://localhost:8878").