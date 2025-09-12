# JobLockCacheImpl

This document provides an overview of the `JobLockCacheImpl` class and its associated data model.

## Mermaid Diagram

The following diagram illustrates the relationships between `JobLockCacheImpl` and other key classes and interfaces in the system.

```mermaid
classDiagram
    class JobLockCacheImpl {
        -jobLockCacheData: JobLockCacheData
        -jobLockCacheService: JobLockCacheService
        -jobLockCacheEventBroadcaster: JobLockCacheEventBroadcaster
        -executor: ExecutorService
        +instance(): JobLockCacheImpl
        +addLocks(List~JobLock~)
        +lock(String, String): boolean
        +release(String, String): boolean
        +locked(String, String): boolean
        +hasLock(String, String): boolean
    }

    class JobLockCacheDataImpl {
        -jobLocksByLockName: Map~String, JobLockHolder~
        -jobLocksByIdentifier: Map~String, String~
        -exclusiveLockHolder: JobLockHolder
    }

    class JobLockHolderImpl {
        -lockName: String
        -lockCount: int
        -exclusiveJobLock: boolean
        -lockHolders: Set~String~
        -schedulerJobs: Map~String, List~SchedulerJobLockParticipant~~
    }
    
    class JobLockCacheRecordImpl {
        -jobLockCache: JobLockCacheData
    }


    class JobLockCacheEventListener {
        +onJobLockCacheEvent(JobLockCacheEvent)
    }

    class JobLockCacheData {
    }

    class JobLockHolder {
    }
    
    class JobLockCacheRecord {
    }

    class JobLockCacheService {
        +save(JobLockCacheRecord)
    }
    
    class JobLockCacheEventBroadcaster {
        +broadcast(JobLockCacheEvent)
    }

    JobLockCacheImpl --|> JobLockCacheEventListener
    JobLockCacheImpl o-- JobLockCacheData
    JobLockCacheImpl --> JobLockCacheService
    JobLockCacheImpl --> JobLockCacheEventBroadcaster
    JobLockCacheDataImpl --|> JobLockCacheData
    JobLockHolderImpl --|> JobLockHolder
    JobLockCacheRecordImpl --|> JobLockCacheRecord
    JobLockCacheImpl o-- JobLockCacheRecord
    JobLockCacheRecordImpl o-- JobLockCacheData
    JobLockCacheDataImpl o-- JobLockHolder
```
