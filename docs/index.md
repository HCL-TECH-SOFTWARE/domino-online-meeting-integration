---
layout: default
title: Overview
nav_order: 1
description: "HCL Domino Online Meeting Integration"
has_children: true
permalink: /
last_modified_date: 2021.03.02
---

### HCL Domino Online Meeting Integration
{: .fs-9 }
Version {{ site.version }}
{: .fs-3 }

## Goals

Domino Online Meeting Integration (DOMI) had the following goals:
- Provide support to create, update and delete ad hoc meetings for:
   1. Zoom
   2. Microsoft Teams
   3. Webex
   4. GoToMeeting
-  Use modern APIs rather than talking to the vendors' desktop applications.
-  Work via mail template modifications rather than HCL Notes Standard Client (Eclipse) plugins.
-  Support Sametime Meetings ad hoc meetings.
-  Support Verse.

## Limitations

GoToMeeting requires a paid account. Free accounts do not have access to run GoToMeeting REST APIs and an HTTP status code 500 will be returned.
{: .alert .alert-warning}

The phase 1 delivery has the following limitations:
- No multi-lingual support.
- No support for repeating meetings. Domino's flexibility for how meetings repeat is not available in other meeting services, so the "right" approach is unclear.
- Tokens need manually copying into Notes Client from a web application. OAuth process requires loading a browser and receiving either a code as a query string parameter for the meeting services or a header parameter for Sametime. Notes Client functionality can be called via `notes://` protocol (Notes Client) or `notes+web://` protocol (Nomad Web). However, the protocols currently ignore anything other than the base URL. Until that changes, it will not be possible to trigger an OAuth process from Notes Client without requiring the user to copy content back into the Notes Client.
- Tokens are not encrypted, but the Online Meeting Credentials form has a Readers field for the author, mail database owner, LocalDomainServers and \[DOMIAdmin] role. The \[DOMIAdmin] role can be leveraged for support purposes, if desired. The assumption is that PAs will create the Online Meeting Credentials form on behalf of their manager. If this is not your corporate policy, an additional role can be added to the form to expose it to PAs as well as the mail database owner. For group calendars, the assumption is that the "group entity" will not have its own account with the meeting provider, but individuals will use their own credentials.
- Token will need refreshing periodically. Scheduled agents can only be scheduled for a specific time, not a particular interval from deployment. Consequently, using a scheduled agent would place excessive load on the web application and could give the impression to third-party services of token harvesting. So the phase 1 approach has been to refresh programmatically when creating a meeting. If the refresh token has expired, the user will need to re-initiate the OAuth dance, and functionality has been added to support this.
- Zoom is the only provider that supports revocation of tokens. So this is the only Online Meeting Credential type that has a Revoke Token button.
Customer testing will inform wether or not this is sufficient for a real-world implementation. It is possible, though by no means certain, that a more regular refresh might be required. But because a perfect approach is not obvious, it makes more sense to gather more information around usage before guessing on a better option. Metrics are gathered for requests for tokens, which will provide some background on throughput.
