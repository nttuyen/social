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
                create: true,
                render: {
                    option: function(item, escape) {
                        if (item.avatarUrl == null) {
                            if (item.type == "space") {
                                item.avatarUrl = '/eXoSkin/skin/images/system/SpaceAvtDefault.png';
                            } else {
                                item.avatarUrl = '/eXoSkin/skin/images/system/UserAvtDefault.png';
                            }
                        }
                        return '<div class="option">' +
                        '<img width="20px" height="20px" src="' + item.avatarUrl + '"> ' +
                        escape(item.text) + '</div>';
                    }
                },
                sortField: [{field: 'order'}, {field: '$score'}]
            });
        },

        notify: function(selector, anchor) {
            $(anchor).append($(selector));
        }
    };

    return invite;
})($, selectize);