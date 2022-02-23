package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduled.service.MonitoringFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequestMapping("/rest")
@RestController
public class LogFileStreamApplication
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(LogFileStreamApplication.class);

    @RequestMapping(method = RequestMethod.GET,
                    value = "/logs",
                    produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public SseEmitter streamLogFile(@RequestParam("fullFilePath") String fullFilePath) throws IOException {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        MonitoringFileService monitoringFileService = new MonitoringFileService(fullFilePath,emitter);

        return emitter;
    }

}
