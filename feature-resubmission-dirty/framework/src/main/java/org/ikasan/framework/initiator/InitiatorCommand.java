package org.ikasan.framework.initiator;

import java.util.Date;

/**
 * This class encapsulates the intention to carry out some action on an <code>Initiator</code>
 * 
 * Capturing this information is useful for restoring an <code>Initiator</code> back to hopefully 
 * the same state, following a server reboot (expected, or otherwise).
 * 
 * This class has appropriate fields in order to:
 * <ul>
 *  <li>identify the target <code>Initiator</code></li>
 *  <li>describe the action (probably 'stop' or 'start') that we want carried out on the <code>Initiator</code></li>
 *  <li>know who the actor was - this could be useful for auditing</li>
 *  <li>know the time the action was submitted</li>
 * </ul>
 * 
 * Note that a historical collection of <code>InitiatorCommand</code>s could form the basis of an auditing record.
 * 
 * @author Ikasan Development Team
 *
 */
public class InitiatorCommand
{
    /**
     * Name of the module with which the target Initiator is associated
     */
    private String moduleName;
    
    /**
     * Name of the target Initiator
     */
    private String initiatorName;
    
    /**
     * Action to perform against the initiator, eg start, stop
     */
    private String action;
    
    /**
     * Name of the principal requesting the action
     */
    private String actor;
    
    /**
     * Time that this action was requested
     */
    private Date submittedTime;
    
    /**
     * Identity key
     */
    private Long id;
    
    
    /**
     * No args constructor, as required by ceratin ORM tools
     */
    @SuppressWarnings("unused")
    private InitiatorCommand(){}
    


 
    /**
     * Constructor
     * 
     * @param moduleName - Name of the module with which the target Initiator is associated
     * @param initiatorName - Name of the target Initiator
     * @param action - Action to perform against the initiator, eg start, stop
     * @param actor - Name of the principal requesting the action
     */
    public InitiatorCommand(String moduleName, String initiatorName, String action, String actor){
        this.moduleName = moduleName;
        this.initiatorName = initiatorName;
        this.action = action;
        this.actor = actor;
        this.submittedTime = new Date();
    }
    

    /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }


    /**
     * Setter for moduleName
     * 
     * @param moduleName
     */
    @SuppressWarnings("unused")
    private void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Accessor for initiatorName
     * 
     * @return initiatorName
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }



    /**
     * Setter for initiatorName
     * 
     * @param initiatorName
     */
    @SuppressWarnings("unused")
    private void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * Accessor for action
     * 
     * @return action
     */
    public String getAction()
    {
        return action;
    }


    /**
     * Setter for action
     * 
     * @param action
     */
    @SuppressWarnings("unused")
    private void setAction(String action)
    {
        this.action = action;
    }

    /**
     * Accessor for actor
     * 
     * @return actor
     */
    public String getActor()
    {
        return actor;
    }

    /**
     * Setter for actor
     * 
     * @param actor
     */
    @SuppressWarnings("unused")
    private void setActor(String actor)
    {
        this.actor = actor;
    }

    /**
     * Accessor for submittedTime
     * 
     * @return submittedTime
     */
    public Date getSubmittedTime(){
        return submittedTime;
    }
    
    /**
     * Setter for timeSubmitted
     * 
     * @param timeSubmitted
     */
    @SuppressWarnings("unused")
    private void setSubmittedTime(Date timeSubmitted){
        this.submittedTime = timeSubmitted;
    }
    
    /**
     * Accessor for id
     * 
     * @return id
     */
    public Long getId(){
        return id;
    }
    
    /**
     * Setter for id
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id){
        this.id = id;
    }
    
    
    
    
}
