/*
 * Function that looks at all anchor tags and adds the target=_blank attribute
 * to any that have class="new-window" as an attribute.  This is a method of opening 
 * up external links in a new window/tab that is valid with XHTML Strict.
 */
$(function(){
    $('a.new-window').click(function(){
        window.open(this.href);
        return false;
    });
});