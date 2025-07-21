package org.ikasan.module.builder.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

import java.util.List;

public class InstanceOfMethod implements TemplateMethodModelEx {

    /**
     * Executes the method to determine if the given object is an instance of the specified class.
     *
     * @param arguments A list containing two elements: the object to check and the fully qualified class name as a string.
     * @return Returns true if the object is an instance of the specified class; false otherwise.
     * @throws TemplateModelException if the number of arguments is not equal to 2, if the class specified by the name is
     * not found, or if an error occurs during class loading.
     */
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("Wrong arguments");
        }
        
        Object bean = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
        String className = arguments.get(1).toString();
        
        try {
            Class clazz = Class.forName(className);
        
            return clazz.isInstance(bean);
        }
        catch (ClassNotFoundException ex) {
            throw new TemplateModelException("Could not find the class '" + className + "'", ex);
        }
    }
    
}