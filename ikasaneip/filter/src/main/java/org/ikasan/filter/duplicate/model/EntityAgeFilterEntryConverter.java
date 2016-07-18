package org.ikasan.filter.duplicate.model;

import com.ximpleware.*;
import java.text.*;
import java.util.Date;

/**
 * Created by stewmi on 09/07/2016.
 */
public class EntityAgeFilterEntryConverter implements FilterEntryConverter<String>
{
    private String entityIdentifierXpath;
    private String entityLastUpdatedXpath;
    private String datePattern;
    private String clientId;

    public EntityAgeFilterEntryConverter(String entityIdentifierXpath, String entityLastUpdatedXpath,
                                         String datePattern, String clientId)
    {
        this.entityIdentifierXpath = entityIdentifierXpath;
        if(this.entityIdentifierXpath == null || this.entityIdentifierXpath.isEmpty())
        {
            throw  new IllegalArgumentException("entityIdentifierXpath cannot be null or an empty String!");
        }
        this.entityLastUpdatedXpath = entityLastUpdatedXpath;
        if(this.entityLastUpdatedXpath == null || this.entityLastUpdatedXpath.isEmpty())
        {
            throw  new IllegalArgumentException("entityLastUpdatedXpath cannot be null or an empty String!");
        }
        this.datePattern = datePattern;
        if(this.datePattern == null || this.datePattern.isEmpty())
        {
            throw  new IllegalArgumentException("datePattern cannot be null or an empty String!");
        }
        this.clientId = clientId;
        if(this.clientId == null || this.clientId.isEmpty())
        {
            throw  new IllegalArgumentException("clientId cannot be null or an empty String!");
        }
    }

    @Override
    public FilterEntry convert(String message) throws FilterEntryConverterException
    {
        FilterEntry result = null;
        try
        {
            VTDGen vg = new VTDGen();
            vg.setDoc(message.getBytes());

            vg.parse(true);

            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath(this.entityIdentifierXpath);
            String entityIdentifier = ap.evalXPathToString();

            ap.resetXPath();
            ap.selectXPath(entityLastUpdatedXpath);

            String entityLastUpdated = ap.evalXPathToString();

            DateFormat df = new SimpleDateFormat(this.datePattern);
            Date date =  df.parse(entityLastUpdated);

            result = new DefaultFilterEntry(entityIdentifier.hashCode(),
                    this.clientId, new Long(date.getTime()).toString(), 30);
        }
        catch (Exception e)
        {
            throw new FilterEntryConverterException(e);
        }

        return result;
    }
}
