$(function () { //shorthand for document.ready
    "use strict";
    function togglevisibility(elem) {
        if (elem.is(':hidden')) {
            elem.show();
        } else {
            elem.hide();
        }
    }

    $("#buu").click(function () {
        alert('clicked');
    });

    $('#link').tooltip();

    $('#vistoggle').click(function () {
        togglevisibility($('#mytext'));
    });
    // Table column resizers
    $(function () {
        $("table.colresizable").colResizable({
            liveDrag:true,
            hoverCursor:"col-resize",
            dragCursor:"col-resize",
            postbackSafe:true
        });
    });
});
