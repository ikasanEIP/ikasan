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
package org.ikasan.mapping.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class SetProducer
{	
	/**
	 * This method takes the parent set and produces a series of unique child subsets
	 * of the size defined.
	 * 
	 * @param parentSet
	 * @param setSize
	 * @return
	 */
	public static Set<Set<String>> combinations(List<String> parentSet, int setSize) 
	{

	    Set<Set<String>> allCombos = new HashSet<Set<String>> ();
	    // base cases for recursion
	    if (setSize == 0) {
	        // There is only one combination of size 0, the empty team.
	        allCombos.add(new HashSet<String>());
	        return allCombos;
	    }
	    if (setSize > parentSet.size()) {
	        // There can be no teams with size larger than the group size,
	        // so return allCombos without putting any teams in it.
	        return allCombos;
	    }

	    // Create a copy of the group with one item removed.
	    List<String> groupWithoutX = new ArrayList<String> (parentSet);
	    String x = groupWithoutX.remove(groupWithoutX.size()-1);

	    Set<Set<String>> combosWithoutX = combinations(groupWithoutX, setSize);
	    Set<Set<String>> combosWithX = combinations(groupWithoutX, setSize-1);
	    for (Set<String> combo : combosWithX) 
	    {
	        combo.add(x);
	    }
	    allCombos.addAll(combosWithoutX);
	    allCombos.addAll(combosWithX);
	    return allCombos;
	}

}
