---
layout: default
title: DOMI Install Agent Details
parent: DOMI Installation Code
nav_order: 3
has_children: false
last_modified_date: 2021.03.30
---

## DOMI Install Agent Details
The DOMI Install Agent (_01. Install DOMI in Target Mail Db_) can really be broken down into three major steps for each design element in the featureset:

* If the design element is a Script Library or Agent, replace the UNID (and of course update the corresponding Design Element Profile with the new UNID)
* Search the target Mail template for an existing version of the design element; if found, delete it
* Copy the design element into the target mail template
  
Once the featureset design elements have been copied, then the final step is to add the DOMI subform into the Calendar Entry Form. This is done using DXL.

Each of the steps above has been encapsulated into a function to make it easier for troubleshooting and overall maintenance. Additionally, a couple of calls are used in multiple places, so those functions are stored in the _dxlUtilsBE_ Script Library.

### Function: Replace Design Element UNID for Script Libraries and Agents
This function is called `replaceDEUNID(deDoc as NotesDocument)`, where `deDoc` is a handle to the Design Element Profile of the current design element. It performs the following steps:

* Gets a handle to the existing design element using the UNID stored in the provided Design Element Profile
* Generates a new UNID
* Replaces the UNID of the design element 
* Deletes the old design element __NOTE:__ When you replace the UNID of a design element, it creates a NEW design element using that UNID; however the old design element is still in the database. Therefore the old design element must be deleted manually.
* Updates the Design Element Profile with the new UNID
* Returns the new UNID as a String

### Function: Find & Remove the Old Design Element From the Mail Template (if applicable)
The function used for this step is called `getElementHandle(mailDb as NotesDatabase, elementType as String, elementName as String)`, where

* `mailDb` is a handle to the target Mail template
* `elementType` is the type of design element we're looking for (e.g. Script Library, Form, View, Agent)
* `elementName` is the name or alias of the design element

`getElementHandle()` uses `NotesNoteCollection` to get a collection of all of the design elements of the given type. Keep in mind that `NotesNoteCollection` does __NOT__ contain a collection of `NotesDocument` objects; instead it contains a list of all of the _NoteIDs_ of those type of design elements found in the given database/template. This means that we have to:

* Get a collection of all of the NoteIDs
* Loop through them using `getDocumentByID()` then check for our element by name or alias
* Once our design element is found, return it as a `NotesDocument` object

If a design element is returned from `getElementHandle()` it is deleted from the target Mail template.

Once the old design element has been removed from the target Mail template, the new design element is copied into that template.

Then our final step is to update the _Calendar Entry_ form.

### Function: Add the DOMI Subform to the Calendar Entry Form
The function used for this step is called `addDOMISubformToAppointment(mailDb as NotesDatabase)`, where `mailDb` is a handle to the target Mail template. Since this function is only used by this agent, it is located in the agent itself. This function performs the following setps:

* Check to see if a backup of the original, unchanged Calendar Entry Form already exists using the `getElementHandle()` function previously covered. If it's found, then it is deleted.
* Get the DXL of the Calendar Entry Form using the function `getElementDXL()`, and put it into a `NotesStream` object to make it easier to work with
* Search the DXL stream to see if the DOMI subform DXL tag (`<subformref name='DOMI_onlineMeetingActions | onlineMeetingActions'/>`) already exists
* If the tag does not exist, add it to the bottom of the form, right above the `</richtext></body>` close tags
* Import the updated DXL stream back into the Mail template using the `importDXLStream()` function. This function will create a backup of the design element and name it _(BACKUP-DOMI-Appointment)_ automatically
* Return `True` if successful

And that's it! The Mail template is now updated with the DOMI feature.