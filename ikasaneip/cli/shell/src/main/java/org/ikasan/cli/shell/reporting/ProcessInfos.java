package org.ikasan.cli.shell.reporting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProcessInfos
{
    Map<Long,ProcessInfo> processInfos = new HashMap<Long,ProcessInfo>();

    public ProcessInfos add(ProcessInfo processInfo)
    {
        processInfos.put(processInfo.getPid(), processInfo);
        return this;
    }

    public ProcessInfos add(ProcessInfos processInfos)
    {
        for(Map.Entry<Long,ProcessInfo> entry: processInfos.getProcessInfos().entrySet())
        {
            this.processInfos.put(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public Map<Long,ProcessInfo> getProcessInfos()
    {
        return this.processInfos;
    }

    public JSONObject toJSON()
    {
        JSONArray jsonArray = new JSONArray();
        for(Map.Entry<Long,ProcessInfo> entry: processInfos.entrySet())
        {
            jsonArray.put(entry.getValue().toJSON());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Processes", jsonArray);

        return jsonObject;
    }

}
