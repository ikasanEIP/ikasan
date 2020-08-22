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
}
