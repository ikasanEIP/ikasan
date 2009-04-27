/*
 * $Id: ConnectionHandler.java 10033 2008-04-09 14:54:22Z verbma $
 * $URL: $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.exception;

// Imported java classes
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

// Imported log4j classes
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
