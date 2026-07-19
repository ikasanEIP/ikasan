package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.exception.MoveFileBrokerException;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MoveFileBroker implements Broker<FileWatcherJobEvent, FileWatcherJobEvent> {

    private static Logger logger = LoggerFactory.getLogger(MoveFileBroker.class);
    public static SimpleDateFormat ARCHIVE_FILE_DATE_FORMATTER = new SimpleDateFormat("YYYYddMM_hhmmss");

    public static final String MOVE_DIRECTORY_PATTERN = "moveDirectoryPattern";
    public static final String CORRELATING_IDENTIFIER = "correlatingIdentifier";

    @Override
    public FileWatcherJobEvent invoke(FileWatcherJobEvent fileWatcherJobEvent) throws EndpointException {
        if(fileWatcherJobEvent.isDryRun()) {
            return fileWatcherJobEvent;
        }

        if(fileWatcherJobEvent.getCorrelatedFileList() != null && fileWatcherJobEvent.getCorrelatedFileList().getFileList() != null) {
            try {
                String moveDirectory = this.resolveMoveDirectory(fileWatcherJobEvent);

                for (File file : fileWatcherJobEvent.getCorrelatedFileList().getFileList()) {
                    if (moveDirectory != null && !moveDirectory.isEmpty()
                        && !moveDirectory.equals(".")) {
                        logger.info(String.format("Moving file[%s] to directory[%s]", file.getAbsolutePath(), moveDirectory));
                        File destDir = new File(moveDirectory);

                        if (file.getParentFile().equals(destDir)) {
                            logger.info(String.format("Not moving file[%s] to directory[%s], as the source and destination directories are the same!"
                                , file.getAbsolutePath(), moveDirectory));
                        } else {
                            File destFile = new File(destDir, file.getName());
                            if (destFile.exists()) {
                                this.renameArchiveFile(destFile);
                            }
                            FileUtils.moveFileToDirectory(new File(file.getAbsolutePath()), destDir, true);
                        }
                    }
                }
            } catch (Exception e) {
                throw new MoveFileBrokerException(String.format("Error moving fileWatcherJobEvent to dir %s. %s"
                    , fileWatcherJobEvent.getMoveDirectory(), e.getMessage()), e);
            }
        }

        return fileWatcherJobEvent;
    }

    /**
     * Resolve the actual move (archive) directory to use, evaluating the configured SpEL expression
     * against the raw moveDirectory pattern when one has been configured. Falls back to the raw
     * moveDirectory value unchanged when no SpEL expression is configured, for backward compatibility
     * with existing jobs.
     *
     * @param fileWatcherJobEvent the event carrying the raw moveDirectory and optional SpEL expression
     * @return the resolved move directory
     */
    private String resolveMoveDirectory(FileWatcherJobEvent fileWatcherJobEvent) {
        String moveDirectory = fileWatcherJobEvent.getMoveDirectory();

        if (fileWatcherJobEvent.getMoveDirectorySpelExpression() != null) {
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setVariable(MOVE_DIRECTORY_PATTERN, fileWatcherJobEvent.getMoveDirectory());
            evaluationContext.setVariable(CORRELATING_IDENTIFIER, fileWatcherJobEvent.getCorrelationIdentifier());

            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(fileWatcherJobEvent.getMoveDirectorySpelExpression());

            moveDirectory = exp.getValue(evaluationContext, String.class);
        }

        return moveDirectory;
    }

    /**
     * Renames the given file by appending a timestamp to its name.
     *
     * @param file the file to be renamed
     * @return the newly renamed File object
     */
    private File renameArchiveFile(File file) {
        String filename;
        if(file.getName().contains(".")) {
            filename = file.getAbsolutePath() + "_" + ARCHIVE_FILE_DATE_FORMATTER.format(new Date());
        }
        else {
            filename = file.getAbsolutePath() + "_" + System.currentTimeMillis();
        }

        File archiveFile = new File(filename);
        file.renameTo(archiveFile);

        return archiveFile;
    }
}
