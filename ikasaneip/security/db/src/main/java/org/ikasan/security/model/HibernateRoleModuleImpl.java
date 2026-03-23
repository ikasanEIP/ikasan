package org.ikasan.security.model;

import jakarta.persistence.*;
import org.ikasan.spec.security.model.Role;
import org.ikasan.spec.security.model.RoleModule;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "RoleModule")
public class HibernateRoleModuleImpl implements RoleModule
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ModuleName", nullable = false)
    private String moduleName;
    @ManyToOne()
    @JoinColumn(name="RoleId", nullable=false, updatable = false)
    private HibernateRoleImpl role;

    /** The date time stamp when an instance was first created */
    @Column(name = "CreatedDateTime", nullable = false)
    private Date createdDateTime;

    /** The date time stamp when an instance was last updated */
    @Column(name = "UpdatedDateTime", nullable = false)
    private Date updatedDateTime;

    public HibernateRoleModuleImpl()
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
        this.role = (HibernateRoleImpl) role;
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
        HibernateRoleModuleImpl that = (HibernateRoleModuleImpl) o;
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
