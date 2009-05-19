/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

/**
 * Common XML Transformer provides the base interface for all XML Transformation.
 *
 * @author Jeff Mitchell
 */  
public interface CommonXMLTransformer
{ 
    /**
     * Transform document to String
     * 
     * @param document
     * @return String representing transformed Document
     * @throws IOException
     * @throws TransformerException
     */
    public String toString(Document document)
        throws IOException, TransformerException;
    
    /**
     * Transform Document to File
     * 
     * @param document
     * @throws IOException
     * @throws TransformerException
     */
    public void toFile(Document document)
        throws IOException, TransformerException;
    
    /**
     * Transform document to XML file
     * @param document
     * @param xmlFileName
     * @throws IOException
     * @throws TransformerException
     */
    public void toFile(Document document, String xmlFileName)
        throws IOException, TransformerException;

    /**
     * Configure the output properties of the transformer
     * @param name
     * @param value
     */
    public void setOutputProperty(String name, String value);
} 