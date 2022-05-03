package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration.ContextualisedFileConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextParametersCacheUtil;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.List;

public class ContextualisedFileMessageProvider extends FileMessageProvider {


    @Override
    public List<File> invoke(JobExecutionContext context)
    {
        createFileMatchers();

        return super.invoke(context);
    }

    /** replacing contextual param with correct value in the filenames */
    private void createFileMatchers() {
        super.fileMatchers.clear();
        for(String filename : super.fileConsumerConfiguration.getFilenames())
        {
            super.fileMatchers.add( getFileMatcher(ContextParametersCacheUtil
                .resolveContextualPlaceholderParam(((ContextualisedFileConsumerConfiguration)fileConsumerConfiguration).getContextId(), filename)));
        }
    }
}
