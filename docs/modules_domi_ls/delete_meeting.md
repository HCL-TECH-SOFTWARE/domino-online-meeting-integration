---
layout: default
title: Deleting Meetings (OAuth)
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 3
has_children: false
last_modified_date: 2021.03.24
---

## Deleting Meetings (OAuth Providers)

Sametime functionality is different, so this page only refers to other meeting providers.
{: .alert .alert-info}

### Agent

Deleting meetings goes via the **(DOMI_deleteXXXXMeeting)** agents.

`domiDeleteMeeting()` is then called, passing the `nodesToCopy` StringList and the service type as a string constant.

### domiDeleteMeeting

The `domiCreateMeeting()` function in the **domiUtilsFE** Script Library calls `dominoOnlineMeetingIntegrator.deleteMeeting()`. This checks whether there is a meeting ID and, if not, aborts.

`deleteMeeting()` then goes through the process for [`me.doMeetingRequest()`]({{'/modules_domi_ls/core_code#dominoonlinemeetingintegratordomeetingrequest' | relative_url}}).

After `doMeetingRequest()` push, if authorization has still failed the code exits - this will get picked up in `domiNotifyResult()` in **domiUtilsFE**.

If everything was fine, the code  clears the fields.

The code then comes back up to `domiDeleteMeeting` and calls [`domiNotifyResult()`]({{'/modules_domi_ls/core_code/#domiutilsfedominotifyresult' | relative_url}}).
