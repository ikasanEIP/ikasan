package org.ikasan.spec.module.client;

public interface TriggerService<T>
{
    /**
     * Create a trigger on a module.
     *
     * @param contextUrl
     * @param triggerDto
     * @return
     */
    public boolean create(String contextUrl, T triggerDto);

    /**
     * Remove a trigger from a module.
     *
     * @param contextUrl
     * @param triggerId
     * @return
     */
    public boolean delete(String contextUrl, String triggerId);


    /**
     * Deletes a trigger based on the context URL, trigger ID, and user.
     *
     * @param contextUrl the URL of the context/module where the trigger is to be deleted
     * @param triggerId the ID of the trigger to be deleted
     * @param user the user initiating the deletion
     * @return true if the trigger was successfully deleted, false otherwise
     */
    public boolean delete(String contextUrl, String triggerId, String user);
}
