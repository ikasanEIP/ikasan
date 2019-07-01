package org.ikasan.spec.metadata;

public interface Transition
{
    /**
     * Get from component name.
     *
     * @return
     */
    public String getFrom();

    /**
     * Set from component name.
     *
     * @param from
     */
    public void setFrom(String from);

    /**
     * Get to component name.
     *
     * @return
     */
    public String getTo();

    /**
     * Get from component name.
     *
     * @param to
     */
    public void setTo(String to);

    /**
     * Get the transition name.
     *
     * @return
     */
    public String getName();

    /**
     * Set the transition name.
     *
     * @param name
     */
    public void setName(String name);
}
