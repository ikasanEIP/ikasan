 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.security.model;

 import jakarta.persistence.*;

 import java.security.Principal;
 import java.util.Date;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.StringJoiner;

 /**
 *
 * @author Ikasan Development Team
 */
@Entity
@Table(name = "SecurityPrincipal")
 public class IkasanPrincipal implements Principal
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Name")
    private String name;
    @Column(name = "PrincipalType")
    private String type;
    @Column(name = "ApplicationSecurityBaseDn")
    private String applicationSecurityBaseDn;
    @Column(name = "Description")
    private String description;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "PrincipalRole",
        joinColumns = { @JoinColumn(name = "PrincipalId") },
        inverseJoinColumns = { @JoinColumn(name = "RoleId") }
    )
    private Set<Role> roles;

    /** The date time stamp when an instance was first created */
    @Column(name = "CreatedDateTime")
    private Date createdDateTime;

    /** The date time stamp when an instance was last updated */
    @Column(name = "UpdatedDateTime")
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public IkasanPrincipal()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
    }

	public IkasanPrincipal(String name, String type, String description) {
		this.name = name;
		this.type = type;
		this.description = description;
		long now = System.currentTimeMillis();
		this.createdDateTime = new Date(now);
		this.updatedDateTime = new Date(now);
	}

	/**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the createdDateTime
     */
    public Date getCreatedDateTime()
    {
        return createdDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(Date createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public Date getUpdatedDateTime()
    {
        return updatedDateTime;
    }

    /**
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(Date updatedDateTime)
    {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * @return the roles
     */
    public Set<Role> getRoles()
    {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<Role> roles)
    {
        this.roles = roles;
    }

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

    /**
     * @return the applicationSecurityBaseDn
     */
    public String getApplicationSecurityBaseDn() {
        return applicationSecurityBaseDn;
    }

    /**
     * @param applicationSecurityBaseDn the applicationSecurityBaseDn to set
     */
    public void setApplicationSecurityBaseDn(String applicationSecurityBaseDn) {
        this.applicationSecurityBaseDn = applicationSecurityBaseDn;
    }

    public void addRole(Role role)
    {
		if(roles!=null)
        {
			if(!roles.contains(role))
            {
				roles.add(role);
			}
		}
        else
        {
			roles= new HashSet<>();
			roles.add(role);

		}
	}

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IkasanPrincipal that = (IkasanPrincipal) o;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IkasanPrincipal.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("type='" + type + "'")
            .add("applicationSecurityBaseDn='" + applicationSecurityBaseDn + "'")
            .add("description='" + description + "'")
            .add("roles=" + roles)
            .add("createdDateTime=" + createdDateTime)
            .add("updatedDateTime=" + updatedDateTime)
            .toString();
    }
}
