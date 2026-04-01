![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Security Specification Module

## Overview

This module defines the core security specification interfaces for the Ikasan Enterprise Integration Platform. It provides the contract layer that all security implementations must adhere to, establishing a consistent API for authentication, authorization, user management, and access control across the platform.

## Purpose

The security specification module serves as:
- **API Contract**: Defines interfaces that concrete implementations must implement
- **Type Definitions**: Provides model interfaces for security entities (User, Role, Policy, Principal)
- **DAO Contracts**: Specifies data access layer interfaces for persistence operations
- **Service Contracts**: Defines service-layer interfaces for security business logic
- **Decoupling Mechanism**: Enables multiple implementations (database, LDAP, REST) without coupling

## Module Structure

```
ikasaneip/spec/service/security/
├── src/main/java/org/ikasan/spec/security/
│   ├── dao/                          # Data Access Object interfaces
│   │   ├── AuthorityDao.java        # Authority persistence operations
│   │   ├── SecurityDao.java          # Security entity persistence
│   │   └── UserDao.java              # User persistence operations
│   ├── model/                        # Domain model interfaces
│   │   ├── AuthenticationMethod.java # Authentication configuration
│   │   ├── Authority.java            # Permission representation
│   │   ├── IkasanPrincipal.java     # Security principal
│   │   ├── IkasanPrincipalFilter.java # Principal query filter
│   │   ├── IkasanPrincipalLite.java # Lightweight principal
│   │   ├── JobPlanGrantedAuthority.java # Job plan permissions
│   │   ├── ModuleGrantedAuthority.java  # Module permissions
│   │   ├── Policy.java               # Authorization policy
│   │   ├── Role.java                 # User role
│   │   ├── RoleJobPlan.java         # Role-to-job-plan mapping
│   │   ├── RoleModule.java          # Role-to-module mapping
│   │   ├── User.java                 # User account
│   │   ├── UserFilter.java          # User query filter
│   │   ├── UserLite.java            # Lightweight user
│   │   └── constants/
│   │       └── SecurityConstants.java # Security constants
│   └── service/                      # Service interfaces
│       ├── AuthenticationService.java # Authentication operations
│       ├── AuthenticationServiceException.java
│       ├── SecurityService.java      # Security management
│       └── UserService.java          # User management
└── pom.xml
```

## Architecture

### Layered Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        REST[REST Controllers]
        WEB[Web Layer]
    end

    subgraph "Service Layer Specification"
        US[UserService Interface]
        SS[SecurityService Interface]
        AS[AuthenticationService Interface]
    end

    subgraph "DAO Layer Specification"
        UD[UserDao Interface]
        SD[SecurityDao Interface]
        AD[AuthorityDao Interface]
    end

    subgraph "Model Layer Specification"
        USER[User Interface]
        ROLE[Role Interface]
        POLICY[Policy Interface]
        PRINCIPAL[IkasanPrincipal Interface]
    end

    REST --> US
    REST --> SS
    WEB --> AS

    US --> UD
    SS --> SD
    AS --> AD

    UD --> USER
    SD --> ROLE
    SD --> POLICY
    SD --> PRINCIPAL

    style US fill:#e1f5ff
    style SS fill:#e1f5ff
    style AS fill:#e1f5ff
    style UD fill:#fff3e0
    style SD fill:#fff3e0
    style AD fill:#fff3e0
    style USER fill:#f3e5f5
    style ROLE fill:#f3e5f5
    style POLICY fill:#f3e5f5
    style PRINCIPAL fill:#f3e5f5
```

### Security Model Relationships

```mermaid
classDiagram
    class User {
        <<interface>>
        +Object getId()
        +String getUsername()
        +String getPassword()
        +String getEmail()
        +boolean isEnabled()
        +Set~IkasanPrincipal~ getPrincipals()
        +Set~Role~ getRoles()
    }

    class IkasanPrincipal {
        <<interface>>
        +Object getId()
        +String getName()
        +String getType()
        +String getDescription()
        +Set~Role~ getRoles()
    }

    class Role {
        <<interface>>
        +Object getId()
        +String getName()
        +String getDescription()
        +Set~Policy~ getPolicies()
        +Set~RoleModule~ getRoleModules()
        +Set~RoleJobPlan~ getRoleJobPlans()
    }

    class Policy {
        <<interface>>
        +Object getId()
        +String getName()
        +String getDescription()
        +Set~PolicyLink~ getPolicyLinks()
    }

    class RoleModule {
        <<interface>>
        +Object getId()
        +Role getRole()
        +String getModuleName()
    }

    class RoleJobPlan {
        <<interface>>
        +Object getId()
        +Role getRole()
        +String getJobPlanName()
    }

    class AuthenticationMethod {
        <<interface>>
        +Object getId()
        +String getName()
        +long getOrder()
        +String getMethod()
    }

    User "1" --> "*" IkasanPrincipal : has principals
    IkasanPrincipal "1" --> "*" Role : has roles
    Role "1" --> "*" Policy : has policies
    Role "1" --> "*" RoleModule : module access
    Role "1" --> "*" RoleJobPlan : job plan access
```

### Service Layer Contracts

```mermaid
graph LR
    subgraph "UserService Operations"
        US1[Create User]
        US2[Update User]
        US3[Delete User]
        US4[Enable/Disable User]
        US5[Change Password]
        US6[Grant/Revoke Authority]
        US7[Query Users]
    end

    subgraph "SecurityService Operations"
        SS1[Create Principal/Role/Policy]
        SS2[Save Principal/Role/Policy]
        SS3[Delete Principal/Role/Policy]
        SS4[Find by Name]
        SS5[Get All Entities]
        SS6[Manage Role-Module Associations]
        SS7[Manage Authentication Methods]
    end

    subgraph "AuthenticationService Operations"
        AS1[Authenticate]
        AS2[Get Authentication Methods]
        AS3[Configure Auth Providers]
    end

    style US1 fill:#e8f5e9
    style US2 fill:#e8f5e9
    style US3 fill:#e8f5e9
    style US4 fill:#e8f5e9
    style US5 fill:#e8f5e9
    style US6 fill:#e8f5e9
    style US7 fill:#e8f5e9
    style SS1 fill:#e3f2fd
    style SS2 fill:#e3f2fd
    style SS3 fill:#e3f2fd
    style SS4 fill:#e3f2fd
    style SS5 fill:#e3f2fd
    style SS6 fill:#e3f2fd
    style SS7 fill:#e3f2fd
    style AS1 fill:#fff3e0
    style AS2 fill:#fff3e0
    style AS3 fill:#fff3e0
```

## Key Interfaces

### Service Interfaces

#### UserService
Manages user account lifecycle, credentials, and authorities.

**Key Operations:**
- `createUser(String username, String password, String email, boolean enabled)` - Create new user
- `loadUserByUsername(String username)` - Retrieve user for authentication
- `changeUsersPassword(String username, String newPassword, String confirmNewPassword)` - Password management
- `enableUser(String username)` / `disableUser(String username)` - Account status control
- `grantAuthority(String username, String authority)` - Assign permissions
- `getUsersWithRole(String roleName, UserFilter filter, int limit, int offset)` - Query users by role

#### SecurityService
Manages security entities (principals, roles, policies) and their relationships.

**Key Operations:**
- `createPrincipal()` / `createRole()` / `createPolicy()` - Entity factory methods
- `savePrincipal(IkasanPrincipal)` / `saveRole(Role)` / `savePolicy(Policy)` - Persistence
- `findPrincipalByName(String)` / `findRoleByName(String)` / `findPolicyByName(String)` - Lookups
- `getAllPrincipals()` / `getAllRoles()` / `getAllPolicies()` - Retrieval
- `setJobPlanRoles(String jobPlanName, List<String> roleNames)` - Associate roles with job plans

#### AuthenticationService
Handles authentication operations and provider configuration.

**Key Operations:**
- `authenticate(String username, String password)` - Authenticate credentials
- `getAuthenticationMethods()` - Retrieve configured authentication methods

### DAO Interfaces

#### SecurityDao
Provides persistence operations for security entities.

**Key Methods:**
- Entity creation: `createPrincipal()`, `createRole()`, `createPolicy()`
- Persistence: `saveOrUpdatePrincipal()`, `saveOrUpdateRole()`, `saveOrUpdatePolicy()`
- Deletion: `deletePrincipal()`, `deleteRole()`, `deletePolicy()`
- Queries: `getPrincipalByName()`, `getRoleByName()`, `getPolicyByName()`
- Filtering: `getPrincipals(IkasanPrincipalFilter, int, int)`

#### UserDao
Provides persistence operations for user accounts.

**Key Methods:**
- `createUser(String username, String password, String email, boolean enabled)` - Create user
- `getUser(String username)` - Retrieve user by username
- `save(User user)` - Persist user changes
- `delete(User user)` - Remove user
- `getUsersWithRole(String roleName, UserFilter, int, int)` - Query by role
- `getUserByUsernameLike(String username)` - Search users

### Model Interfaces

All model interfaces follow a consistent pattern:
- `Object getId()` / `void setId(Object id)` - Universal ID handling
- Domain-specific getters and setters
- Collection accessors for relationships

**Key Model Entities:**
- **User**: Represents a user account with credentials and principals
- **IkasanPrincipal**: Security principal (user or application entity)
- **Role**: Grouping of policies assigned to principals
- **Policy**: Fine-grained permission
- **AuthenticationMethod**: Configuration for authentication providers

## Design Principles

### 1. Interface Segregation
Separate interfaces for different concerns (DAO, Service, Model) ensure clean separation of responsibilities.

### 2. Object-Based IDs
All entities use `Object` type for IDs, enabling flexibility for different persistence mechanisms (Long for JPA, String for NoSQL, etc.).

### 3. Spring Security Integration
- `User extends UserDetails` for Spring Security authentication
- `UserService extends UserDetailsManager` for user management
- Compatible with Spring Security's authentication and authorization framework

### 4. Filter-Based Queries
`UserFilter` and `IkasanPrincipalFilter` enable complex queries with pagination support.

### 5. Lightweight DTOs
`UserLite` and `IkasanPrincipalLite` provide efficient data transfer for listing operations.

## Authentication Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthService as AuthenticationService
    participant UserService
    participant SecurityDao
    participant UserDao

    Client->>AuthService: authenticate(username, password)
    AuthService->>UserService: loadUserByUsername(username)
    UserService->>UserDao: getUser(username)
    UserDao-->>UserService: User
    UserService-->>AuthService: User (with authorities)
    AuthService->>AuthService: validate credentials
    alt Valid credentials
        AuthService->>SecurityDao: getPrincipalByName(username)
        SecurityDao-->>AuthService: IkasanPrincipal (with roles)
        AuthService-->>Client: Authentication success
    else Invalid credentials
        AuthService-->>Client: Authentication failure
    end
```

## Authorization Model

```mermaid
graph TD
    USER[User] -->|has| PRINCIPAL[IkasanPrincipal]
    PRINCIPAL -->|assigned to| ROLE[Role]
    ROLE -->|contains| POLICY[Policy]
    ROLE -->|grants access to| MODULE[RoleModule]
    ROLE -->|grants access to| JOBPLAN[RoleJobPlan]
    POLICY -->|enforces| PERMISSION[Permissions]

    style USER fill:#e8f5e9
    style PRINCIPAL fill:#e3f2fd
    style ROLE fill:#fff3e0
    style POLICY fill:#f3e5f5
    style MODULE fill:#ffebee
    style JOBPLAN fill:#fce4ec
    style PERMISSION fill:#f1f8e9
```

## Usage Example

```java
// Service layer usage
public class SecurityManager {
    private final UserService userService;
    private final SecurityService securityService;

    public void createUserWithRole(String username, String password, String roleName) {
        // Create user
        User user = userService.createUser(username, password,
            username + "@example.com", true);

        // Create principal
        IkasanPrincipal principal = securityService.createPrincipal();
        principal.setName(username);
        principal.setType("user");
        securityService.savePrincipal(principal);

        // Assign role
        Role role = securityService.findRoleByName(roleName);
        principal.addRole(role);
        user.addPrincipal(principal);

        userService.updateUser(user);
    }
}
```

## Dependencies

This module has minimal dependencies:
- **Spring Security Core**: For `UserDetails`, `UserDetailsManager`, `GrantedAuthority`
- **Spring DAO Support**: For `DataAccessException`
- **Java Standard Library**: No additional external dependencies

## Implementation Modules

This specification is implemented by:
- **ikasan-security-db**: Hibernate/JPA database implementation
- **ikasan-security-service**: Service layer implementations
- **ikasan-security-rest**: REST API model implementations
- **ikasan-security-ldap**: LDAP authentication integration

## Best Practices

1. **Always implement all interface methods**: Ensure complete contract fulfillment
2. **Use filters for queries**: Leverage `UserFilter` and `IkasanPrincipalFilter` for flexible queries
3. **Handle nulls appropriately**: DAO methods may return `null` for not-found scenarios
4. **Implement proper exception handling**: Use `UsernameNotFoundException`, `DataAccessException`
5. **Maintain transactional boundaries**: Service layer should manage transactions
6. **Validate inputs**: Check for null/empty values before persistence operations
7. **Use lightweight entities for listings**: Prefer `UserLite` and `IkasanPrincipalLite` for large result sets

## Version Information

- **Module**: ikasan-spec-service-security
- **Parent**: ikasan-spec-service
- **Group ID**: org.ikasan
- **Artifact ID**: ikasan-spec-service-security
