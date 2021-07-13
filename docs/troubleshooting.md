---
layout: default
title: Troubleshooting
nav_order: 8
has_children: false
last_modified_date: 2021.06.25
---

## Troubleshooting

### Version Issues

Remember that to use DOMI the Notes Client will need to be HCL Notes 11.0.1 FP3 or higher. If not, users will receive a warning when opening the **Online Meeting Credentials** form. Retrieving an OAuth token and creating meetings may work. Updating and deleting is not expected to work. Logs will confirm the HCL Notes Client version, if other options are more difficult.

### Proxy Servers

NotesHTTPRequest uses libcurl to make HTTP requests. If access to the internet is via a proxy server, OS environment variables need setting. There is an [HCL technote](https://support.hcltechsw.com/csm?id=kb_article&sys_id=0157fae51b418c1483cb86e9cd4bcb96) covering this.

### "No Signature" Warning When Creating Calendar Entry

This appears to be caused by DXL export / import of Calendar Entry form during DOMI installation, if the ID running the install is different to the signer of the mail template. Re-signing the mail template should resolve the problem.

### Notes Crashes After Get Sametime Token

This can be caused by libcurl being unable to retrieve the SSL certificates for the Sametime server. To verify, add the Notes.ini variable `Debug_NotesHTTPRequest=1`, restart Notes Client, try again and check the console.log. You will get a line like this:
```
[1AB4:0002-1AB8] SSL certificate problem: unable to get local issuer certificate
```

This has not happened on all environments, so there may be something in Sametime configuration that does not allow all required certificates to be pulled automatically. However, we cannot identify what the best practice resolution should be. Adding the relevant certificate to the Notes Client will solve the problem. [This technote](https://support.hcltechsw.com/csm?id=kb_article&sysparm_article=KB0069539&sys_kb_id=c15650e01b333f8883cb86e9cd4bcb9e) talks about the Domino server for Notes HTTPRequest calls server-side, but the same solution should resolve the problem when applied to a Notes Client. If you are in a multi-user environment, [this technote](https://support.hcltechsw.com/csm?id=kb_article&sysparm_article=KB0084642&sys_kb_id=063361c6db38701055f38d6d139619ef) may be the required solution.

### Getting an OAuth Token - Notes Embedded Browser

*Get OAuth Token* action button launches a browser. The Notes Client embedded browser has not been tested so if that preference is enabled, users may have to work around that.

### Get OAuth Token - Page Not Available

Retrieving an OAuth token opens the web application. First verify the URL is correct. The URL will be built up of:  
- `g_DOMI_ROOTURL_TOKEN` constant. This should be in the format `https://integration-auth-token.hcltechsw.com/`.
- Endpoint for service, e.g. `dominoZoom/index.html`.

The server can be accessed by entering the root URL into a browser. If this is not returning a valid page, there may be a problem with the service.

###  Validate OAuth Token Failure

Refresh tokens are only valid for a fixed period of time. If the token has expired, users may need to use the *Get OAuth Token* action button.

Validating an OAuth token again accesses this same server. If an error occurred performing the OAuth dance, this will be displayed in a message box and written to the logs. You can check whether there is a problem with service available by entering the root URL into a browser.

### Repeating Meetings

There is no specific support for repeating meetings. The same meeting will be used for all instances. Updating meetings should be used with caution:  
- Where the meeting service supports date/times, updating the meeting from one instance will override it with the date/times from that instance.  
- Microsoft Teams deletes and recreates the online meeting, so the link on all other instances of the meeting will no longer work.
- Microsoft Teams does not hold dates and times, so there is nothing relevant to update anyway.

### GoToMeeting Error - Free Account

GoToMeeting requires a paid account. Free accounts do not have access to run GoToMeeting REST APIs and an HTTP status code 500 will be returned. The message lpgged will say a precondition has failed and the 412 HTTP status code is being treated as an error 500.

### Creating / Update / Deleting Meetings - Generic Troubleshooting

If an error occurs when trying to create / update / delete a meeting, the messagebox that appears will include the HTTP status code returned and, if available, an error message. HTTP status code 500 indicates some unexpected problem with the service you're trying to use. It may be resolved by amending the subject and dates, if there is some unanticipated issue with the content being passed. Alternatively, it may be some specific problem with the service or user account.

For other errors, cross-reference with [the Mozilla website](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) for help on diagnosing the problem.

If the messagebox has already been cleared and providing logging has not been disabled, logs will be written to the local log.nsf.

By default only errors are logged. To increase logging, see [Notes.ini Variables for Logging]({{'/modules_admin_guide/notes_ini' | relative_url}}). You may need to restart the Notes Client for the updated notes.ini variable to be picked up.

The notes.ini setting `Debug_NotesHTTPRequest=1` is a pre-existing options to give full output of cURL integration from the NotesHTTPRequest class. If you add this, you can see all steps in the cURL process, including request and response headers and body, in the console.log file (<NotesData>\IBM_TECHNICAL_SUPPORT). **Note** this will also log the access and refresh tokens.

### Validating HTTP Issues

A generic agent called "99. Admin\04. Test HTTP Request Response Code" has been added to the installation database. This makes GET, POST, PUT, PATCH and DELETE calls to https://httpstat.us. This is a service which echoes the HTTP status passed. For example, if you enter the desired response code 418, all five HTTP methods should return "HTTP/1.1 418 I'm a teapot". If not, something is wrong with the integration with libcurl from the LotusScript NotesHttpRequest classes. 

If you add the notes.ini setting `Debug_NotesHTTPRequest=1`, you can see all steps in the cURL process, including request and response headers and body, in the console.log file (<NotesData>\IBM_TECHNICAL_SUPPORT).

If you do not get the same response code for all five HTTP methods, the Notes Client does not have the fix that was added in Notes 11.0.1 FP3 and Notes 12.

### Delegated Access

See the [Delegated Access]({{'/modules_user_guide/delegated_access' | relative_url}}) page.

### Team Mailboxes

See the [Team Mailbox]({{'/modules_user_guide/team_mailboxes' | relative_url}}) page.

### Updating / Deleting Meetings - Multiple Accounts

If multiple users access the same mail database, it is possible different accounts are used to create and update / delete a meeting. Obviously this will generate an error. This will probably return a HTTP status code 404.

### Updating / Deleting Meetings - Meeting Deleted Outside Calendar Entry

Similarly, some meeting providers allow you to delete the online meeting via their own website or desktop application. This cannot update the Calendar Entry, which will still have the meeting details. Obviously attempts to update / delete the meeting in the Calendar Entry will generate an error, again expected to be a HTTP status code 404.

A Domino Developer can review the hide-when formula of the buttons to update the Calendar Entry to allow a new online meeting to be added. If no new online meeting is needed, a Domino Developer can review the code to clear the relevant fields in the Calendar Entry.

### Sametime Meetings - Duplicate Meeting Name

Sametime Meetings uses a meeting name to create a meeting. The code attempts to ensure this is unique. However, if a meeting already exists for the same name, a HTTP status code 409 will be returned.

### Creating / Updating Meetings - 400 / 403 HTTP Status Code

When creating / updating meetings, validation is performed on subjects are not blank and to ensure start and end date/time are not in the past. For Sametime Meetings, the subject is also sanitised for all known unsupported characters.

There may be other invalid content not anticipated. For Sametime, this may results in a HTTP status code 403 instead of HTTP status code 400. For other services, this should return a status code 400.