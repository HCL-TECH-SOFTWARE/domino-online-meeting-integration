---
layout: default
title: Updating Meetings (OAuth)
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 2
has_children: false
last_modified_date: 2021.03.24
---

## Updating Meetings (OAuth Providers)

Sametime functionality is different, so this page only refers to other meeting providers.
{: .alert .alert-info}

### Agent

Creating meetings goes via the **(DOMI_updateXXXXMeeting)** agents. Each agent has different entries in the `nodesToCopy` StringList. This list contains a **key** (the field to set on the Calendar Entry) and a **value** (the node to extract from the REST API response). The fields set will vary depending on what information is available from the REST API. It is normal that the same JSON element is put into multiple fields.

We will use GoToMeeting as an example:

```
nodesToCopy.content(|meetingId|) =        |meetingid|
nodesToCopy.content(|meetingTitle|) =     |subject|
nodesToCopy.content(|meetingURL|) =       |joinURL|
nodesToCopy.content(|ApptUNIDURL|) =      |joinURL|
```

`domiUpdateMeeting()` is then called, passing the `nodesToCopy` StringList and the service type as a string constant.

### domiUpdateMeeting

The `domiUpdateMeeting()` function in the **domiUtilsFE** Script Library calls `dominoOnlineMeetingIntegrator.updateMeeting()`.

The meetingID is extracted from the Calendar Entry. Some services don't return content, so specific fields for DOMI content are extracted so the code does not wipe out the field values.

At this point there is a `Select Case` statement to perform slightly different processes for the different services.  
- Microsoft Teams does not have an update endpoint, so the code calls `me.deleteMeeting()` followed by `me.createMeeting()`.
- Different services use different HTTP methods, so the verb may need to vary.
- Some services do not return content, so the `pushMeeting()` method will blank the items. So we need to reset them with the existing meeting values.

The code then goes through [`pushMeeting`]({{'/modules_domi_ls/core_code' | relative_url}}).

After `pushMeeting`, the code then comes back up to `domiUpdateMeeting` and calls [`domiNotifyResult()`]({{'/modules_domi_ls/core_code/#domiutilsfedominotifyresult' | relative_url}}).
