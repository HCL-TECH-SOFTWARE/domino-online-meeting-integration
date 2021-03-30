---
layout: default
title: How It Works
parent: DOMI Installation Code
nav_order: 1
has_children: false
last_modified_date: 2021.03.30
---

## Introduction
This section describes the overall design architecture of the Domino Online Meeting Integration (DOMI) Installation application, as well as some of the maintenance tools provided.

## Overview
The installation system used by DOMI is designed to be a self-contained, reusable delivery mechanism for installing new features into existing Notes/Domino databases and templates; DOMI just happens to be the first feature using this system. Each Installation database should contain one and only one featureset, as this makes the focus more targeted and concise. The installation database contains both its own design elements used by the system itself, as well as the design elements that are a part of the feature it will install - DOMI in this case.

## DOMI Feature Installation
The installation of DOMI is accomplished through a single agent that Administrators normally access through the DOMI landing page in the DOMI Installation database. The agent is surfaced to the Administrator through the _Install DOMI_ Action.
![Install DOMI Action]({{'/assets/images/installation/DOMIInstallAction.png' | relative_url}})

As mentioned earlier, the two main goals of the installation portion of DOMI are to:

* Provide a way to store, identify, and copy all of the net-new DOMI design elements into the target Notes/Domino Mail template
* Provide a way to add the new DOMI Subform to the existing Calendar Entry Form

The DOMI Installation database provides us a place to store the DOMI feature design elements. The Design Element Profiles provide us a way to identify which design elements in the database are a part of the DOMI featureset. The installation agent handles the remaining goals - copy all of the DOMI design elements into the target mail template, and add the DOMI Subform to the existing Calendar Entry form. This agent is called _01. Install DOMI in Target Mail Db_. 

But how exactly does that agent work? Let's take a closer look.

### Background
During development of the DOMI Installation system, we discovered an interesting "quirk" in Notes/Domino. During the process of copying the feature design elements into the target mail template, we first check to see if the element already exists - if it does, we delete it so we can copy the new one in. We found out that if you do this, the design element is "cached" within the db design; and as such when you try to copy the new element into the database after deleting the old one, you get an error basically informing you that someone else edited the element while you were working on it, and it prevents the copy from taking place. Incidentally, this only seems to happen with code-based design elements - namely Agents and Script Libraries. 

To get around this, we took a novel approach. The design elements are indexed by their IDs - namely their UNIDs and NoteIDs. So, to get around this limitation, we have code that changes the UNID of all of the Script Libraries and Agents in the featureset BEFORE the installation takes place. And since we're updating the UNIDs of the design elements, we need to update the corresponding Design Element Profiles as well.

Now that you understand this quirk and how we work around it, let's take a look at how the agent actually works.
