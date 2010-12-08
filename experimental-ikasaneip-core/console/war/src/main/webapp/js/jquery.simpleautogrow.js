/*
 * Simple Auto Expanding Text Area (0.1.2-dev)
 * by Antti Kaihola (antti.kaihola.fi)
 * akaihol+jquery@ambitone.com
 *
 * Copyright (c) 2009 Antti Kaihola (antti.kaihola.fi)
 * Licensed under the MIT and BSD licenses.
 *
 * NOTE: This script requires jQuery to work.  Download jQuery at
 *       www.jquery.com
 */

(function(jQuery) {

    jQuery.fn.simpleautogrow = function() {
        return this.each(function() { new jQuery.simpleautogrow(this); }); };

    jQuery.simpleautogrow = function (e) {
        var self = this;
        var $e = this.textarea = jQuery(e)
            .css({overflow: 'hidden', display: 'block'})
            .bind('focus', function() {
                this.timer = window.setInterval(function() {self.checkExpand(); }, 200); })
            .bind('blur', function() { clearInterval(this.timer); });
        this.border = $e.outerHeight() - $e.innerHeight();
        this.clone = $e.clone().css({position: 'absolute', visibility: 'hidden'}).attr('name', '')
        $e.height(e.scrollHeight + this.border)
            .after(this.clone);
        this.checkExpand(); };

    jQuery.simpleautogrow.prototype.checkExpand = function() {
        var target_height = this.clone[0].scrollHeight + this.border;
        if (this.textarea.outerHeight() != target_height)
            this.textarea.height(target_height + 'px');
        this.clone.attr('value', this.textarea.attr('value')).height(0); };

})(jQuery);