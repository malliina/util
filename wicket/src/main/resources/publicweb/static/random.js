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
            liveDrag: true,
            hoverCursor: "col-resize",
            dragCursor: "col-resize",
            postbackSafe: true
        });
    });

    var pressed = false, start, startX, startWidth;

    $("table.resizable th").mousedown(function (e) {
        start = $(this);
        pressed = true;
        startX = e.pageX;
        startWidth = $(this).width();
        $(start).addClass("resizing");
    });

    $(document).mousemove(function (e) {
        if (pressed) {
            $(start).width(startWidth + (e.pageX - startX));
        }
    });

    $(document).mouseup(function () {
        if (pressed) {
            $(start).removeClass("resizing");
            pressed = false;
        }
    });
});
