---
layout: default
title: How It Works
parent: DOMI Installation Code
nav_order: 1
has_children: false
last_modified_date: 2021.03.23
---

## Introduction
This section describes the overall design architecture of the Domino Online Meeting Integration (DOMI) Installation application, as well as some of the maintenance tools provided.

## Design Considerations
The Notes/Domino Mail template is arguably one of the most mission critical, widely used, and complex Notes/Domino templates in use today. As such DOMI was designed from the beginning to be as "minimally invasive" to the Notes/Domino Mail Template as much as possible. This means that we didn't mind adding net-new design elements to the template, but we wanted to avoid editing existing elements as much as possible. We accomplished this by doing two things in particular:

1. We wrote our code to be completely self-contained, primarily within Script Libraries and Agents
2. We "surface" the DOMI functionality through a set of Actions within the Calendar Entry/Appointment Form; these Actions are contained within a Subform that's added to the Calendar Entry Form during installation

This means the DOMI Installation app needed to do the following:

* Provide a way to store, identify, and copy all of the net-new DOMI design elements into the target Notes/Domino Mail template
* Provide a way to add the new DOMI Subform to the existing Calendar Entry Form
* Provide a way to "uninstall" itself by removing all DOMI design elements and remove the Subform from the Calendar Entry

How these tasks were accomplished is summarized below.

## Overview
The installation system used by DOMI is designed to be a self-contained, reusable delivery mechanism for installing new features into existing Notes/Domino databases and templates; DOMI just happens to be the first feature using this system. Each Installation database should contain one and only one featureset, as this makes the focus more targeted and concise. The installation database contains both its own design elements used by the system itself, as well as the design elements that are a part of the feature it will install - DOMI in this case.

Then how does the installation system know the difference between the feature's elements and its own? This is done through the use of Design Element Profiles.

## Design Element Profile (Form)
A Design Element Profile represents a design element contained in the installation database. There is one Design Element Profile for each design element. The Design Element Profile contains some basic metadata about the corresponding design element - for instance, here's the Design Element Profile for the profile form itself...
![Design Element Profile]({{'/assets/images/installation/DEProfile.png' | relative_url}})

The Design Element Profile contains useful metadata about the corresponding design element, such as:
* __Element Name__ - the name of the design element [Design Element Profile]
* __Element Type__ - the design element type, such as Script Library, Form, etc. [FORM]
* __Element Alias(es)__ - any aliases assigned to the design element [desElement]
* __Element Modified Date__ - the date and time the design element was last modified [03/18/2021 03:20:37 PM]
* __Element UNID__ - the Document UNique ID (UNID) of the design element [3C3DA40D37A4EB6B8525867600470097]
* __Featureset?__ - this flag indicates if the design element is a part of the featureset (Yes) or not (No) [No]
* __Comments__ - any comments the developer or administrator may wish to add about the element
  
Design Element Profiles are used by the install code to identify and more easily access the elements that are a part of the featureset. You can create individual Design Element Profiles, however it's much easier to just delete the existing ones and use the _99. Admin/01. Generate Design Element Profiles_ action found in the DOMI Installation database.

### Generating Design Element Profiles
There is an agent available in the DOMI Installation db that will automatically generate a Design Element Profile for each design element found in the database. This agent can be found under _Actions/99. Admin/01. Generate Design Element Profiles_.
![Generate Design Element Profiles Action]({{'/assets/images/installation/GenerateDEProfilesAction.png' | relative_url}})

It is recommended that you delete all of the the old Design Element Profile documents before you run this agent.

Once you generate all of your Design Element Profile documents, you need to mark which elements are a part of your featureset. You can do this from within one of the two Design Element Profile views, but it's recommended that you use the view _02. Design Element Profiles/02. All by Feature Flag_

Since the DOMI Installation database is deisgned to open to a landing page, getting to the underlying views may seem difficult for some. You can do this easily by selecting _View/Go To..._ from the Menu bar. Alternatively you can right-click on the database in the Workspace and select _Application/Go To..._ 
{: .alert .alert-info}

Once you're in the view, simply select the design elements that are a part of your featureset, then click the _Mark Selected as Feature?_ view action and click Yes or No.
![Mark Selected As Feature Action]({{'/assets/images/installation/DOMIMarkFeature.png' | relative_url}})

All elemements that are marked as a part of the featureset will be injected into the target mail database/template by the installation agent.

## DOMI Feature Installation
The installation of DOMI is accomplished through a single agent that Administrators normally access through the DOMI landing page in the DOMI Installation database. The agent is surfaced to the Administrator through the _Install DOMI_ Action.
![Install DOMI Action]({{'/assets/images/installation/DOMIInstallAction.png' | relative_url}})

As mentioned earlier, the two main goals of the installation portion of DOMI are to:

* Provide a way to store, identify, and copy all of the net-new DOMI design elements into the target Notes/Domino Mail template
* Provide a way to add the new DOMI Subform to the existing Calendar Entry Form

(There is a third goal, uninstallation - but we'll cover that later)

The DOMI Installation database provides us a place to store the DOMI feature design elements. The Design Element Profiles provide us a way to identify which design elements in the database are a part of the DOMI featureset. The installation agent handles the remaining goals - copy all of the DOMI design elements into the target mail template, and add the DOMI Subform to the existing Calendar Entry form. This agent is called _01. Install DOMI in Target Mail Db_. 

But how exactly does that agent work? Let's take a closer look.

### Background
During development of the DOMI Installation system, we discovered an interesting "quirk" in Notes/Domino. During the process of copying the feature design elements into the target mail template, we first check to see if the element already exists - if it does, we delete it so we can copy the new one in. We found out that if you do this, the design element is "cached" within the db design; and as such when you try to copy the new element into the database after deleting the old one, you get an error basically informing you that someone else edited the element while you were working on it, and it prevents the copy from taking place. Incidentally, this only seems to happen with code-based design elements - namely Agents and Script Libraries. 

To get around this, we took a novel approach. The design elements are indexed by their IDs - namely their UNIDs and NoteIDs. So, to get around this limitation, we have code that changes the UNID of all of the Script Libraries and Agents in the featureset BEFORE the installation takes place. And since we're updating the UNIDs of the design elements, we need to update the corresponding Design Element Profiles as well.

Now that you understand this quirk and how we work around it, let's take a look at how the agent actually works.

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