package org.ikasan.serialiser.converter;

import java.io.Serializable;

/**
 * Created by amajewski on 19/07/15.
 */
public class SimplePojo implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 3595514737829632181L;


    protected String id;

    /**
     * Get the serial uid
     *
     * @return serial uid
     */
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

}
