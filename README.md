# Domino Online Meeting Integration 

Domino Online Meeting Integration is an extension to the Domino Mail template allowing create, update and delete of ad hoc online meetings from HCL Notes client calendar entries. The following meeting providers are supported out-of-the-box:  
- Zoom
- Microsoft Teams
- Webex
- GoToMeeting
- HCL Sametime

**Zoom requires approval so is not yet working**

GoToMeeting requires a paid account. Free accounts do not have access to run GoToMeeting REST APIs and an HTTP status code 412 will be returned.

However, it has been built with extensibility in mind. So it is possible to add support for additional meeting providers by:  
- Making the relevant changes in the Notes mail template, or the installation database.  
- Adding a mechanism to get the refresh token, for which the web application in here can be used.

## Documentation

See [GitHub Pages site](https://opensource.hcltechsw.com/domino-online-meeting-integration).
