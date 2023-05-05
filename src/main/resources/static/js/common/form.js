'use strict';

const forms = (function() {
    return {
        upsert: function($form, successCallback, failCallback) {
            const id = $($form.attr("data-id-field")).val();
            const body = $form.serializeJSON({checkboxUncheckedValue: "false"});
            if(id) {
                api.put($form.attr("action") + "/" + id, body, successCallback, failCallback)
            } else {
                api.post($form.attr("action"), body, successCallback, failCallback);
            }
        },
        get: function($form, successCallback, failCallback) {
            api.get($form.attr("action"), $form.serialize(), successCallback, failCallback);
        },
    }
})();