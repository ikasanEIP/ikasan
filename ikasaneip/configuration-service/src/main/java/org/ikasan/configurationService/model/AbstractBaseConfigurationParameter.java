package org.ikasan.configurationService.model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractBaseConfigurationParameter {

    /** required by ORM */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;

    /** configuration name */
    @Column(name="Name", nullable = false)
    protected String name;

    /** configuration description */
    @Column(name="Description")
    protected String description;

    /**
     * Getter for id
     * @return
     */
    public Long getId()
    {
        return this.id;
    }

    /**
     * Setter for id
     * @param id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Getter for name
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Setter for name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Getter for description
     * @return
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Setter for description
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
}
