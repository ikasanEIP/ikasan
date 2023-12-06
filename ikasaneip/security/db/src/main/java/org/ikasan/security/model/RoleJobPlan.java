package org.ikasan.security.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
public class RoleJobPlan
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "JobPlanName", nullable = false)
    private String jobPlanName;
    @ManyToOne
    @JoinColumn(name="RoleId", nullable=false, updatable = false)
    private Role role;

    /** The date time stamp when an instance was first created */
    @Column(name = "CreatedDateTime", nullable = false)
    private Date createdDateTime;

    /** The date time stamp when an instance was last updated */
    @Column(name = "UpdatedDateTime", nullable = false)
    private Date updatedDateTime;

    public RoleJobPlan()
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

    public String getJobPlanName() {
        return jobPlanName;
    }

    public void setJobPlanName(String jobPlanName) {
        this.jobPlanName = jobPlanName;
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
        RoleJobPlan that = (RoleJobPlan) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(jobPlanName, that.jobPlanName) &&
            Objects.equals(role, that.role) &&
            Objects.equals(createdDateTime, that.createdDateTime) &&
            Objects.equals(updatedDateTime, that.updatedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobPlanName, role, createdDateTime, updatedDateTime);
    }
}
