---
layout: default
title: HCL Domino Administrators
nav_order: 2
has_children: true
last_modified_date: 2021.03.22
---

## Documentation for Domino Administrators

The Domino Online Meeting Integration installation database copies specific design elements into your mail template and adds action buttons to the end of your Calendar entry form. The process is designed as a "light touch" integration that should work even if you have made design changes to the mail template.

Installation of DOMI for Domino Administrators requires:  
- Copying the DOMI Installation NSF on a Domino server.  
- Updating URLs in Script Libraries.  
- Signing the NSF.  
- Using the *Install DOMI* action button to copy the design elements to the appropriate mail template.

When deployed to mail databases, end users will need to be on HCL Notes 11.0.1 FP3 or higher. They will be warned if they are not using the correct version. There are no restrictions on server version for using the online meeting integration in calendars in HCL Notes Client.
{:.alert .alert-warning}

### 1. Installing DOMI Installation Database

1. Download the Domino Online Meeting Integration NSF and copy it to the Domino server where you wish to sign and deploy it.  
2. Open the database. You will be brought to a landing page which walks you through the steps.  

### 2. Pre-Requisite Configuration

1. If you are using the new Sametime Meetings, you will need to update the base URL for the server. To do this:  
    a) Open this application in Domino Designer.  
    b) Go to *Code > Script Libraries* and open **domiConstantsBE**.  
    c) In Declarations, set the value for the `g_DOMI_ROOTURL_SAMETIMEMEETINGS` constant to your Sametime server, e.g. "https://demomeetings.hcltechsw.com"  
    ![Script Library]({{'/assets/images/ScriptLibrary.png' | relative_url}})
2. If you have decided to host your own version of the OAuth application that communicates to Zoom, Teams, GoToMeeting and Webex. You will also need to update the `g_DOMI_ROOTURL_TOKEN` constant in the same place. This should just be the hostname of the server, in the format "https://demoDOMI.hcltechsw.com".

### 3. Signing The Database

Sign the database with your organisation's signing ID or other appropriate Notes ID for signing databases prior to deployment to production.

### 4. Installation

Installation process runs quickest using a local replica of DOMI Installation database against a local replica of the mail template.
{:.alert .alert-info}

In the DOMI Installation database's landing page, click the *Install DOMI* action button. You will be prompted to select the mail template into which to deploy DOMI. Within seconds the process will complete and the design elements will have been copied into the mail template. The standard Domino design task can be used to propagate the changes to relevant users.

### Uninstall

If you need to uninstall DOMI, use the *Uninstall DOMI* action button. You will be prompted to select the mail template into which to deploy DOMI. Within seconds the process will complete and the design elements will have been removed from the mail template.

### Restricting Meeting Provider Options

By default DOMI supports Zoom, Webex, GoToMeeting and Microsoft Teams. If you wish to restrict these options, you will need to edit the Online Meeting Credentials form in the DOMI installation database before running the install. The list of service providers available to end users is held in the `serviceList` field. Removing an option will prevent users creating an Online Meeting Credentials profile for that service provider. Sametime is automatically removed if you have not set a Sametime Meetings server.