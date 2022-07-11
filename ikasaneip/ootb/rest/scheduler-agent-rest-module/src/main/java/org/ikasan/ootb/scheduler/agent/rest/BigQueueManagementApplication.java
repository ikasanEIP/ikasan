package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Module application implementing the REST contract for queue management in the agents.
 */
@RequestMapping("/rest/big/queue")
@RestController
public class BigQueueManagementApplication {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueueManagementApplication.class);

    @Autowired
    private BigQueueManagementService bigQueueManagementService;

    @Value("${big.queue.consumer.queueDir}")
    private String queueDir;

    @RequestMapping(method = RequestMethod.GET,
        value = "/",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity workingQueueDirectory() {
        try {
            return new ResponseEntity(queueDir, HttpStatus.OK);
        } catch (Exception e) {
            String message = "Error getting working directory for queue: " + e.getMessage();
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/{queueDir}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getQueues(@PathVariable("queueDir") String queueDir) {
        try {
            List<String> queues = bigQueueManagementService.listQueues(queueDir);
            return new ResponseEntity(queues, HttpStatus.OK);

        } catch (Exception e) {
            String message = String.format("Got exception trying to list queues for dir [%s]. Error [%s]", queueDir, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/size/{queueDir}/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity size(@PathVariable("queueDir") String queueDir,
                               @PathVariable("queueName") String queueName) {
        try {
            long size = bigQueueManagementService.size(queueDir, queueName);
            return new ResponseEntity(size, HttpStatus.OK);

        } catch (Exception e) {
            String message = String.format("Got exception trying to get size for dir [%s] queue [%s]. Error [%s]", queueDir, queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/peek/{queueDir}/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity peek(@PathVariable("queueDir") String queueDir,
                               @PathVariable("queueName") String queueName) {
        try {
            BigQueueMessage bigQueueMessage = bigQueueManagementService.peek(queueDir, queueName);
            return new ResponseEntity(bigQueueMessage, HttpStatus.OK);

        } catch (Exception e) {
            String message = String.format("Got exception trying to peek queue for dir [%s] queue [%s]. Error [%s]", queueDir, queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/messages/{queueDir}/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity messages(@PathVariable("queueDir") String queueDir,
                                   @PathVariable("queueName") String queueName) {
        try {
            List<BigQueueMessage> messages = bigQueueManagementService.getMessages(queueDir, queueName);
            return new ResponseEntity(messages, HttpStatus.OK);

        } catch (Exception e) {
            String message = String.format("Got exception trying to get message for dir [%s] queue [%s]. Error [%s]", queueDir, queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
        value = "/delete/{queueDir}/{queueName}/{messageId}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteMessageId(@PathVariable("queueDir") String queueDir,
                                          @PathVariable("queueName") String queueName,
                                          @PathVariable("messageId") String messageId) {
        try {
            bigQueueManagementService.deleteMessage(queueDir, queueName, messageId);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            String message
                = String.format("Got exception trying to delete message for dir [%s] queue [%s] message [%s]. Error [%s]", queueDir, queueName, messageId, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
        value = "/delete/{queueDir}/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteQueue(@PathVariable("queueDir") String queueDir,
                                      @PathVariable("queueName") String queueName) {
        try {
            bigQueueManagementService.deleteQueue(queueDir, queueName);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            String message = String.format("Got exception trying to delete queue for dir [%s] queue [%s]. Error [%s]", queueDir, queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }
}
