package org.ikasan.security.service;

import org.ikasan.security.SecurityAutoConfiguration;
import org.ikasan.security.SecurityTestAutoConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class is to test with the property value "ikasan.dashboard.extract.enabled" set to true
 * This is to make sure that when this property is enabled that we do not try and authenticate using the user service.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityAutoConfiguration.class, SecurityTestAutoConfiguration.class})
@TestPropertySource(properties = "ikasan.dashboard.extract.enabled = true")
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class UserServiceDashboardExtractEnableTest {

    private User admin;
    private  User disabledUser;

    @Autowired
    private UserDao xaUserDao;
    @Autowired
    private UserService xaUserService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup()
    {
        admin = new User("admin", "admin", "admin@admin.com",true);
        disabledUser = new User("disabledUser", "disabledUser", "disabledUser@admin.com",false);

        xaUserDao.save(admin);
        xaUserDao.save(disabledUser);
    }

    /**
     * Test to make sure that admin user even though allow to authenticate can't when property ikasan.dashboard.extract.enabled=true
     */
    @Test
    @DirtiesContext
    public void testLoadUserByUsername()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("ikasan.dashboard.extract.enabled=true. Do not try to authenticate locally. Username : admin");

        User result = xaUserService.loadUserByUsername("admin");

        Assert.assertEquals(admin,result);
    }

    /**
     * Test to make sure that disable user won't even attempt to authenticate when property ikasan.dashboard.extract.enabled=true
     */
    @Test
    @DirtiesContext
    public void testLoadUserByUsernameWhenUserIsDisabled()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("ikasan.dashboard.extract.enabled=true. Do not try to authenticate locally. Username : disabledUser");

        User result = xaUserService.loadUserByUsername("disabledUser");

        Assert.assertEquals(admin,result);
    }

    @Test
    @DirtiesContext
    public void testLoadUserByUsernameWhenUserDoesntExist()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("ikasan.dashboard.extract.enabled=true. Do not try to authenticate locally. Username : unknown");

        User result = xaUserService.loadUserByUsername("unknown");

        Assert.assertEquals(admin,result);
    }

}

