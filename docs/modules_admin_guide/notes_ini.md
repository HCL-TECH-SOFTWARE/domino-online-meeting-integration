---
layout: default
title: Notes.ini Variables for Logging
parent: HCL Domino Administrators
nav_order: 2
has_children: false
last_modified_date: 2021.03.23
---

## Notes.ini Variables for Logging

Logging is done by default to the log.nsf. To log to log.nsf on the server, users will need at least depositor access. If they do not have access, logs are written to the mail database and will be accessible in the All Documents view. By default only errors are logged.

### DOMI_LOG_LEVEL

The log level can be changed by adding a notes.ini variable `DOMI_LOG_LEVEL`. Valid settings are integers:  
- 0: OFF - logging is disabled.  
- 200: ERROR - only exceptions are logged. This is the setting used if the notes.ini variable is omitted.  
- 300: WARN - warnings and exceptions are logged.  
- 400: INFO - method results, warnings and exceptions are logged.  
- 600: TRACE - actions, HTTP Request and Response content, method results, warnings and exceptions are logged.

EXAMPLE:
`DOMI_LOG_LEVEL=600`

For security purposes, access and refresh tokens are masked in requests and responses at TRACE level.

### DOMI_LOCAL_ECHO

Setting `DOMI_LOCAL_ECHO=1` echoes all log events to the Notes Client status bar as well as writing to the log.

Remember that the Notes Client will need to be restarted to take account of changes in notes.ini variables.
{: .alert .alert-info}