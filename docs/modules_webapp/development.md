---
layout: default
title: Development
parent: DOMI OAuth Web Application
nav_order: 6
has_children: true
last_modified_date: 2021.03.15
---

## Development

The web application is a Maven project in the webapp directory of the repo. It should be imported into Eclipse or your preferred Java IDE. Instructions for Eclipse are covered in [Running from Eclipse]({{'/modules_webapp/eclipse' | relative_url}}).

### Project Documentation Generation

Running the command `mvn post-site` will generate project documentation into the target folder and copy it across to the GitHub Pages site in the docs directory of the repo.

### Docker

The application is also available as a Docker image. This is built using the command `mvn compile jib:build`, but requires access to the remote repository.