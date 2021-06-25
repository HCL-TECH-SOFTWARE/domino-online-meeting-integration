---
layout: default
title: HCL Notes &amp; Domino Webex Meetings Integration
parent: Overview
nav_order: 5
has_children: false
last_modified_date: 2021.06.25
---

## HCL Notes & Domino Webex Meetings Integration

HCL Notes and Domino integration with Webex Meetings allows you to add an ad hoc meeting to your calendar entry. After setting up your online meeting credentials, at a single click you can:
- create a Webex meeting and add the link to your calendar entry
- update the Webex meeting if key details on the calendar entry have changed
- delete the Webex meeting if the calendar entry is no longer required

### 1. Pre-Requisites

It is assumed you are already a Notes and Domino customer and familiar with Domino administration. There is no additional subscription required. The functionality only requires a minor modification to your mail template, which will need to be done by your Domino Administrator. The [documentation](../../modules_admin_guide/overview) covers all the steps required to do this.

You will need:
- a paid [Webex Meetings Account](https://www.webex.com/). This only works with paid Webex Meetings Accounts. Free Webex Meeting Accounts unfortunately do not qualify.
- an HCL Notes Client including Domino Administrator and Domino Designer.
- the <a href="{{ site.gh_edit_repository }}/tree/main/release" target="_new">DOMI Installation database</a>.

### 2. Installation Process

Full details are covered in the [documentation](../../modules_admin_guide/overview). You will need to:
1. Download the <a href="{{ site.gh_edit_repository }}/tree/main/release" target="_new">DOMI Installation database</a>. Installation will run quickest from a local database.
2. Open the database. You will be brought to a landing page which walks you through the steps.
3. If you have decided to host your own version of the OAuth application that communicates to Webex Meetings. You will also need to update the `g_DOMI_ROOTURL_TOKEN` constant in the same place. This should just be the hostname of the server, in the format "https://demoDOMI.hcltechsw.com".

### 3. Signing The Database

Sign the database with your organisation's signing ID or other appropriate Notes ID for signing databases prior to deployment to production.

### 4. Installation

Installation process runs quickest using a local replica of DOMI Installation database against a local replica of the mail template.
{:.alert .alert-info}

In the DOMI Installation database's landing page, click the *Install DOMI* action button. You will be prompted to select the mail template into which to deploy DOMI. Within seconds the process will complete and the design elements will have been copied into the mail template. The standard Domino design task can be used to propagate the changes to relevant users.

If the ID running the install is different to the signer of the mail template, it is recommended to re-sign the mail template after installing DOMI.
{: .alert .alert-warning}

### 5. Setting Up Online Meeting Credentials

1. Go to the *Online Meeting Credentials* view.  
<img src="{{'/assets/images/user_guide/credentials_view.png' | relative_url}}" style="height:200px" alt="Online Meeting Credentials View" />
2. click the *New Meeting Credentials* button.  
3. Click the *Get OAuth Token* button.  
4. You will be taken to a browser to authenticate with the relevant meeting service. After authenticating, you will be redirected to a page with a refresh token. The format of the token will vary depending on the meeting provider. Click *Copy*.  
<img src="{{'/assets/images/user_guide/copy_token_webex.png' | relative_url}}" style="height:300px" alt="Copy Token" />
5. Return to the Notes Client and click the *Paste* button. Using the *Paste* button automatically saves and validates the token. If you use paste into the field manually, you will need to save and close to trigger the token validation. <br/>
<img src="{{'/assets/images/user_guide/paste_token_webex.png' | relative_url}}" style="height:250px" alt="Paste Token" />
6. The system will automatically validate and retrieve an access token. If successful, you will receive the following success message. You are now ready to create ad hoc meetings for this provider and can save and close the Online Meeting Credentials document.  
<img src="{{'/assets/images/user_guide/success_token.png' | relative_url}}" style="height:100px" alt="Success Token" />

## 6. Creating Online Meetings from Calendar Entries

The *Schedule Online Meeting...* action button will only be available if you have configured online meeting credentials for one or more services. The actions underneath will depend on which online meeting credentials were configured when the calendar entry was created.
{: .alert .alert-info}

1. Create a Calendar Entry and complete as normal. Where services support that functionality, Subject, dates/times and agenda will be sent to the meeting provider for the ad hoc meeting. So it is recommended that you complete those details before scheduling the online meeting.  
2. Click *Schedule Online Meeting...* and select the desired meeting provider.  
<img src="{{'/assets/images/user_guide/schedule_online_meeting.png' | relative_url}}" style="height:250px" alt="Schedule Online Meeting" />
1. If the access and refresh tokens are valid, you will be notified that the meeting has been generated, the details added to the Location field. 
2. If the refresh token is no longer valid, you will be prompted to request a new token. To complete adding an ad hoc meeting for this provider, click "Yes".  
3. You will be taken to the Online Meeting Credentials form. Click the *Get OAuth Token* button (or *Get Sametime Token* button for Sametime) and continue through authentication. This is the same process outlined in the [Online Meeting Credentials]({{'/modules_user_guide/credentials/index.html' | relative_url}}) section.  
4. Once complete, save and close the Online Meeting Credentials.
5. Return to the calendar entry and click *Schedule Online Meeting...* and select the desired meeting provider. The online meeting should now be created successfully.