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
package org.ikasan.security.model;

import java.security.Principal;
import java.util.Date;
import java.util.Set;

 /**
  *
  * @author Ikasan Development Team
  */
 public class IkasanPrincipalLite implements Principal
 {
     private Long id;
     private String name;
     private String type;
     private String description;

     /** The data time stamp when an instance was first created */
     private Date createdDateTime;

     /** The data time stamp when an instance was last updated */
     private Date updatedDateTime;

     /**
      * Default constructor
      */
     public IkasanPrincipalLite()
     {
         long now = System.currentTimeMillis();
         this.createdDateTime = new Date(now);
         this.updatedDateTime = new Date(now);
     }

     /**
      * @return the id
      */
     public Long getId()
     {
         return id;
     }

     /**
      * @param id the id to set
      */
     public void setId(Long id)
     {
         this.id = id;
     }

     /**
      * @return the name
      */
     public String getName()
     {
         return name;
     }

     /**
      * @param name the name to set
      */
     public void setName(String name)
     {
         this.name = name;
     }

     /**
      * @return the type
      */
     public String getType()
     {
         return type;
     }

     /**
      * @param type the type to set
      */
     public void setType(String type)
     {
         this.type = type;
     }

     /**
      * @return the createdDateTime
      */
     public Date getCreatedDateTime()
     {
         return createdDateTime;
     }

     /**
      * @param createdDateTime the createdDateTime to set
      */
     public void setCreatedDateTime(Date createdDateTime)
     {
         this.createdDateTime = createdDateTime;
     }

     /**
      * @return the updatedDateTime
      */
     public Date getUpdatedDateTime()
     {
         return updatedDateTime;
     }

     /**
      * @param updatedDateTime the updatedDateTime to set
      */
     public void setUpdatedDateTime(Date updatedDateTime)
     {
         this.updatedDateTime = updatedDateTime;
     }


     /**
      * @return the description
      */
     public String getDescription()
     {
         return description;
     }

     /**
      * @param description the description to set
      */
     public void setDescription(String description)
     {
         this.description = description;
     }


     /* (non-Javadoc)
      * @see java.lang.Object#hashCode()
      */
     @Override
     public int hashCode()
     {
         final int prime = 31;
         int result = 1;
         result = prime * result
                 + ((createdDateTime == null) ? 0 : createdDateTime.hashCode());
         result = prime * result
                 + ((description == null) ? 0 : description.hashCode());
         result = prime * result + ((id == null) ? 0 : id.hashCode());
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((type == null) ? 0 : type.hashCode());
         result = prime * result
                 + ((updatedDateTime == null) ? 0 : updatedDateTime.hashCode());
         return result;
     }

     /* (non-Javadoc)
      * @see java.lang.Object#equals(java.lang.Object)
      */
     @Override
     public boolean equals(Object obj)
     {
         if (this == obj)
             return true;
         if (obj == null)
             return false;
         if (getClass() != obj.getClass())
             return false;
         IkasanPrincipalLite other = (IkasanPrincipalLite) obj;
         if (createdDateTime == null)
         {
             if (other.createdDateTime != null)
                 return false;
         } else if (!createdDateTime.equals(other.createdDateTime))
             return false;
         if (description == null)
         {
             if (other.description != null)
                 return false;
         } else if (!description.equals(other.description))
             return false;
         if (id == null)
         {
             if (other.id != null)
                 return false;
         } else if (!id.equals(other.id))
             return false;
         if (name == null)
         {
             if (other.name != null)
                 return false;
         } else if (!name.equals(other.name))
             return false;
         if (type == null)
         {
             if (other.type != null)
                 return false;
         } else if (!type.equals(other.type))
             return false;
         if (updatedDateTime == null)
         {
             if (other.updatedDateTime != null)
                 return false;
         } else if (!updatedDateTime.equals(other.updatedDateTime))
             return false;
         return true;
     }

     /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
     @Override
     public String toString()
     {
         return "IkasanPrincipal [id=" + id + ", name=" + name + ", type="
                 + type + ", description=" + description
                 + ", createdDateTime=" + createdDateTime + ", updatedDateTime="
                 + updatedDateTime + "]";
     }
 }
