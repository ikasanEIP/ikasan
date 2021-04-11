package org.ikasan.component.endpoint.amazon.s3.client;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to show a progress bar of the upload in the log
 */
public class TransferManagerProgressLogger {

    private static Logger logger = LoggerFactory.getLogger(TransferManagerProgressLogger.class);

    /**
     * Shows the transfer progress
     *
     * @param xfer the transfer object
     */
    public static void showTransferProgress(Transfer xfer) {
        String currentProgress = printProgressBar(0.0);
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            double pct = progress.getPercentTransferred();
            String progressBar = printProgressBar(pct);
            if (!progressBar.equals(currentProgress)){
                logger.info(xfer.getDescription() + " : " + progressBar);
                currentProgress = progressBar;
            }
        } while (!xfer.isDone());

    }

    private static String printProgressBar(double pct) {
        final int bar_size = 20;
        final String empty_bar =  "                    ";
        final String filled_bar = "####################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        return String.format("  [%s%s]", filled_bar.substring(0, amt_full),
            empty_bar.substring(0, bar_size - amt_full));
    }


}
