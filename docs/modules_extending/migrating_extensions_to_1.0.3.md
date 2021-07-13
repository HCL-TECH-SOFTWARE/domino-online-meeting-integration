---
layout: default
title: HCL Notes Extensions Migrating to 1.0.3
parent: Extending DOMI
nav_order: 3
has_children: false
last_modified_date: 2021.07.12
---

### nodesToCopy

1.0.3 moved the setting of `nodesToCopy` StringList from the agents to the `getDOMInodeNamesToCopy` function in `domiUtilsBE`. This avoids duplication across the create and update agents.

Steps to migrate are:
1. Cut the code setting `nodesToCopy.content` in the create or update agent.
2. Add to the Case statement in `getDOMInodeNamesToCopy` for the relevant meeting service(s).
3. Paste the code setting `nodesToCopy.content`. You will just need to change the StringList variable name from `nodesToCopy` to `result`.
4. Remove the declaration of `nodesToCopy` in the agents and remove it as a parameter to `domiCreateMeeting()` / `domiUpdateMeeting()` - the methods take only the meeting service type now.