---
layout: default
title: Core Code
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 4
has_children: false
last_modified_date: 2021.03.24
---

## Core Code

### dominoOnlineMeetingIntegrator.pushMeeting()

`pushMeeting()` validates the MeetingService and Calendar Entry details. An error is returned to the user if invalid, for example Subject not yet completed or start / end date/time are in the past.

`pushMeeting()` then calls `getMeetingBodyArgs()` to extract the content required for the REST service call to the meeting provider and add it to the `result` VariantList. The content required varies from provider to provider, so there is a `Select Case` statement based on the meeting provider in use. `meetingTime.startITC` and `meetingTime.endITC` are set to the relevant JSON date/time formats. This allows us to just reference those variables afterwards, in some cases using them also for the response.

`pushMeeting()` then goes through the process for [`me.doMeetingRequest()`](#dominoonlinemeetingintegratordomeetingrequest).

After `doMeetingRequest()` push, if authorization has still failed the code exits - this will get picked up in `domiNotifyResult()` in **domiUtilsFE**.

If everything was fine, the code calls `me.MeetingValues()` to extract the values from the JSON object into a VariantList. This is iterated to set fields in the Calendar Entry. Some additional fields are also set, some comprising content for support purposes and others to generate the "Join Online" link. There is also another `Select Case` statement to generate the content for the **Location** field.

The code will then continue back in the relevant method - [createMeeting]({{'/modules_domi_ls/create_meeting#domicreatemeeting' | relative_url}}) or [updateMeeting]({{'/modules_domi_ls/update_meeting#domiupdatemeeting' | relative_url}}).

### dominoOnlineMeetingIntegrator.doMeetingRequest()

`dominoOnlineMeetingIntegrator.doMeetingRequest()` first calls `me.doMeetingRequestRAW()`.

`doMeetingRequestRAW()` retrieves the relevant URL for the meeting service using `me.getDOMIurl()` via `getDOMIendpointURL()` in **domiUtilsBE**. It then calls `me.RESTconsumer.processRequest()` to perform the actual HTTP request and loads the JSON response into a `NotesJSONNavigator`, if possible. `doMeetingRequestRAW()` then validates the response code. If the HTTP response code does not indicate an error, it iterates over the `nodesToCopy` StringList that was passed across, extracts the values and puts them in a new JSON object with the variable name `target`. So using the GoToMeeting example, the JSON object returned by the REST API may just be:

```
{
    "meetingid": "1234",
    "subject": "Demo Meeting",
    "joinURL": "https://gotomeet.me/123456
}
```

This will get move to a new JSON object (`target`) with the format:

```
{
    "meetingId": "1234",
    "meetingTitle": "Demo Meeting",
    "meetingURL": "https://gotomeet.me/123456,
    "ApptUNIDURL": "https://gotomeet.me/123456
}
```

After `doMeetingRequestRAW()`, `doMeetingRequest()` loads the JSON into `me.JSONhelper` and, if there was an authorization error - i.e. the auth token has expired - it refreshes the token and calls `doMeetingRequestRAW()` again.

The code then continues back in [deleteMeeting]({{'/modules_domi_ls/delete_meeting#domideletemeeting' | relative_url}}) for deletes or [`pushMeeting`](#dominoonlinemeetingintegratorpushmeeting) for creates and updates.

### domiUtilsFE.domiNotifyResult()

If creation was successful, the document is reloaded to pick up backend changes and confirmation given to the user.

If not, there is a check for if authorization failed. If so, the end user is prompted to edit their Online Meeting Credentials and request a new token. If they choose to do so, the Online Meeting credentials document is opened in edit mode. If validation failed, the user is informed which fields need amending. If the request failed for other reasons, the error code and error message is displayed. This may be needed for support purposes. They are also pointed towards the log for further details.