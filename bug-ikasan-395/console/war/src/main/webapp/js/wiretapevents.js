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

