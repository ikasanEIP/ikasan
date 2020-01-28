package org.ikasan.dashboard.boot;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.ikasan.security.service.UserService;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class WebContextInitializer implements VaadinServiceInitListener
{

    private Logger logger = LoggerFactory.getLogger(WebContextInitializer.class);

    @Resource
    private PersistenceService persistenceService;

    @Resource
    private UserService userService;

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent)
    {
        try
        {
            createFull();
            upgrade();
            installFileTransfer();
        }
        catch (PersistenceServiceException e)
        {
            logger.error("An error has occurred applying persistence initialisation!", e);
        }
    }

    protected void createFull() throws PersistenceServiceException
    {
        if(persistenceService.baselinePersistenceChangesRequired())
        {
            persistenceService.createBaselinePersistence();

            userService.changeUsersPassword("admin", "admin", "admin");
        }
    }

    protected void upgrade() throws PersistenceServiceException
    {
        if(persistenceService.postBaselinePersistenceChangesRequired())
        {
            persistenceService.createPostBaselinePersistence();
        }
    }

    protected void installFileTransfer() throws PersistenceServiceException
    {
        if(persistenceService.fileTransferPersistenceChangesRequired())
        {
            persistenceService.createFileTransferPersistence();
        }
    }
}