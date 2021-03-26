---
layout: default
title: Logging
parent: DOMI LotusScript Architecture
nav_order: 1
has_children: false
last_modified_date: 2021.03.24
---

## Logging

Logging is done using Enhanced Log. This logs to the log.nsf, if possible, otherwise the current database.

There are two Notes.ini variables for handling the verbosity of logging, **DOMI_LOG_LEVEL** and **DOMI_LOCAL_ECHO**. For more details, see [Notes.ini Variables for Logging]({{'/modules_admin_guide/notes_ini' | relative_url}}).

Logging must be instantiated before at the start of any code to run. This is done using `Call instantiateDOMILog()`. That subroutine is in **domiUtilsBE** Script Library. This checks the notes.ini variables and starts up logging at the appropriate level.