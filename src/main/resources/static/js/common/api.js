'use strict';

const api = (function () {
    const CSRF_TOKEN = $('meta[name="csrf-token"]').attr('content');
    const CSRF_HEADER = $('meta[name="csrf-header"]').attr('content');

    $(document).ready(function() {
        if(!CSRF_HEADER || !CSRF_TOKEN) {
            notify.warn("Not found CSRF token.");
        }
    });

    function apiCall(uri, methodOpts, data, onSuccess, onFail) {
        const opts = Object.assign({
            global: false,
            url: uri,
            data: data,
            beforeSend: function (xhr) {
                if(!!CSRF_HEADER && !!CSRF_TOKEN) {
                    xhr.setRequestHeader(CSRF_HEADER, CSRF_TOKEN);
                }
            },
        }, methodOpts);

        try {
            $.ajax(opts)
                .done(function(response, textStatus, xhr) {
                    if(onSuccess) {
                        onSuccess(response, xhr);
                    }
                })
                .fail(function(xhr) {
                    switch (xhr.status) {
                        case 200:
                        case 201:
                            if(onSuccess) {
                                onSuccess(xhr.responseText, xhr);
                            }
                            break;
                        default:
                            if(xhr.status > 0) {
                                notify.apiError(JSON.parse(xhr.responseText));
                            } else {
                                notify.alert("Response is not succeed", xhr.responseText);
                            }
                            if(onFail) {
                                onFail(xhr);
                            }
                            break;

                    }
                });
        } catch (e) {
            notify.alert("API Error", e, opts.type.toUpperCase() + " - " + uri);
            if(onFail) {
                onFail(e);
            }
        }
    }

    const METHOD_MAPPING = {
        GET: {
            type: 'get',
            dataType: null,
            contentType: null
        },
        POST: {
            type: 'post',
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        },
        PUT: {
            type: 'put',
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        },
        PATCH: {
            type: 'patch',
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        },
        DELETE: {
            type: 'delete',
            dataType: "text",
            contentType: "application/json; charset=utf-8"
        }
    }

    function toBody(data) {
        return data ? JSON.stringify(data) : null;
    }
    return {
        get: function (uri, param, successCallback, failCallback) {
            apiCall(uri, METHOD_MAPPING.GET, param, successCallback, failCallback)
        },
        post: function (uri, body, successCallback, failCallback) {
            apiCall(uri, METHOD_MAPPING.POST, toBody(body), successCallback, failCallback)
        },
        put: function (uri, body, successCallback, failCallback) {
            apiCall(uri, METHOD_MAPPING.PUT, toBody(body), successCallback, failCallback)
        },
        patch: function (uri, body, successCallback, failCallback) {
            apiCall(uri, METHOD_MAPPING.PATCH, toBody(body), successCallback, failCallback)
        },
        delete: function (uri, successCallback, failCallback) {
            apiCall(uri, METHOD_MAPPING.DELETE, null, successCallback, failCallback)
        }
    }
})();