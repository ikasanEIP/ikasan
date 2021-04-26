package org.ikasan.component.endpoint.amazon.s3.client;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to show a progress percentage of the upload in the log
 */
public class TransferManagerProgressLogger {

    private static Logger logger = LoggerFactory.getLogger(TransferManagerProgressLogger.class);

    /**
     * Shows the transfer progress
     *
     * @param xfer the transfer object
     */
    public static void showTransferProgress(Transfer xfer) {
        String currentProgress = "0.0";
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            TransferProgress progress = xfer.getProgress();
            double pct = progress.getPercentTransferred();
            String progressStr = "" + pct;
            if (!progressStr.equals(currentProgress)){
                logger.info(xfer.getDescription() + " : " + progressStr + " %");
                currentProgress = progressStr;
            }
        } while (!xfer.isDone());

    }




}
