---
layout: default
title: Zoom, Teams, Webex, GoToMeeting
parent: Online Meeting Credentials
grand_parent: HCL Notes Users
nav_order: 1
has_children: false
last_modified_date: 2021.06.25
---

## Zoom, Microsoft Teams, Webex and GoToMeeting

The options available may vary if your Domino Administrator has restricted the options available.
{: .alert .alert-info}

1. Go to the *Online Meeting Credentials* view.  
<img src="{{'/assets/images/user_guide/credentials_view.png' | relative_url}}" style="height:200px" alt="Online Meeting Credentials View" />
2. click the *New Meeting Credentials* button.  
<img src="{{'/assets/images/user_guide/new_meeting_credentials.png' | relative_url}}" style="height:100px" alt="New Online Meeting Credentials" />
3. Select the service.  
4. Click the *Get OAuth Token* button.  
<img src="{{'/assets/images/user_guide/get_oauth_token.png' | relative_url}}" style="height:200px" alt="Get OAuth Token" />
5. You will be taken to a browser to authenticate with the relevant meeting service. After authenticating, you will be redirected to a page with a refresh token. The format of the token will vary depending on the meeting provider. Click *Copy*.  
<img src="{{'/assets/images/user_guide/copy_token.png' | relative_url}}" style="height:300px" alt="Copy Token" />
6. Return to the Notes Client and click the *Paste* button. Using the *Paste* button automatically saves and validates the token. If you use paste into the field manually, you will need to save and close to trigger the token validation.  
<img src="{{'/assets/images/user_guide/paste_token.png' | relative_url}}" style="height:250px" alt="Paste Token" />
7. The system will automatically validate and retrieve an access token. If successful, you will receive the following success message. You are now ready to create ad hoc meetings for this provider and can save and close the Online Meeting Credentials document.  
<img src="{{'/assets/images/user_guide/success_token.png' | relative_url}}" style="height:100px" alt="Success Token" />