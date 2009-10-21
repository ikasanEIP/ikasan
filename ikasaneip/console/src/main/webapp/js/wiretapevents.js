/*
 * JQuery code
 * 
 * Isn't enabled unless the document is 'ready' (e.g. the DOM is fully loaded)
 */
$(document).ready(
		function() {
			if ($('#pointToPointFlowProfileSearch').val() == "true") {
				$('#pointToPointFlowProfileCheckboxes').show(100);
				$('#moduleCheckboxes').hide();
			} else {
				$('#pointToPointFlowProfileCheckboxes').hide(100);
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
				if ($('a#showHideSearchForm').text() == "[+]") {
					$('a#showHideSearchForm').text("[-]");
				} else {
					$('a#showHideSearchForm').text("[+]");
				}
				return false;
			});

			/*
			 * Shows/hides the eventSearchPointToPointFlowProfileCheckboxes and
			 * moduleFields divs on clicking the link with an ID of
			 * "toggleSearchMode", can't simply use jquery toggle() between the
			 * two because the show/hide searchFields sets the visibility on
			 * both checkbox sections.
			 */
			$('a#toggleSearchMode').click( function() {
				// Swap to the other search
					if ($('#pointToPointFlowProfileSearch').val() == "true") {
						$('#moduleCheckboxes').show(100);
						$('#pointToPointFlowProfileCheckboxes').hide(100);
						$('#pointToPointFlowProfileSearch').val("false");
					} else {
						$('#moduleCheckboxes').hide(100);
						$('#pointToPointFlowProfileCheckboxes').show(100);
						$('#pointToPointFlowProfileSearch').val("true");
					}

					// Do not follow the link
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
			$('.toggle').prev().append(
					'<a href="#" class="toggleLink">' + showText + '</a>');

			/* hide all of the elements with a class of 'toggle' */
			$('.toggle').hide();

			/* capture clicks on the toggle links */
			$('a.toggleLink').click( function() {

				/* change the link depending on whether the element is shown or hidden */
				$(this).html($(this).html() == hideText ? showText : hideText);

				/*
				 * toggle the display - uncomment the next line for a basic
				 * "accordion" style
				 */

				$(this).parent().next('.toggle').toggle();

				/* return false so any link destination is not followed */
				return false;

			});

			$('#searchFormHelp').hovertip();
			$('#toggleSearchModeHelp').hovertip();
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

		});

/*
 * A function to check/uncheck all checkboxes in a form based on which selectAll
 * checkbox has been ticked.
 */
function checkUncheckAll(theElement) {
	var theForm = theElement.form
	var z = 0;
	for (z = 0; z < theForm.length; z++) {
		if (theForm[z].type == 'checkbox') {
			if (theElement.name == 'pointToPointFlowProfileSelectAll'
					&& theForm[z].name == 'pointToPointFlowProfileIds') {
				theForm[z].checked = theElement.checked;
			}
			if (theElement.name == 'moduleSelectAll'
					&& theForm[z].name == 'moduleIds') {
				theForm[z].checked = theElement.checked;
			}
		}
	}
}
