![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Security

The matrix below shows for a given policy, what is functionally allowed for users how are assigned a role with that policy.


| Functionality  | Read  | Write  | Admin  | Description  |
|---|---|---|---|---|
|**Wiretaps**|**wiretap-read**|**wiretap-write**|**wiretap-admin**|   |
|Search/View Wiretap|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|Search/View Wiretap|

Create new wiretap

(tick)

(tick)

Create new wiretap

Search Wiretap

(tick)

(tick)

(tick)

Search Wiretap

Download Wiretap

(tick)

(tick)

(tick)

Download Wiretap

Errors

error-read

error-write

error-admin

Search/View Error

(tick)

(tick)

(tick)

Search/View Error

Close Error

(tick)

(tick)

Close Error

Comment on Error

(tick)

(tick)

Comment on Error

View Error Links

(tick)

(tick)

(tick)

View Error Links

Export Errors as JIRA Table

(tick)

(tick)

(tick)

Export Errors as JIRA Table

Export Errors as CSV

(tick)

(tick)

(tick)

Export Errors as CSV

Actioned Errors

actioned_error-read

actioned_error-write

actioned_error-admin

(tick)

(tick)

(tick)

Search/View Actioned Errors

Categorised Errors

categorised_error-read

categorised_error-admin

categorised_error-admin

(tick)

(tick)

(tick)

Search View Categorised Errors

(tick)

(tick)

Comment on Categorised Errors

(tick)

(tick)

Close Categorised Errors

(tick)

Resubmit Excluded Categorised Error

(tick)

(tick)

(tick)

Export Categorised Errors as JIRA Table

Exclusions

exclusion-read

exclusion-write

exclusion-admin

(tick)

(tick)

(tick)

Search/View Exclusions

(tick)

Resubmit Exclusion

(tick)

Ignore Exclusion

(tick)

(tick)

(tick)

Export Exclusions Errors as JIRA Table

(tick)

(tick)

(tick)

Export Exclusions Errors as CSV

Actioned Exclusions

actioned_exclusion-read

actioned_exclusion-write

actioned_exclusion-admin

(tick)

(tick)

(tick)

Search/View Actioned Exclusions

(tick)

(tick)

(tick)

Export Actioned Exclusions Errors as JIRA Table

(tick)

(tick)

(tick)

Export Actioned Exclusions Errors as CSV

System Events

system_event-read

system_event-write

system_event-admin

(tick)

(tick)

(tick)

Search/View System Events

Filters

filter-read

filter-write

filter-admin

(tick)

(tick)

(tick)

Search/View Filters

(tick)

(tick)

Create New Filter

(tick)

(tick)

Modify Existing Filter

(tick)

(tick)

Delete Filter

Topology Tree

topology-read

topology-write

topology-admin

Discover

(tick)

Refresh Tree

(tick)

(tick)

(tick)

Create New Server

(tick)

Edit Existing Server

(tick)

Create/Edit Server Level Categorised Error

(tick)

(tick)

View Server Level Categorised Error

(tick)

(tick)

View Server Details

(tick)

(tick)

(tick)

View Module Details

(tick)

(tick)

(tick)

Manage Module Level Component Configurations

(tick)

(tick)

Create/Edit Module Level Categorised Error

(tick)

(tick)

View Module Diagram

(tick)

(tick)

Start/Stop/Pause Flow

(tick)

Edit Flow Startup Type

(tick)

Create Flow Level Categorised Error

(tick)

(tick)

Configure Flow

(tick)

(tick)

Manage FlowLevel Component Configurations

(tick)

(tick)

View Component Configuration

(tick)

(tick)

(tick)

Edit Component Configuration

(tick)

(tick)

View Metrics Configuration

(tick)

(tick)

Edit Metrics Configuration

(tick)

(tick)

View Wiretaps

(tick)

(tick)

(tick)

Create New Wiretap

(tick)

(tick)

View Component Level Categorised Errors

(tick)

(tick)

Create/Edit Component Level Categorised Errors

(tick)

(tick)

Mapping

mapping-read

mapping-write

mapping-admin

Search Mappings

(tick)

(tick)

(tick)

View Mapping

(tick)

(tick)

(tick)

Delete Mapping

(tick)

Edit Mapping

(tick)

(tick)

Create New Client

(tick)

(tick)

Create New Context

(tick)

(tick)

Create New Type

(tick)

(tick)

Create New Mapping

(tick)

(tick)

Download Mapping

(tick)

(tick)

(tick)

Upload Mapping

(tick)

(tick)

Monitoring

monitoring-read

monitoring-write

monitoring-admin

View Monitoring

(tick)

(tick)

(tick)

Replay

replay-read

replay-write

replay-admin

Search/View Replay Event

(tick)

(tick)

(tick)

Download Replay Event

(tick)

(tick)

(tick)

Search/View Replay Audit Event

(tick)

(tick)

(tick)

Replay Events

(tick)

(tick)

Housekeeping

housekeeping-read

housekeeping-write

housekeeping-admin

View Housekeeping

(tick)

(tick)

(tick)

Start/Stop Housekeeping Scheduler

(tick)

View Housekeeping Job

(tick)

(tick)

(tick)

Edit Housekeeping Job

(tick)

(tick)

User Administration

user_administration-read

user_administration-write

user_administration-admin

View User Administration

(tick)

(tick)

(tick)

Create New User

(tick)

Add Role to Existing User

(tick)

(tick)

Group Administration

group_administration-read

group_administration-write

group_administration-admin

View Group Administration

(tick)

(tick)

(tick)

Add Role to Group

(tick)

(tick)

Role Administration

role_administration-read

role_administration-write

role_administration-admin

View Role Administration

(tick)

(tick)

(tick)

Create New Role

(tick)

Delete Existing Role

(tick)

Add Policy to Role

(tick)

(tick)

Assign User Role

(tick)

(tick)

Assign Group Role

(tick)

(tick)

Policy Administration

policy_administration-read

policy_administration-write

policy_administration-admin

View Policy Administration

(tick)

(tick)

(tick)

Add New Dynamic Policy

(tick)

Delete Dynamic Policy

(tick)

Assign Policy to Role

(tick)

(tick)

User Directory Administration

user_directory-read

user_directory-write

user_directory-admin

View User Directory Administration

(tick)

(tick)

(tick)

Add Directory

(tick)

Disable User Directory

(tick)

(tick)

Synchronise User Directory

(tick)

(tick)

Edit User Directory

(tick)

View User Directory Configuration

(tick)

(tick)

Delete User Directory

(tick)

Test User Directory Configuration

(tick)

(tick)

Change Directory Order

(tick)

(tick)

Platform Configuration

platform_configuration-read

platform_configuration-write

platform_configuration-admin

View Platform Configurations

(tick)

(tick)

(tick)

Add Platform Configuration

(tick)

Remove Platform Configuration

(tick)

Re-create Platform Configuration

(tick)

Edit Existing Configuration

(tick)

(tick)

Notifications

notification-read

notification-write

notification-admin

View Motifications

(tick)

(tick)

(tick)

Start/Stop Notification Scheduler

(tick)

Create New Notification

(tick)

(tick)

Delete Notification

(tick)

Edit Existing Notification


