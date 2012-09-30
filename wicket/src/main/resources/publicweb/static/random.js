$(function () { //shorthand for document.ready
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
});
