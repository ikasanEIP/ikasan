package org.ikasan.component.endpoint.mongo.encoding;

import net.sf.json.JSONNull;

import org.apache.log4j.Logger;
import org.bson.Transformer;

/**
 * Transforms net.sf.json.JSONNull objects to null, so can be encoded by the BSONEncoder
 * This a work-around for how the BSONEncoder is unable to serialise instances of these objects
 * 
 * @author Ikasan Development Team
 */
public class BsonEncodingJsonNullToNullTransformer implements Transformer
{

    /** logger instance */
    private static Logger logger = Logger.getLogger(BsonEncodingJsonNullToNullTransformer.class);

    /**
     * 
     */
    @Override
    public Object transform(Object o)
    {
        Object transformed = o;
        if (o instanceof JSONNull){
            logger.debug("Got a net.sf.json.JSONNull value will transform to a null value for handling by the BSONEncoder");
            return null;
        }
        else {
            logger.warn(String.format("The object to be transformed [%1$s] is not a type net.sf.json.JSONNull class ensure this transformer is mapped to this class", o));
        }
        return transformed;
    }
    
    @Override
    public String toString()
    {
        return "BsonEncodingJsonNullToNullTransformer []";
    }

}
