---
layout: default
title: DOMI LotusScript Architecture
nav_order: 6
has_children: true
last_modified_date: 2021.03.24
---

## Documentation for DOMI LotusScript Code

The Domino Online Meeting Integration runs LotusScript for integrating with meeting providers.

### Forms
- Online Meeting Credentials: form for creating and managing online meeting credentials.  
- (domiSTLoginDlg): dialog form used when prompting for Sametime credentials from Online Meeting Credentials form.

### Subform
- DOMI_onlineMeetingActions: subform appended to the Calendar Entry form during installation. This contains all the action buttons for adding online meetings to a calendar entry.

### Views
- Online Meeting Credentials: the view end users access to create and manage onlione meeting credentials.  
- (DOMI_beMeetingCredentials): a programmatic view used by LotusScript code to retrieve online meeting credentials. Do not change the order of columns in this view.

### Agents
All interaction from action buttons is via Agents. All agents are hidden from Actions menu. Agent names should be self-explanatory, with the following abbreviations:  
- Gtm: GoToMeeting  
- ST: Sametime  
- Teams: Microsoft Teams
- Webex: WebEx  
- Zoom: Zoom

### Script Libraries
- domiConstantsBE: Script Library for all constants used within DOMI. **NOTE** Constants get cached by the Script Library and all dependent Script Libraries. So the scenario is a constant is defined in Script Library A and used in Script Library B. If you change the constant in Script Library A, Script Library B still retains the old value until either Script Library B is re-saved or all LotusScript is recompiled. However, if the constant is referenced via a Property Get statement, the updated constant value is reflected.
- domiIntegrator: Script Library containing the `DominoOnlineMeetingIntegrator` class that performs the OAuth meeting integration. There are also some private functions that are only used within this class.
- domiListsCollections: Script Library containing list and collection classes, particularly used to load objects and convert to JSON strings.
- domiUtilsBE: Script Library containing backend-only methods relating to DOMI. Restricting to only backend functions ensure this class can be used from outside the Notes Client.
- domiUtilsFE: Script Library containing functions used from Notes Client. This is the entry point from many of the agents.
- enhLogClass: Script Library containing Enhanced Log logging framework.
- FluentNotesJson: Script Library from Volt MX LotusScript Toolkit, for more easily contructing JSON objects.
- JsonDateTime: Script Library from Volt MX LotusScript Toolkit, for converting NotesDateTime objects to JSON.
- NotesHttpJsonRequestHelper: Script Library from Volt MX LotusScript Toolkit, for processing JSON requests in LotusScript.
- NotesHttpRequestHelper: Script Library from Volt MX LotusScript Toolkit, parent of NotesHttpJsonRequestHelper.
- NotesHttpStatusCodes: Script Library containing handling IETF RFC HTTP status codes.
- NotesRESTConsumer: Script Library containing NotesRESTconsumer class for processing HTTP Requests and extracting content.
- VoltMXHttpHelper: Script Library from Volt MX LotusScript Toolkit.