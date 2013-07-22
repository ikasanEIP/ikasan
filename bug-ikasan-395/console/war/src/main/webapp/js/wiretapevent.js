/*
 * JQuery code
 * 
 * Isn't enabled unless the document is 'ready' (e.g. the DOM is fully loaded)
 */
$(document).ready(
	function()
	{
		// TODO These aren't executing, not sure why
		/*
		$('#wiretapNavigationHelp').hovertip();
		$('#wiretapEventDownloadHelp').hovertip();
		$('#wiretapEventNativeFormatHelp').hovertip();
		*/

		/* 
		 * We focus on the text area before we execute the auto expanding 
		 * text box so that the auto expanding works in IE6
		 */ 
		$('#payloadContentTextArea').focus();
		$('#payloadContentTextArea').simpleautogrow();
		
	});