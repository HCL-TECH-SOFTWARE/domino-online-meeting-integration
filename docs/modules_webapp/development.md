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

The application camn also be built as a Docker image locally, using Google jib. You will need Docker installed and Maven 3.5 or newer. It is built using the command `mvn compile jib:dockerBuild`. For more information on using jib to build a Docker image locally, see https://cloud.google.com/java/getting-started/jib#build-jib.

Alternatively, you can build using jib to deploy to a private repository, using `mvn compile jib:build`. You will need to amend the `<image>` tag in the pom.xml to point to the relevant server.