/*
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
 * =============================================================================
 */

/*
 * JQuery code
 * 
 * Isn't enabled unless the document is 'ready' (e.g. the DOM is fully loaded)
 */
$(document).ready(
		function()
		{
			if ($('#pointToPointFlowProfileSearch').val() == "true")
			{
				// $('#pointToPointFlowProfileCheckboxes').show(100);
				$('#flowCheckboxes').show(100);
				$('#moduleCheckboxes').hide();
			}
			else
			{
				// $('#pointToPointFlowProfileCheckboxes').hide(100);
				$('#flowCheckboxes').hide(100);
				$('#moduleCheckboxes').show();
			}

			/* jquery date picker assistance */
			$("#fromDateString").datepicker( {
				dateFormat : 'dd/mm/yy'
			});
			$("#untilDateString").datepicker( {
				dateFormat : 'dd/mm/yy'
			});

			/*
			 * If the fromDateString changes then set the fromTimeString to be a
			 * default value
			 */
			$("#fromDateString").change( function() {
				$("#fromTimeString").val('00:00:00');
			});

			/*
			 * If the untilDateString changes then set the untilTimeString to be
			 * a default value
			 */
			$("#untilDateString").change( function() {
				$("#untilTimeString").val('00:00:00');
			});

			/*
			 * Shows/hides the searchFields div on clicking the link with an ID
			 * of "showHideSearchForm"
			 */
			$('a#showHideSearchForm').click( function() {
				$('#searchFields').toggle(100);
				if ($('a#showHideSearchForm').text() == "[+]")
				{
					$('a#showHideSearchForm').text("[-]");
				}
				else
				{
					$('a#showHideSearchForm').text("[+]");
				}
				return false;
			});

			/*
			 * Shows/hides the flowCheckboxes or moduleCheckboxes row on clicking one of the 
			 * radio buttons with the name 'searchMode'.
			 * 
			 * Can't simply use jquery toggle() between the two because the show/hide 
			 * searchFields functionality sets the visibility on both rows.
			 */
			$("#modules").mousedown(function(){
				$('#flowCheckboxes').hide();
				$('#moduleCheckboxes').show();
				$('#pointToPointFlowProfileSearch').val("false");
				return false;
			});
				
			$("#flows").mousedown(function(){
				$('#moduleCheckboxes').hide();
				$('#flowCheckboxes').show();
				$('#pointToPointFlowProfileSearch').val("true");
				return false;
			});

			/*
			 * choose text for the show/hide link - can contain HTML (e.g. an
			 * image)
			 */
			var showText = '[+]';
			var hideText = '[-]';

			/*
			 * append show/hide links to the element directly preceding the
			 * element with a class of "toggle"
			 */
			$('.toggle').prev().children().children("span").prepend(
					'<a href="#" class="toggleLink">' + showText + '</a>&nbsp;');

			/* hide all of the elements with a class of 'toggle' */
			$('.toggle').hide();

			/* capture clicks on the toggle links */
			$('a.toggleLink').click( function() {

				/* change the link depending on whether the element is shown or hidden */
				$(this).html($(this).html() == hideText ? showText : hideText);

				/* toggle the display */
				$(this).parent().parent().parent().next('.toggle').toggle();

				/* return false so any link destination is not followed */
				return false;

			});

			/*
			$('#searchModeHelp').hovertip();
			$('#pointToPointFlowProfileIdsHelp').hovertip();
			$('#moduleIdsHelp').hovertip();
			$('#moduleFlowHelp').hovertip();
			$('#componentNameHelp').hovertip();
			$('#eventIdHelp').hovertip();
			$('#payloadIdHelp').hovertip();
			$('#fromDateStringHelp').hovertip();
			$('#toDateStringHelp').hovertip();
			$('#payloadContentHelp').hovertip();
			$('#orderByHelp').hovertip();
			$('#orderAscHelp').hovertip();
			*/
			
			$('#wiretapSearchResults tr:even').addClass('alt');
		});

/*
 * A function to check/uncheck all checkboxes in a form based on which selectAll
 * checkbox has been ticked.
 */
function checkUncheckAll(theElement) {
	var theForm = theElement.form
	var z = 0;
	for (z = 0; z < theForm.length; z++)
	{
		if (theForm[z].type == 'checkbox')
		{
			if (theElement.name == 'pointToPointFlowProfileSelectAll'
					&& theForm[z].name == 'pointToPointFlowProfileIds')
			{
				theForm[z].checked = theElement.checked;
			}
			if (theElement.name == 'moduleSelectAll'
					&& theForm[z].name == 'moduleIds')
			{
				theForm[z].checked = theElement.checked;
			}
		}
	}
}

