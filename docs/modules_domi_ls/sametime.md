---
layout: default
title: Sametime Meeting Code
parent: DOMI LotusScript Architecture
nav_order: 4
has_children: false
last_modified_date: 2021.03.24
---

## Sametime Meeting Code

The Sametime Meetings REST service calls are different to the OAuth meeting service providers. Although some functions and classes are re-used between the two, they are at a more granular level and the wrapper code around those granular calls is peculiar to Sametime.

The entry point for the action buttons is agents with names consistent with the other meeting service processes, e.g. **(DOMI_scheduleSTMeeting)**.

The process flow for making the create, update and delete operations has a consistent flow. The first two calls are done in the `getSTHeaderToken()` function in **domiUtilsBE**

### Call 1: /refresh Endpoint

For the first call, the code goes into `getSTTempTokens()`. This uses a `NotesRESTconsumer` to make a GET request on **/refresh** endpoint of the Sametime Meetings API. This call is expected to result in a 401 HTTP status code (Unauthorised), but will return a `set-cookie` header with an **_csrf** token and a `x-csrf-token` header.

### Call 2: /refresh Endpoint

`getSTHeaderTokens` then uses the `set-cookie` header is used to construct a `cookie` header containing the **_csrf** cookie value and the refresh token from the credentials. A `NotesRESTconsumer` is created to make a second GET request on **/refresh** endpoint. This is expected to result in a successful request, which will return a `set-cookie` header with an auth token and an updated `x-csrf-token` header.

### Call 3: Create / Update / Delete Meeting

The agents then update the **cookie** header with the sametime auth JWT token. 

For create and update, a payload is created with the meeting details. Meeting names need to be unique for Sametime Meetings and certain characters are prohibited. As a result, the meeting name is sanitised before added to the payload.

The relevant REST service call is then made to create, update or delete the meeting. If the REST service call was successful, the meeting fields in the calendar entry are updated and success confirmed.