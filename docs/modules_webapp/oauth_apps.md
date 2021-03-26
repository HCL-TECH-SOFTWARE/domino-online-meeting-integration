---
layout: default
title: Meeting Provider OAuth Apps
parent: DOMI OAuth Web Application
nav_order: 1
has_children: false
last_modified_date: 2021.03.19
---

## Meeting Provider OAuth Apps

If you are happy to leverage the HCL-hosted OAuth broker application, you do not need to worry about setting up OAuth applications on each of the meeting providers.
{:.alert .alert-info}

### Setting up the OAuth Apps

To run the application yourself, you will need to set up OAuth applications on the developer console for all online meeting providers you wish to use - Zoom, Teams, GoToMeeting(LogMeIn Developers site), and Webex. You will need to pass the client ID and client secret at runtime into the web application, so make a note of those. The scopes required are documented in [OAuth Scopes]({{'/modules_overview/oauth_scopes' | relative_url}}). 

### Redirect URLs for OAuth Apps

By default the application will run on port 8878. This can be configured in the config.json file. The testing locally, the redirect URLs will be "http://localhost:8878/" +
 
- zoomCallback - Zoom
- teamsCallback - Microsoft Teams  
- gtmCallback - GoToMeeting  
- webexCallback - Cisco Webex 

Alternatively, software like [ngrok](https://ngrok.com) can be used to provide public URLs for testing the web application. The public URL provided will replace "http://localhost:8878".