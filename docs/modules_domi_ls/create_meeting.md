---
layout: default
title: Creating Meetings (OAuth)
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 1
has_children: false
last_modified_date: 2021.03.24
---

## Creating Meetings (OAuth Providers)

Sametime functionality is different, so this page only refers to other meeting providers.
{: .alert .alert-info}

### Agent

Creating meetings goes via the **(DOMI_scheduleXXXXMeeting)** agents. Each agent has different entries in the `nodesToCopy` StringList. This list contains a **key** (the field to set on the Calendar Entry) and a **value** (the node to extract from the REST API response). The fields set will vary depending on what information is available from the REST API. It is normal that the same JSON element is put into multiple fields.

We will use GoToMeeting as an example:

```
nodesToCopy.content(|meetingId|) =        |meetingid|
nodesToCopy.content(|meetingTitle|) =     |subject|
nodesToCopy.content(|meetingURL|) =       |joinURL|
nodesToCopy.content(|ApptUNIDURL|) =      |joinURL|
```

`domiCreateMeeting()` is then called, passing the `nodesToCopy` StringList and the service type as a string constant.

### domiCreateMeeting

The `domiCreateMeeting()` function in the **domiUtilsFE** Script Library calls `dominoOnlineMeetingIntegrator.createMeeting()`.

This goes through [`pushMeeting`]({{'/modules_domi_ls/core_code' | relative_url}}).

The code then comes back up to `domiCreateMeeting` and calls [`domiNotifyResult()`]({{'/modules_domi_ls/core_code/#domiutilsfedominotifyresult' | relative_url}}).
