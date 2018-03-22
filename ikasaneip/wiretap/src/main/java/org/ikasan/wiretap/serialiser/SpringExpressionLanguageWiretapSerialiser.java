package org.ikasan.wiretap.serialiser;

import org.ikasan.spec.wiretap.WiretapSerialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Convenient way of serialising an event to a string for a Wiretap using the spring el 
 * 
 * @author edwaki
 *
 * @param <T>
 */
public class SpringExpressionLanguageWiretapSerialiser <T> implements WiretapSerialiser<T,String>
{

    private String springExpression;

    private final ExpressionParser springElParser;
    
    private String eventNameInExpression;

    private Expression parseExpression;

    /** Logger */
    private final static Logger logger = LoggerFactory.getLogger(SpringExpressionLanguageWiretapSerialiser.class);

    public SpringExpressionLanguageWiretapSerialiser(String eventNameInExpression, String springExpression){
        springElParser = new SpelExpressionParser();
        this.springExpression = springExpression;
        this.eventNameInExpression = eventNameInExpression;
        parseExpression = springElParser.parseExpression(springExpression);
        
    }

    public SpringExpressionLanguageWiretapSerialiser(String springExpression){
        this(null, springExpression);
    }
    
    @Override
    public String serialise(T source)
    {
        logger.debug("Wiretap Serialising the source message [{}] using serialiser [{}]", source.toString(), this.toString());
        if (eventNameInExpression == null){
            EvaluationContext evaluationContext = new StandardEvaluationContext(source);           
            return parseExpression.getValue(evaluationContext).toString();
        } else {
            EvaluationContext evaluationContext = new StandardEvaluationContext();           
            evaluationContext.setVariable(eventNameInExpression, source);
            return parseExpression.getValue(evaluationContext).toString();
        }
    }
    
    @Override
    public String toString()
    {
        return "SpringExpressionLanguageWiretapSerialiser [springExpression=" + springExpression
                + ", eventNameInExpression=" + eventNameInExpression + "]";
    }

    
}
