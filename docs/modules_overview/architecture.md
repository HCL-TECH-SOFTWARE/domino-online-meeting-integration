---
layout: default
title: Architecture
parent: Overview
nav_order: 1
has_children: false
last_modified_date: 2021.03.03
---

## Architecture


![Architecture]({{'/assets/images/Architecture.png' | relative_url}})

**DOMI** comprises two elements:

- A Vert.x web application which performs the OAuth dance with the online meeting provider services.
- An HCL Notes installation database with the design elements that need to be added to the mail template, configuration and an action to push the design elements into the mail template.

### HCL-Hosted Web Application

The OAuth process requires a developer to set up an OAuth app on the relevant meeting provider portals. The app requires a fixed, complete URL to redirect back to after the user has granted permission. So to avoid each customer needing to create an OAuth app for each of the meeting services, HCL has created and hosted a web application to perform the OAuth dance. The web application doesn't store any personal information, but acts as a gateway to display the token for the user.

The web application is also available, if customers prefer to run in locally. But they will need to set up their own OAuth apps on the relevant meeting provider portals, set up the redirect URLs for wherever the web application is being deployed, and pass their own client IDs and secrets to the application.

More details about the architecture can be found in the [Web Application]({{'/modules_webapp/overview' | relative_url}}) section.

### DOMI Installation Notes Application

The DOMI Installation database contains design notes and configuration for which need pushing to the mail template. This is what administrators will need to use to deploy the functionality into mail templates. More details about the use, architecture and how to extend it can be found in the [Notes Application]() section.