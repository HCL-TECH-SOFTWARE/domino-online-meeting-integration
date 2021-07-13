---
layout: default
title: Creating Meetings (OAuth)
parent: OAuth Meeting Code
grand_parent: DOMI LotusScript Architecture
nav_order: 1
has_children: false
last_modified_date: 2021.07.12
---

## Creating Meetings (OAuth Providers)

Sametime functionality is different, so this page only refers to other meeting providers.
{: .alert .alert-info}

### Agent

Creating meetings goes via the **(DOMI_scheduleXXXXMeeting)** agents. `domiCreateMeeting() is called passing the relevant service type as a string constant.

<div class="panel panel-warning">
    <div class="panel-heading">Deprecated Functionality - 1.0.2 and Lower</div>
    <div class="panel-body">
<b>Prior to version 1.0.3</b> each agent had different entries in the `nodesToCopy` StringList. This list contained a <b>key</b> (the field to set on the Calendar Entry) and a <b>value</b> (the node to extract from the REST API response). The fields set vary depending on what information is available from the REST API.
<br/><br/>
`domiCreateMeeting()` was then called, passing the `nodesToCopy` StringList and the service type as a string constant.
<br/><br/>
This has now been moved to domiUtilsBE Script Library and getDOMInodeNamesToCopy function.
    </div>
</div>

### domiCreateMeeting

The `domiCreateMeeting()` function in the **domiUtilsFE** Script Library calls `dominoOnlineMeetingIntegrator.createMeeting()`.

This goes through [`pushMeeting`]({{'/modules_domi_ls/core_code' | relative_url}}).

The code then comes back up to `domiCreateMeeting` and calls [`domiNotifyResult()`]({{'/modules_domi_ls/core_code/#domiutilsfedominotifyresult' | relative_url}}).
