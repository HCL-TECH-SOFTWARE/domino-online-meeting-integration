---
layout: default
title: DOMI Installation Code
nav_order: 5
has_children: true
last_modified_date: 2021.03.22
---

## Documentation for DOMI Installation Code

The Domino Online Meeting Integration installation database copies specific design elements into your mail template and adds action buttons to the end of your Calendar Entry form. This section outlines how it works "under the hood", and then the components that make up the design.

### Design Considerations
The Notes/Domino Mail template is arguably one of the most mission critical, widely used, and complex Notes/Domino templates in use today. As such DOMI was designed from the beginning to be as "minimally invasive" to the Notes/Domino Mail Template as much as possible. This means that we didn't mind adding net-new design elements to the template, but we wanted to avoid editing existing elements as much as possible. We accomplished this by doing two things in particular:

1. We wrote our code to be completely self-contained, primarily within Script Libraries and Agents
2. We "surface" the DOMI functionality through a set of Actions within the Calendar Entry/Appointment Form; these Actions are contained within a Subform that's added to the Calendar Entry Form during installation

This means the DOMI Installation app needed to do the following:

* Provide a way to store, identify, and copy all of the net-new DOMI design elements into the target Notes/Domino Mail template
* Provide a way to add the new DOMI Subform to the existing Calendar Entry Form
* Provide a way to "uninstall" itself by removing all DOMI design elements and remove the Subform from the Calendar Entry

How these tasks were accomplished is summarized below.