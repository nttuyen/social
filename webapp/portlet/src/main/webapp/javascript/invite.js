(function($, selectize) {
    var invite = {
        build: function(selector, url) {
            $('#' + selector).selectize({
                plugins: ['remove_button', 'restore_on_backspace'],
                maxItems: null,
                valueField: 'value',
                labelField: 'text',
                searchField: ['text'],
                load: function(query, callback) {
                    $.ajax({
                        type: "GET",
                        url: url,
                        data: { nameToSearch : query },
                        complete: function(jqXHR) {
                            if(jqXHR.readyState === 4) {
                                var json = $.parseJSON(jqXHR.responseText)
                                if (json.options != null) {
                                    callback(json.options);
                                }
                            }
                        }
                    });
                },
                create: true
            });
        }
    };

    return invite;
})($, selectize);