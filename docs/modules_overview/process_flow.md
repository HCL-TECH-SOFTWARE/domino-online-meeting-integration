---
layout: default
title: Process Flow
parent: Overview
nav_order: 2
has_children: false
last_modified_date: 2021.03.02
---

## Simple Flow

Once DOMI has been deployed to your mail template by the Domino Administrator, the process flow between users, the Notes Client, the hosted web application and the meeting service is fairly straightforward. The flow below is simplified, omitting two aspects:
- The web application (either hosted by HCL or deployed locally), which provides the OAuth dance with meeting services.
- The requirement for refreshing the authorization token, if it has expired by the time the user creates a calendar entry.

![Process Flow]({{'/assets/images/DOMI_Flow.png' | relative_url}})

The steps are:
1. Create an Online Meeting Credentials profile, click the "Get OAuth Token" button. This launches a browser which redirects to the relevant meeting service's OAuth login, prompting the user to allow permission to run on their behalf. This redirects back to a web page that displays the Refresh token.
2. Click the "Copy" button in the browser, go back to the Online Meeting Credentials profile and use the "Paste" button to enter the token. This function validates the token against the meeting service, retrieving and storing the Authorization token and Refresh token, and confirming success to the user.
3. Create a calendar entry and click the "Create Online Meeting" button. This will use the tokens to create a meeting on the relevant service and insert the details into the calendar entry.

## Detailed Flow

The web application is just a proxy for the OAuth process, displaying a token for the user to copy and paste, and proxying communication between Notes Client and the meeting service for updating Authorization and Refresh tokens. Nothing is stored in the web application, and it is not used for session-based communication, just one-off interactions. To see how this fits into the overall process, here is the detailed communication flow.

![Detailed Process Flow]({{'/assets/images/DOMI_Detailed_Flow.png' | relative_url}})

The steps are:
1. Create an Online Meeting Credentials profile, click the "Get OAuth Token" button. This launches a browser which redirects to the web application and onto relevant meeting service's OAuth login, prompting the user to allow permission to run on their behalf. This returns a short-lived code to the web application.
2. The web app exchanges the short-lived code for an Authorization token and Refresh token, and redirects to a web page that displays the Refresh token. The user clicks the "Copy" button in the browser, go back to the Online Meeting Credentials profile and use the "Paste" button to enter the token. This function validates the token against the meeting service, retrieving and storing the Authorization token and Refresh token, and confirming success to the user.
3. This function sends the refresh token to the web application, which calls the meeting service to refresh the token. The meeting services sends back the Authorization token and Refresh token, which are passed back to HCL Notes. HCL Notes stores them and confirms success to the user. At this point, the profile is set up for future use. As long as the user is creating online meetings regularly, the tokens should continue to work without needing to repeat this step.
4.  Create a calendar entry and click the "Create Online Meeting" button. This either:  
  a. Uses the token to create a meeting on the relevant service and insert the details into the calendar entry.  
  b. Exchanges the refresh token for valid Authorization and Refresh tokens, updates the Online Meeting Credentials profile, and uses the tokens to create a meeting on the relevant service and insert the details into the calendar entry.  
  c. If the token has expired HCL Notes prompts the user asking if they wish to refresh the token. If so, the relevant Online Meeting Credentials profile is opened.  
    1. User clicks the "Get OAuth Token" button. This launches a browser which redirects to the web application and onto relevant meeting service's OAuth login, prompting the user to allow permission to run on their behalf. This returns a short-lived code to the web application.  
    2. The web app exchanges the short-lived code for an Authorization token and Refresh token, and redirects to a web page that displays the Refresh token.   
    3. The user clicks the "Copy" button in the browser, go back to the Online Meeting Credentials profile and use the "Paste" button to enter the token.  
    4. The token is validated to exchange the refresh token for valid Authorization and Refresh tokens, via the web application.  
    5. The token is used to create a meeting on the relevant service and insert the details into the calendar entry.  