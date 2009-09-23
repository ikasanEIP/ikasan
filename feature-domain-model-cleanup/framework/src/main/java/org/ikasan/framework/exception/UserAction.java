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
package org.ikasan.framework.exception;

// Imported java classes
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class provides possible UserActions that are used for handling
 * exceptions.
 * 
 * @author Ikasan Development Team
 */
public class UserAction
    implements Serializable
{
    /**
     * Serial GUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(UserAction.class);

    /**
     * The action <code>STOP</code> designates very severe error events that
     * will presumably lead the process to abort the current transaction and
     * stop.
     */
    public static final UserAction ACCEPT =
        new UserAction("accept", 4, "Accept user operation");

    /**
     * The UserAction <code>RESTART</code> designates potentially harmful
     * error events but allows the process to recover the situation. Process can
     * be restarted from the last successful commit.
     */
    public static final UserAction REJECT =
        new UserAction("reject", 3, "Reject user action");

    /**
     * The UserAction <code>CONTINUE</code> designates error events that might
     * still allow the application to ignore the exception and simply continue
     * with the publication of the event, but without exception publication.
     */
    public static final UserAction RETRY =
        new UserAction("retry", 2, "Retry user action");

    /**
     * The list of UserActions.
     */
    private static ArrayList<UserAction> listOfUserActions = new ArrayList<UserAction>();
    static
    {
        Field fields[] = UserAction.class.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (UserAction.class.isAssignableFrom(fields[i].getType()))
            {
                UserAction field = null;
                try
                {
                    field = (UserAction)(fields[i].get(null));
                    listOfUserActions.add(field);
                }
                catch (Exception e)
                {
                    logger.warn("User action not loaded.", e);
                }
            }
        }
    }

    /** TYpe of user action */
    protected String type = null;

    /** Id */
    protected int id = 0;

    /** Description */
    protected String description = null;

    /**
     * Creates a new instance of <code>UserAction</code>.
     * 
     * @param type 
     * @param id 
     * @param description 
     */
    protected UserAction(String type, int id, String description)
    {
        this.type = type;
        this.id = id;
        this.description = description;
    }

    /**
     * Creates a new instance of <code>UserAction</code>.
     * 
     * @param type 
     */
    protected UserAction(String type)
    {
        this.type = type;
    }

    /**
     * Returns the type of this UserAction.
     * 
     * @return type
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Compares this UserAction to the specified object.
     * 
     * @param UserAction 
     * @return true if it equals
     */
    public boolean equals(String UserAction)
    {
        if (UserAction == null) return false;
        return this.type.equals(UserAction);
    }

    /**
     * Compares this <code>UserAction</code> to the specified object.
     * 
     * @param UserAction 
     * @return true if it equals
     */
    public boolean equals(UserAction UserAction)
    {
        if (UserAction == null) return false;
        return this.type.equals(UserAction.type);
    }

    /**
     * Returns the list of UserActions.
     * 
     * @return the list of UserActions.
     */
    public static List<UserAction> getListOfUserActions()
    {
        return listOfUserActions;
    }

    /**
     * Returns a String object representing this UserAction's value.
     */
    @Override
    public String toString()
    {
        return "{" + this.type + ", " + this.id + ", " + this.description + "}";
    }

    /**
     * Runs this class for test.
     * TODO Unit Test
     * 
     * @param args 
     */
    public static void main(String args[])
    {
        List<UserAction> list = UserAction.getListOfUserActions();
        for (int i = 0; i < list.size(); i++)
        {
            logger.info("UserAction: " + list.get(i) + ".");
        }
    }

}
