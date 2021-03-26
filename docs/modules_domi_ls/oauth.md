---
layout: default
title: OAuth Meeting Code
parent: DOMI LotusScript Architecture
nav_order: 3
has_children: true
last_modified_date: 2021.03.24
---

## OAuth Meeting Code

The code for the OAuth meeting service providers (Zoom, Microsoft Teams, WebEx, GoToMeeting) is virtually identical, with differences only for endpoints, some HTTP verbs, and case statements for creating the request body and parsing the response body.