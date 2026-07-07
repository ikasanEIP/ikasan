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
package org.ikasan.component.validator.xml;

import java.util.List;

/**
 * Configuration bean for the SchematronValidator
 */
public class XMLValidatorConfiguration
{
    /**
     * Option to skip the validation, defaults to false
     */
    private boolean skipValidation = false;

    /**
     * Option to throw an exception on any validation failure, defaults to false
     */
    private boolean throwExceptionOnValidationFailure = false;

    /**
     * Option to return ValidationResult object as result of validation, defaults to false
     */
    private boolean returnValidationResult = false;

    /**
     * Represents the URL of the catalog used for configuring schema validation.
     * This field is used to specify the location of the catalog file, which can
     * contain mappings for XML schemas and other related resources to facilitate
     * validation processes.
     */
    private String catalogUrl;

    /**
     * A collection of schema locations used for validation purposes.
     * Each location in the list represents a URI or path pointing to
     * an XML Schema definition (XSD). These schemas define the structure
     * and constraints for XML documents to be validated.
     *
     * This field is typically used as part of a configuration for XML validation
     * and can accommodate multiple schema definitions to support validation
     * of documents against multiple namespaces or schemas.
     */
    private List<String> schemaLocations;

    /**
     * Returns whether validation should be skipped.
     *
     * @return true if validation is set to be skipped, false otherwise
     */
    public boolean isSkipValidation()
    {
        return skipValidation;
    }

    /**
     * Sets the skipValidation flag, which determines whether validation should be skipped.
     *
     * @param skipValidation a boolean value indicating whether validation should be skipped.
     *                        If true, validation will be bypassed; if false, validation will occur.
     */
    public void setSkipValidation(boolean skipValidation)
    {
        this.skipValidation = skipValidation;
    }

    /**
     * Returns whether an exception should be thrown on validation failure.
     *
     * @return {@code true} if exceptions are to be thrown on validation failures,
     *         {@code false} otherwise.
     */
    public boolean isThrowExceptionOnValidationFailure()
    {
        return throwExceptionOnValidationFailure;
    }

    /**
     * Sets whether an exception should be thrown on a validation failure.
     *
     * @param throwExceptionOnValidationFailure if true, an exception will be thrown
     *        when a validation failure occurs; if false, validation failures will
     *        not throw exceptions.
     */
    public void setThrowExceptionOnValidationFailure(boolean throwExceptionOnValidationFailure)
    {
        this.throwExceptionOnValidationFailure = throwExceptionOnValidationFailure;
    }

    /**
     * Determines whether the validation process will return a ValidationResult object.
     *
     * @return true if the validation process is configured to return a ValidationResult object,
     *         false otherwise.
     */
    public boolean isReturnValidationResult()
    {
        return returnValidationResult;
    }

    /**
     * Sets the flag indicating whether the validation process should return a
     * {@code ValidationResult} object as its result.
     *
     * @param returnValidationResult a boolean value where {@code true} indicates
     *                               that a {@code ValidationResult} object should
     *                               be returned, and {@code false} indicates it should not.
     */
    public void setReturnValidationResult(boolean returnValidationResult)
    {
        this.returnValidationResult = returnValidationResult;
    }

    /**
     * Retrieves the catalog URL used for validation configurations.
     *
     * @return the catalog URL as a String, or null if not set.
     */
    public String getCatalogUrl()
    {
        return catalogUrl;
    }

    /**
     * Sets the catalog URL to be used for schema validation configuration.
     *
     * @param catalogUrl the URL of the catalog to be set
     */
    public void setCatalogUrl(String catalogUrl)
    {
        this.catalogUrl = catalogUrl;
    }

    /**
     * Retrieves the list of schema locations configured for validation.
     *
     * @return a List of Strings representing the schema locations.
     */
    public List<String> getSchemaLocations() {
        return schemaLocations;
    }

    /**
     * Sets the schema locations to be used for XML validation.
     *
     * @param schemaLocations a list of schema location strings, where each string represents
     *                        the location of an XML schema to be used for validation.
     */
    public void setSchemaLocations(List<String> schemaLocations) {
        this.schemaLocations = schemaLocations;
    }
}
