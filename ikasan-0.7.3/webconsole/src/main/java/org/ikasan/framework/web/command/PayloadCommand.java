package org.ikasan.framework.web.command;

/**
 * A Wrapper class that can contain a payload along with the module name and initiator 
 * name to deliver that payload to.
 *  
 * @author Ikasan Development Team
 */
public class PayloadCommand
{
    /** The content of the payload */
    private String payloadContent;
    
    /** The name of the module */
    private String moduleName;
    
    /** The name of the initiator */
    private String initiatorName;

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     */
    public PayloadCommand(String moduleName, String initiatorName)
    {
        super();
        this.moduleName = moduleName;
        this.initiatorName = initiatorName;
    }

    /**
     * Get the initiator name
     * @return the initiator name
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }

    /**
     * Get the module name
     * @return the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Get the payload content
     * @return the payload content
     */
    public String getPayloadContent()
    {
        return payloadContent;
    }

    /**
     * Set the initiator name
     * @param initiatorName - the initiator name
     */
    public void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * Set the module name
     * @param moduleName - the module name
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Set the payload content name
     * @param payloadContent - the payload content
     */
    public void setPayloadContent(String payloadContent)
    {
        this.payloadContent = payloadContent;
    }
}
