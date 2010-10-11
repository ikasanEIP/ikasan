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

package org.ikasan.framework.exception.matching;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Implementation of <code>TypeSafeMatcher</code> for matching instances of <code>Throwable</code>
 * by considering the cause of the <code>Throwable</code>
 * 
 * When passed a throwable for matching, this implementation will apply another matcher to both
 * that throwable and its cause. If the 'directly' flag is set to false, then it will also continue recursively
 * up the cause stace of the throwable looking for a match
 * 
 * @author Ikasan Development Team
 *
 */
public class ThrowableCauseMatcher extends TypeSafeMatcher<Throwable> {

	/**
	 * If set to true (default), only the direct cause of the supplied throwable will be considered. 
	 * Otherwise, all causes in the causedBy hierarchy will be considered
	 */
	private boolean directly;
	
	/**
	 * Matcher for matching the cause of the throwable
	 */
	private Matcher<?> causeMatcher;
	
	/**
	 * Constructor
	 * 
	 * @param causeMatcher
	 * @param directly
	 */
	public ThrowableCauseMatcher(Matcher<?> causeMatcher,
			boolean directly) {
		this.causeMatcher = causeMatcher;
		this.directly = directly;
	}


	/**
	 * Constructor
	 * 
	 * @param causeMatcher
	 */
	public ThrowableCauseMatcher(Matcher<?> causeMatcher ) {
		this(causeMatcher, true);
	}

	@Override
	public boolean matchesSafely(Throwable throwable) {
		boolean result = causeMatcher.matches(throwable);

		if (result == false){
			Throwable directCause = throwable.getCause();
			if (directCause!=null){
				result = causeMatcher.matches(directCause);

				if (result==false && !directly){

					Throwable indirectCause = throwable.getCause();
					if (indirectCause!=null){
						result = matchesSafely(indirectCause);
					}
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
	 */
	public void describeTo(Description description) {
		description.appendText("throwable "+(directly?"directly":"indirectly")+" caused by ");
		causeMatcher.describeTo(description);

	}

}
