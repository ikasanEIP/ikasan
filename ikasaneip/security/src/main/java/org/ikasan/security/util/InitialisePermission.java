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
package org.ikasan.security.util;

import org.apache.log4j.Logger;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.Authority;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility class for programmatic initialisation of the security permissions persistence.
 * 
 * @author Ikasan Development Team
 */
public class InitialisePermission implements InitializingBean
{
    /** Logger instance */
    private Logger logger = Logger.getLogger(InitialisePermission.class);

    /** standard user role text */
    private static String ROLE_USER = "ROLE_USER";

    /** standard user role description */
    private static String ROLE_USER_DESC = "Users who may log into the system";

    /** standard admin role text */
    private static String ROLE_ADMIN = "ROLE_ADMIN";

    /** standard admin role description */
    private static String ROLE_ADMIN_DESC = "Users who may perform administration functions on the system";

    /** default Admin authority */
    private static Authority ADMIN_AUTHORITY = new Authority(ROLE_ADMIN, ROLE_ADMIN_DESC);

    /** default User authority */
    private static Authority USER_AUTHORITY = new Authority(ROLE_USER, ROLE_USER_DESC);

    /** user service against which to permission */
    private UserService userService;

    /** name of the module being permissioned */
    private String moduleName;

    /** we need access to the userDao to step around security on initial seeding of permissions */
    private UserDao userDao;

    /** root admin user account */
    private User rootAdminUser;

    /** module admini user account */
    private User moduleAdminUser;

    /**
     * Constructor
     * @param userService
     * @param moduleName
     */
    public InitialisePermission(UserService userService, String moduleName, UserDao userDao)
    {
        this.userService = userService;
        if(userService == null)
        {
            throw new IllegalArgumentException("userService cannot be 'null'");
        }

        this.userDao = userDao;
        if(userDao == null)
        {
            throw new IllegalArgumentException("userDao cannot be 'null'");
        }

        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
    }

    /**
     * Set root admin user
     * @param rootAdminUser
     */
    public void setRootAdminUser(User rootAdminUser)
    {
        this.rootAdminUser = rootAdminUser;
    }

    /**
     * Set admin user for this module name
     * @param moduleAdminUser
     */
    public void setModuleAdminUser(User moduleAdminUser)
    {
        this.moduleAdminUser = moduleAdminUser;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if(this.rootAdminUser != null)
        {
            StringBuilder logMsg = new StringBuilder();
          //  createAuthority(USER_AUTHORITY);
          //  createAuthority(ADMIN_AUTHORITY);

            User _user = userDao.getUser(rootAdminUser.getUsername());
            if(_user == null)
            {
                _user = rootAdminUser;
                logMsg.append("Created ");
            }
            else
            {
                logMsg.append("Updated ");
            }

            // side step security to permission this user
           // _user.grantAuthority(USER_AUTHORITY);
           // _user.grantAuthority(ADMIN_AUTHORITY);
            userDao.save(_user);
            logger.info(logMsg.toString() + " root admin user[" + rootAdminUser.getUsername() + "] and permissioned.");
        }


    }
}
