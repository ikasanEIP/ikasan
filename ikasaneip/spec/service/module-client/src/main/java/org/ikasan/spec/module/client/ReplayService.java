package org.ikasan.spec.module.client;

public interface ReplayService
{
    /**
     * Replays an event within a specific module and flow in the given context URL using the provided credentials and actor.
     *
     * @param contextUrl the URL of the context where the event will be replayed
     * @param username the username used for authentication
     * @param password the password used for authentication
     * @param moduleName the name of the module where the event will be replayed
     * @param flowName the name of the flow within the module where the event will be replayed
     * @param event the event data represented as a byte array
     * @param actor the actor performing the replay action
     * @return true if the event replay was successful, false otherwise
     */
    boolean replay(String contextUrl, String username, String password, String moduleName, String flowName,
                          byte[] event, String actor);
}
