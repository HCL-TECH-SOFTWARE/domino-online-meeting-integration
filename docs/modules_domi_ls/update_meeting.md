---
layout: default
title: Updating Meetings (OAuth)
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 2
has_children: false
last_modified_date: 2021.07.12
---

## Updating Meetings (OAuth Providers)

Sametime functionality is different, so this page only refers to other meeting providers.
{: .alert .alert-info}

### Agent

Updating meetings goes via the **(DOMI_updateXXXXMeeting)** agents. `domiUpdateMeeting() is called passing the relevant service type as a string constant.

<div class="panel panel-warning">
    <div class="panel-heading">Deprecated Functionality - 1.0.2 and Lower</div>
    <div class="panel-body">
<b>Prior to version 1.0.3</b> each agent had different entries in the `nodesToCopy` StringList. This list contained a <b>key</b> (the field to set on the Calendar Entry) and a <b>value</b> (the node to extract from the REST API response). The fields set vary depending on what information is available from the REST API.
<br/><br/>
`domiUpdateMeeting()` was then called, passing the `nodesToCopy` StringList and the service type as a string constant.
<br/><br/>
This has now been moved to domiUtilsBE Script Library and getDOMInodeNamesToCopy function.
    </div>
</div>

### domiUpdateMeeting

The `domiUpdateMeeting()` function in the **domiUtilsFE** Script Library calls `dominoOnlineMeetingIntegrator.updateMeeting()`.

The meetingID is extracted from the Calendar Entry. Some services don't return content, so specific fields for DOMI content are extracted so the code does not wipe out the field values.

At this point there is a `Select Case` statement to perform slightly different processes for the different services.  
- Microsoft Teams does not have an update endpoint, so the code calls `me.deleteMeeting()` followed by `me.createMeeting()`.
- Different services use different HTTP methods, so the verb may need to vary.
- Some services do not return content, so the `pushMeeting()` method will blank the items. So we need to reset them with the existing meeting values.

The code then goes through [`pushMeeting`]({{'/modules_domi_ls/core_code' | relative_url}}).

After `pushMeeting`, the code then comes back up to `domiUpdateMeeting` and calls [`domiNotifyResult()`]({{'/modules_domi_ls/core_code/#domiutilsfedominotifyresult' | relative_url}}).
