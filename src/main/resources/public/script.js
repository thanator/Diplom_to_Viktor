$(function () {

    $('.date-picker').each(function () {
        $(this).datepicker();
    });

    $(document).delegate('.new-answer', 'click', function () {
        $p = $(this).parent().parent().find('.answers-form__list');
        var id = Math.random();
        $p.append(
            '<div class="checkbox">\
                <input type="text" name="answer_text_new' + id + '">\
                &nbsp;\
                <a href="#" class="remove-answer"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>\
            </div>'
        ).focus()
    });
    $(document).delegate('.edit-answer', 'click', function () {
        $p = $(this).parent();
        $p.find('.answer-text').remove();
        $p.find('label').remove();
        $p.find('input[type=hidden]').attr('type', 'text').focus();
        $(this).remove();
    });
    $(document).delegate('.remove-answer', 'click', function () {
        $(this).parent().remove();
    });

});