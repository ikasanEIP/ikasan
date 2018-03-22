package org.ikasan.replay.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikasan Development Team on 05/11/2017.
 */
public class BulkReplayResponse
{
    private List<ReplayResponse> replayResponses = new ArrayList<ReplayResponse>();
    private boolean success = true;

    public void addReplayResponse(ReplayResponse replayResponse)
    {
        if(!replayResponse.isSuccess())
        {
            success = replayResponse.isSuccess();
        }
        this.replayResponses.add(replayResponse);
    }

    public List<ReplayResponse> getReplayResponses()
    {
        return replayResponses;
    }

    public boolean isSuccess()
    {
        return success;
    }

}
