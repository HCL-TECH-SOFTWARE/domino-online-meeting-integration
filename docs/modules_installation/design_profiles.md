---
layout: default
title: Design Element Profiles
parent: DOMI Installation Code
nav_order: 2
has_children: false
last_modified_date: 2021.03.30
---

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
