/*

 You can now create a spinner using any of the variants below:

 $("#el").spin(); // Produces default Spinner using the text color of #el.
 $("#el").spin("small"); // Produces a 'small' Spinner using the text color of #el.
 $("#el").spin("large", "white"); // Produces a 'large' Spinner in white (or any valid CSS color).
 $("#el").spin({ ... }); // Produces a Spinner using your custom settings.

 $("#el").spin(false); // Kills the spinner.

 */
(function($) {
    $.fn.spin = function(opts, color) {
        var presets = {
            "tiny": { lines: 8, length: 2, width: 2, radius: 3 },
            "small": { lines: 16, length: 0, width: 8, radius: 32 },
            "large": { lines: 16, length: 0, width: 8, radius: 32 }
        };
        if (Spinner) {
            return this.each(function() {
                var $this = $(this),
                    data = $this.data();

                if (data.spinner) {
                    data.spinner.stop();
                    delete data.spinner;
                }
                if (opts !== false) {
                    if (typeof opts === "string") {
                        if (opts in presets) {
                            opts = presets[opts];
                        } else {
                            opts = {};
                        }
                        if (color) {
                            opts.color = color;
                        }
                    }
                    data.spinner = new Spinner($.extend({color: $this.css('color')}, opts)).spin(this);
                }
            });
        } else {
            throw "Spinner class not available.";
        }
    };
})(jQuery);