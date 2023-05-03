![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Security Matrix

The matrix below shows for a given policy, what is functionally allowed for users how are assigned a role with that policy.

| Functionality  | Admin  | Write  |  Read | Description  |
|---|:---:|:---:|:---:|---|
|**Wiretaps**|**wiretap-admin/wiretap_all_modules-admin**|**wiretap-write/wiretap_all_modules-write**|**wiretap-read/wiretap_all_modules-read**|   |
|Search/View Wiretap|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View Wiretap|
|Create new wiretap|:white_check_mark:|:white_check_mark:| |Create new wiretap|
|Search Wiretap|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search Wiretap|
|Download Wiretap|:white_check_mark:|:white_check_mark:|:white_check_mark:|Download Wiretap|
|  | | | | |
|**Errors**|**error-admin/error_all_modules-admin**|**error-write/error_all_modules-write**|**error-read/error_all_modules-read**|   |
|Search/View Error|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View Error|
|Download Error|:white_check_mark:|:white_check_mark:|:white_check_mark:|Download Error|
|Close Error|:white_check_mark:|:white_check_mark:||Close Error (functionality not supported in new dashboard)|
|Comment on Error|:white_check_mark:|:white_check_mark:||Comment on Error (functionality not supported in new dashboard)|
|Export Errors as JIRA Table|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Errors as JIRA Table (functionality not supported in new dashboard)|
|Export Errors as CSV|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Errors as CSV (functionality not supported in new dashboard)|
|  | | | | |
|**Actioned Errors**|**actioned_error-admin**|**actioned_error-write**|**actioned_error-read**|   |
|Search/View Actioned Errors|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View Actioned Errors (functionality not supported in new dashboard)|
|  | | | | |
|**Exclusions**|**exclusion-admin/exclusion_all_modules-admin**|**exclusion-write/exclusion_all_modules-write**|**exclusion-read/exclusion_all_modules-readf**|   |
|Search/View Exclusions|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View Exclusions|
|Resubmit Exclusion|:white_check_mark:|||Resubmit Exclusion|
|Ignore Exclusion|:white_check_mark:|||Ignore Exclusion|
|Export Exclusions Errors as JIRA Table|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Exclusions Errors as JIRA Table (functionality not supported in new dashboard)|
|Export Exclusions Errors as CSV|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Exclusions Errors as CSV (functionality not supported in new dashboard)|
|  | | | | |
|**Actioned Exclusions**|**actioned_exclusion-admin**|**actioned_exclusion-write**|**actioned_exclusion-read**|   |
|Search/View Actioned Exclusions|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View Actioned Exclusions|
|Export Actioned Exclusions Errors as JIRA Table|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Actioned Exclusions Errors as JIRA Table (functionality not supported in new dashboard)|
|Export Actioned Exclusions Errors as CSV|:white_check_mark:|:white_check_mark:|:white_check_mark:|Export Actioned Exclusions Errors as CSV (functionality not supported in new dashboard)|
|  | | | | |
|**System Events**|**system_event-admin**|**system_event-write**|**system_event-read**|   |
|Search/View System Events|:white_check_mark:|:white_check_mark:|:white_check_mark:|Search/View System Events|
|  | | | | |
|**Topology**|**topology-admin**|**topology-write**|**topology-read**|   |
|  | | | | |
|**Mapping**|**mapping-admin**|**mapping-write**|**mapping-read**|   |
|  | | | | |
|**Monitoring**|**monitoring-admin**|**monitoring-write**|**monitoring-read**|   |
|  | | | | |
|**Replay**|**replay-admin/replay_all_modules-admin**|**replay-write/replay_all_modules-write**|**replay-read/replay_all_modules-read**|   |
|  | | | | |
|**Housekeeping**|**housekeeping-admin**|**housekeeping-write**|**housekeeping-read**|   |
|  | | | | |
|**User Administration**|**user_administration-admin**|**user_administration-write**|**user_administration-read**|   |
|View User Administration|:white_check_mark:|:white_check_mark:|:white_check_mark:|View User Administration|
|Create New User|:white_check_mark:||||
|Add Role to Existing User|:white_check_mark:|:white_check_mark:|||
|  | | | | |
|**Group Administration**|**group_administration-admin**|**group_administration-write**|**group_administration-read**|   |
|View Group Administration|:white_check_mark:|:white_check_mark:|:white_check_mark:||
|Add Role to Group|:white_check_mark:|:white_check_mark:|||
|  | | | | |
|**Role Administration**|**role_administration-admin**|**role_administration-write**|**role_administration-read**|   |
|View Role Administration|:white_check_mark:|:white_check_mark:|:white_check_mark:||
|Create New Role|:white_check_mark:||||
|Delete Existing Role|:white_check_mark:||||
|Add Policy to Role|:white_check_mark:|:white_check_mark:|||
|Assign User Role|:white_check_mark:|:white_check_mark:|||
|Assign Group Role|:white_check_mark:|:white_check_mark:|||
|  | | | | |
|**Policy Administration**|**policy_administration-admin**|**policy_administration-write**|**policy_administration-read**|   |
|View Policy Administration|:white_check_mark:|:white_check_mark:|:white_check_mark:||
|Add New Dynamic Policy|:white_check_mark:||||
|Delete Dynamic Policy|:white_check_mark:||||
|Assign Policy to Role|:white_check_mark:|:white_check_mark:|||
|**User Directory Administration**|**user_directory-admin**|**user_directory-write**|**user_directory-read**|   |
|View User Directory Administration|:white_check_mark:|:white_check_mark:|:white_check_mark:||
|Add Directory|:white_check_mark:||||
|Disable User Directory|:white_check_mark:|:white_check_mark:|||
|Synchronise User Directory|:white_check_mark:|:white_check_mark:|||
|Edit User Directory|:white_check_mark:||||
|View User Directory Configuration|:white_check_mark:|:white_check_mark:|:white_check_mark:||
|Delete User Directory|:white_check_mark:||||
|Test User Directory Configuration|:white_check_mark:|:white_check_mark:|||
|Change Directory Order|:white_check_mark:|:white_check_mark:|||
|**Scheduled Job Administration**|**scheduler-admin**|**scheduler-write**|**scheduler-read**|   |
|View Scheduled Job Dashboard|:white_check_mark:|:white_check_mark:|:white_check_mark:|Roles with the scheduler-write, scheduler-read policies can only access agents they are provisioned to see.|
|View Scheduled Job History|:white_check_mark:|:white_check_mark:|:white_check_mark:|Roles with the scheduler-write, scheduler-read policies can only access agents they are provisioned to see.|
|View Scheduled Job Calendar|:white_check_mark:|:white_check_mark:|:white_check_mark:|Roles with the scheduler-write, scheduler-read policies can only access agents they are provisioned to see.|
|View Scheduled Job Statistics|:white_check_mark:|:white_check_mark:|:white_check_mark:|Roles with the scheduler-write, scheduler-read policies can only access agents they aree provisioned to see.|
|Create New Scheduled Job|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only create jobs for agents that they have been provisioned to access.|
|Edit Scheduled Job|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only edit jobs for agents that they have been provisioned to access.|
|Delete Scheduled Job|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only delete jobs for agents that they have been provisioned to access.|
|Control Scheduled Jobs (stop, start, pause)|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only control jobs for agents that they have been provisioned to access.|
|Control Scheduled Jobs (stop, start, pause)|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only control jobs for agents that they have been provisioned to access.|
|Fire Job Immediately|:white_check_mark:|:white_check_mark:||Roles with the scheduler-write policy can only control jobs for agents that they have been provisioned to access.|
