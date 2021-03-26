---
layout: default
title: Extending DOMI (HCL Notes)
parent: Extending DOMI
nav_order: 2
has_children: false
last_modified_date: 2021.03.25
---

## Extending DOMI in HCL Notes

If you wish to allow users to use the in-built functionality to retrieve tokens, you will need to deploy a version of the [DOMI OAuth Web Application]({{'/modules_webapp/overview' | relative_url}}) after making the relevant changes for the new meeting service, as outlined in [Extending the Web App]({{'/modules_webapp/development/extending' | relative_url}}).
{: .alert .alert-info}

To extend DOMI to support another OAuth meeting service you will need to make changes. The recommendation is to update the DOMI installation database and test by deploying to your own mail database, before deploying to the mail template after it is fully tested. You should make a note of the changes so that they can be repeated for future releases.

Test API calls in Postman to ensure you know what a correct API call should look like.

As well as debugging the LotusScript calls, you can also add the notes.ini setting `DEBUG_NotesHTTPRequest=1` to output full details of the NotesHTTPRequest class. If you add this, you can see all steps in the cURL process, including request and response headers and body, in the console.log file (<NotesData>\IBM_TECHNICAL_SUPPORT). **Note** this will also log the access and refresh tokens.

### 1. domiConstantsBE Script Library Declarations

In the Declarations, constants are defined for each meeting service, e.g. `DOMI_SERVICE_GTM`. Add another constant for your new meeting service using the same format, and give it a short memorable value.

### 2. domiUtilsBE getDOMIrootURL()

This function builds the URL for retrieving, refreshing and - if the meeting service support it - revoking access and refresh tokens. You should add to the `Select Case` statement a check for the meeting service constant set up in step 1. The result should be the URL that is the root of all calls on the DOMI OAuth web application you have set up.

### 3. domiUtilsBE Script Library getDOMIendpointURL()

Constants here give the base URLs for creating, updating and deleting meetings. For update and delete, the expectation is that the meeting ID should be appended as a path parameter at the end of the URL.

The `Select Case` statement further down in this function will need amending for the new meeting service. Create a `Select Case` statement for the relevant operations, returning the built URL.

### 4. domiUtilsBE Script Library getDOMIserviceLabel()

Add a constant for the relevant service pair, the label and the alias. Add to the `Select case` statement to return the relevant label.

### 5. domiUtilsBE Script Library isDOMIserviceSupported()

Add the meeting service constant defined in step 1 to the `Select Case` statement.

### 6. Agents

Add agents for **(DOMI_scheduleXXXXMeeting)**, **(DOMI_updateXXXXMeeting)** and **(DOMI_deleteXXXXMeeting)**. Copy the format of existing agents, adding to `nodesToCopy` entries for the node to extract from the REST API response and the field to apply it to. The fields available are in **domiIntegrator** `getDOMIItemNames()`.

### 7. domiIntegrator Script Library getMeetingBodyArgs()

Add to the `Select Case` statement to build the body for the relevant meeting service. The first step is to convert `ndtStart` and `ndtEnd` to the appropriate date/time format required by the REST service:  
- `notesDateTimeToJsonOffsetZulu()` builds a format yyyy-MM-ddThh:mm:ss specifically return UTC date/time.  
- `notesDateTimeToJsonOffset()` builds a format yyyy-MM-ddThh:mm:ss with a +/- offset of format "+0100" or Z for UTC.  
- `notesDateTimeToJsonOffsetColon` does the same but adds a colon between hours and minutes of the offset, e.g. "+01:00".

Then the JSON object for the payload is constructed as a VariantList. The VariantList has a `json` property which converts the list to a JSON object. LotusScript booleans will be properly converted. Dates and times will just be outputted as `Cstr()` values. Content will be properly URL encoded. If you need to nest JSON objects, the VariantList can contaiun another VariantList.

### 8. domiIntegrator Script Library DominoOnlineMeetingintegrator.pushMeeting()

Add to the `Select Case` statement to build the content for the Location field.

### 9. domiIntegrator Script Library DominoOnlineMeetingIntegrator.updateMeeting()

Add to the `Select Case` statement if the HTTP verb for updating a meeting is not `PATCH` or the update REST API call returns no content. If no content is returned, you need to call `replaceMultipleItems(context, existingMeetingValues)` because the standard REST service call will have cleared the fields.

### 10. DOMI_onlineMeetingActions Subform

Add a sub-action to the **Schedule Online Meeting...** action for the new meeting service. Copy the code from one of the other actions and change it to call **(DOMI_scheduleXXXXMeeting)**.

Update the formulas for **Update Meeting** and **Delete Meeting** to call the relevant agent. The meetingService field value will be the alias you chose.

### 11. Online Meeting Credentials Form

Update the **serviceList** field with the new meeting service.