---
layout: default
title: Creating Online Meetings
parent: HCL Notes Users
nav_order: 3
has_children: false
last_modified_date: 2021.03.23
---

## Creating Online Meetings from Calendar Entries

The *Schedule Online Meeting...* action button will only be available if you have configured online meeting credentials for one or more services. The actions underneath will depend on which online meeting credentials were configured when the calendar entry was created.
{: .alert .alert-info}

1. Create a Calendar Entry and complete as normal. Where services support that functionality, Subject, dates/times and agenda will be sent to the meeting provider for the ad hoc meeting. So it is recommended that you complete those details before scheduling the online meeting.  
2. Click *Schedule Online Meeting...* and select the desired meeting provider.  
<img src="{{'/assets/images/user_guide/schedule_online_meeting.png' | relative_url}}" style="height:250px" alt="Schedule Online Meeting" />
3. If the access and refresh tokens are valid, you will be notified that the meeting has been generated, the details added to the Location field.  
<img src="{{'/assets/images/user_guide/online_meeting_created.png' | relative_url}}" style="height:250px" alt="Online Meeting Created" />
4. If the refresh token is no longer valid, you will be prompted to request a new token. To complete adding an ad hoc meeting for this provider, click "Yes".  
<img src="{{'/assets/images/user_guide/request_new_token.png' | relative_url}}" style="height:120px" alt="Reqest New Token" />
5. You will be taken to the Online Meeting Credentials form. Click the *Get OAuth Token* button (or *Get Sametime Token* button for Sametime) and continue through authentication. This is the same process outlined in the [Online Meeting Credentials]({{'/modules_user_guide/credentials/index.html' | relative_url}}) section.  
6. Once complete, save and close the Online Meeting Credentials.
7. Return to the calendar entry and click *Schedule Online Meeting...* and select the desired meeting provider. The online meeting should now be created successfully.

<div class="panel panel-info">
    <div class="panel-heading">Meeting Provider Differences</div>
    <div class="panel-body">
Note the following differences in meeting providers:<ul><li>Microsoft teams does not provide an area to see scheduled meetings except in calendar entries.</li><li>Webex will send a calendar entry with your host details.</li><li>Sametime Meetings applications do not display the date and time of the meeting.</li><li>Zoom also receives the agenda.</li><li>Zoom and Webex create a password for the meeting.</li><li>Webex create a SIP link for video phones and a phone / video password.</li>
    </div>
</div>
