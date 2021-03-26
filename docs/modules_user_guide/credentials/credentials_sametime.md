---
layout: default
title: Sametime
parent: Online Meeting Credentials
grand_parent: HCL Notes Users
nav_order: 3
has_children: false
last_modified_date: 2021.03.22
---

## Sametime

Sametime is only available as an option if your Domino Administrator has specified a Sametime Meetings server.
{: .alert .alert-warning}

1. Go to the *Online Meeting Credentials* view.  
<img src="{{'/assets/images/user_guide/credentials_view.png' | relative_url}}" style="height:200px" alt="Online Meeting Credentials View" />
2. click the *New Meeting Credentials* button.  
<img src="{{'/assets/images/user_guide/new_meeting_credentials.png' | relative_url}}" style="height:100px" alt="New Online Meeting Credentials" />
3. Select Sametime.  
4. Click the *Get Sametime Token* button.  
<img src="{{'/assets/images/user_guide/get_sametime_token.png' | relative_url}}" style="height:40px" alt="Get Sametime Token" />
5. In the dialog enter your Sametime password. **NOTE** Your Sametime password needs to be plain text to pass to the code that performs the authentication, so it will be visible this one time. But it is not stored in the online meeting credentials document.  
<img src="{{'/assets/images/user_guide/sametime_login.png' | relative_url}}" style="height:150px" alt="Sametime Login" />
6. If successful, the refresh token will be populated. You are now ready to create ad hoc meetings for this provider and can save and close the Online Meeting Credentials document. 