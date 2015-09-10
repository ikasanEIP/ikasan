/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.converter.xml;

/**
 * Configuration for an XML conversion validation.
 * 
 * @author Ikasan Development Team
 */
public class XmlConfiguration
{
    /**
     * determines whether we let the component fail if the initial
     * setting and loading of configuration fails.
     */
    private boolean fastFailOnConfigurationLoad;

    /** actual schema against which validation will occur */
    private String schema;

    /** schema location as put in the root attribute of the generated XML */
    private String schemaLocation;

    /** is this a no namespace schema */
    private boolean noNamespaceSchema;

    /** whether to validate the generated XML against the schema */
    private boolean validate;

    /** should we use namespace prefixes */
    private boolean useNamespacePrefix;

    /** optionally set root name (QNAME) */
    private String rootName;

    /** optionally set root class name (QNAME) */
    private String rootClassName;

    /** whether to route an XML validation failure to the next component (true) or throw an exception and rollback (false) */
    private boolean routeOnValidationException;

    /**
     * Whether to route an XML validation failure to the next component (true) or throw an exception and rollback (false)
     * @return
     */
    public boolean isRouteOnValidationException()
    {
        return routeOnValidationException;
    }

    /**
     * Setter as to whether the component should route or throw an exception on XML validation failure.
     * @param routeOnValidationException
     */
    public void setRouteOnValidationException(boolean routeOnValidationException)
    {
        this.routeOnValidationException = routeOnValidationException;
    }

    /**
     * Get the root XSD attribute value
     * @return
     */
    public String getSchemaLocation()
    {
        return schemaLocation;
    }

    /**
     * Set the root XSD attribute value
     * @param schemaLocation
     */
    public void setSchemaLocation(String schemaLocation)
    {

        this.schemaLocation = schemaLocation;
    }

    /**
     * Set an alternate schema to validate against.
     * Commonly used if the XSD is picked up from the classpath rather than an http resource URL
     * @return
     */
    public String getSchema()
    {
        return schema;
    }

    /**
     * Get an alternate schema to validate against
     * Commonly used if the XSD is picked up from the classpath rather than an http resource URL
     * @param schema
     */
    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    /**
     * Getter to determine whether to validate the XML against the XSD
     * @return
     */
    public boolean isValidate()
    {
        return validate;
    }

    /**
     * Setter to determine whether to validate the XML against the XSD
     * @param validate
     */
    public void setValidate(boolean validate)
    {
        this.validate = validate;
    }

    /**
     * Getter for any roo name override
     * @return
     */
    public String getRootName() {
        return rootName;
    }

    /**
     * Allow override of the root document name
     * @param rootName
     */
    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    /**
     * Getter for any root class name override
     * @return
     */
    public String getRootClassName() {
        return rootClassName;
    }

    /**
     * Setter for root class name override
     * @param rootClassName
     */
    public void setRootClassName(String rootClassName) {
        this.rootClassName = rootClassName;
    }

    /**
     * If schema specified then is this a noNamespaceSchema
     * @return
     */
    public boolean isNoNamespaceSchema() {
        return noNamespaceSchema;
    }

    /**
     * Setter for noNamespaceSchema if schema specified
     * @param noNamespaceSchema
     */
    public void setNoNamespaceSchema(boolean noNamespaceSchema) {
        this.noNamespaceSchema = noNamespaceSchema;
    }

    /**
     * Getter for use namespace prefix on marshaller
     * @return
     */
    public boolean isUseNamespacePrefix() {
        return useNamespacePrefix;
    }

    /**
     * Setter for namespace prefix on marshaller
     * @param useNamespacePrefix
     */
    public void setUseNamespacePrefix(boolean useNamespacePrefix) {
        this.useNamespacePrefix = useNamespacePrefix;
    }

    public boolean isFastFailOnConfigurationLoad() {
        return fastFailOnConfigurationLoad;
    }

    public void setFastFailOnConfigurationLoad(boolean fastFailOnConfigurationLoad) {
        this.fastFailOnConfigurationLoad = fastFailOnConfigurationLoad;
    }
}
