package org.ikasan.spec.systemevent;

/**
 * Interface for SystemEvent record implementations.
 * Extends SystemEvent with additional methods needed for persistence layer implementations.
 *
 * @author Ikasan Development Team
 */
public interface SystemEventRecord extends SystemEvent {

    /**
     * Get the type field used for entity discrimination.
     *
     * @return the type
     */
    String getType();

    /**
     * Set the type field used for entity discrimination.
     *
     * @param type the type to set
     */
    void setType(String type);

    /**
     * Set the module name.
     *
     * @param moduleName the module name to set
     */
    void setModuleName(String moduleName);

    /**
     * Set the action.
     *
     * @param action the action to set
     */
    void setAction(String action);

    /**
     * Set the actor.
     *
     * @param actor the actor to set
     */
    void setActor(String actor);

    /**
     * Set the subject.
     *
     * @param subject the subject to set
     */
    void setSubject(String subject);

    /**
     * Get the payload content.
     *
     * @return the payload content
     */
    String getPayload();

    /**
     * Set the payload content.
     *
     * @param payload the payload content to set
     */
    void setPayload(String payload);
}
