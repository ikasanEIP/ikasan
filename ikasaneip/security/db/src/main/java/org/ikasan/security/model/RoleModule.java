package org.ikasan.security.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
public class RoleModule
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ModuleName")
    private String moduleName;
    @ManyToOne
    @JoinColumn(name="RoleId", nullable=false, updatable = false)
    private Role role;

    /** The date time stamp when an instance was first created */
    @Column(name = "CreatedDateTime")
    private Date createdDateTime;

    /** The date time stamp when an instance was last updated */
    @Column(name = "UpdatedDateTime")
    private Date updatedDateTime;

    public RoleModule()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Date updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleModule that = (RoleModule) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(moduleName, that.moduleName) &&
            Objects.equals(role, that.role) &&
            Objects.equals(createdDateTime, that.createdDateTime) &&
            Objects.equals(updatedDateTime, that.updatedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moduleName, role, createdDateTime, updatedDateTime);
    }
}
