'use strict';
const $ = jQuery.noConflict();
const BASE_URL = $("meta[name='base-url']").attr("content").replace(/\/$/, "");
const HTTP_STATUS_CODES = {
    '200' : 'OK',
    '201' : 'Created',
    '202' : 'Accepted',
    '203' : 'Non-Authoritative Information',
    '204' : 'No Content',
    '205' : 'Reset Content',
    '206' : 'Partial Content',
    '207' : 'Multi-Status',
    '300' : 'Multiple Choices',
    '301' : 'Moved Permanently',
    '302' : 'Found',
    '303' : 'See Other',
    '304' : 'Not Modified',
    '305' : 'Use Proxy',
    '307' : 'Temporary Redirect',
    '400' : 'Bad Request',
    '401' : 'Unauthorized',
    '402' : 'Payment Required',
    '403' : 'Forbidden',
    '404' : 'Not Found',
    '405' : 'Method Not Allowed',
    '406' : 'Not Acceptable',
    '407' : 'Proxy Authentication Required',
    '408' : 'Request Timeout',
    '409' : 'Conflict',
    '410' : 'Gone',
    '411' : 'Length Required',
    '412' : 'Precondition Failed',
    '413' : 'Request Entity Too Large',
    '414' : 'Request-URI Too Long',
    '415' : 'Unsupported Media Type',
    '416' : 'Requested Range Not Satisfiable',
    '417' : 'Expectation Failed',
    '500' : 'Internal Server Error',
    '501' : 'Not Implemented',
    '502' : 'Bad Gateway',
    '503' : 'Service Unavailable',
    '504' : 'Gateway Timeout',
    '505' : 'HTTP Version Not Supported'
};

const RESPONSE_HEADER_NAMES = [
    "Accept-CH",
    "Access-Control-Allow-Origin",
    "Access-Control-Allow-Credentials",
    "Access-Control-Expose-Headers",
    "Access-Control-Max-Age",
    "Access-Control-Allow-Methods",
    "Access-Control-Allow-Headers",
    "Accept-Patch",
    "Accept-Ranges",
    "Age",
    "Allow",
    "Alt-Svc",
    "Cache-Control",
    "Connection",
    "Content-Disposition",
    "Content-Encoding",
    "Content-Language",
    "Content-Length",
    "Content-Location",
    "Content-MD5",
    "Content-Range",
    "Content-Type",
    "Date",
    "Delta-Base",
    "ETag",
    "Expires",
    "IM",
    "Last-Modified",
    "Link",
    "Location",
    "Pragma",
    "Preference-Applied",
    "Proxy-Authenticate",
    "Public-Key-Pins",
    "Retry-After",
    "Server",
    "Set-Cookie",
    "Strict-Transport-Security",
    "Trailer",
    "Transfer-Encoding",
    "Tk",
    "Upgrade",
    "Vary",
    "Via",
    "Warning",
    "WWW-Authenticate",
    "X-Frame-Options",
];

const asBoolean = function(value) {
    if(value == null || value === 'undefined') {
        return false;
    }

    const valueType = typeof value;
    if(valueType === 'undefined') {
        return false;
    }
    if(valueType === 'boolean') {
        return value;
    }
    if(valueType === 'string') {
        return value.toLowerCase() === 'true' || value === 'on';
    }
    if(valueType === 'number') {
        return value > 0;
    }
    throw new Error("Invalid type for cast to 'boolean'. type = " + valueType);
}

const ifEmpty = function(value, defaultValue) {
    if(value == null || value === 'undefined') {
        return defaultValue;
    }
    return value;
}

const notify = (function() {
    const toast = function(title, content, subTitle, bgClass) {
        $(document).Toasts('create', {
            autohide: true,
            delay: 2000,
            class: bgClass,
            title: title,
            subtitle: subTitle ? subTitle : "",
            body: content
        });
    };

    return {
        info: function (title, content, subTitle) {
            toast(title, content, subTitle, "bg-info");
        },
        alert: function (title, content, subTitle) {
            toast(title, content, subTitle, "bg-danger");
        },
        warn: function (title, content, subTitle) {
            toast(title, content, subTitle, "bg-warning");
        },
        apiError: function(body) {
            const content = "type: " + body.type + "<br/>"
                + "detail: " + body.detail + "<br/>"
                // + "more: " + JSON.stringify(body.more) + "<br/>"
                ;
            toast(body.title, content, body.status + " - " + HTTP_STATUS_CODES[body.status], "bg-danger");
        }
    }
})();

const storage = (function() {
    const put = function(key, value) {
        localStorage.setItem(key, value);
        return value;
    }

    const get = function(key, defaultValue) {
        const result = localStorage.getItem(key);

        if(result == null || result === 'undefined') {
            return defaultValue;
        }
        if(typeof result === 'string' && result === '') {
            return defaultValue;
        }
        return result;
    }

    const clear = function() {
        localStorage.clear();
    }

    const remove = function(key) {
        localStorage.removeItem(key);
    }

    return {
        put: function(key, value) {
            put(key, value);
            return value;
        },
        get: function(key, defaultValue) {
            return get(key, defaultValue);
        },
        append: function(key, value) {
            const result = [];
            const existsValue = get(key);
            if(existsValue != null && existsValue !== 'undefined') {
                if(Array.isArray(existsValue)) {
                    result.concat(existsValue);
                } else {
                    result.push(existsValue);
                }
            }
            result.push(value);
            return put(key, result);
        },
        assign: function(key, value) {
            let result = get(key, {});
            Object.assign(result, value);
            return put(key, result);
        },
        clear: function() {
            clear();
        },
        remove: function(key) {
            remove(key);
        }
    }
})();

(function() {
    const _hide = jQuery.fn.hide;
    const _show = jQuery.fn.show;
    const _val = jQuery.fn.val;

    jQuery.fn.val = function(value) {
        if(this.attr("type") === 'checkbox') {
            if(arguments.length < 1) {
                return "" + this.is(":checked");
            }
            const boolValue = asBoolean(value);
            if("bootstrapToggle" in this) {
                return this.bootstrapToggle(boolValue ? "on" : "off", !!arguments[1]);
            }
            return this.prop("checked", boolValue);
        }
        if(arguments.length > 0) {
            return _val.call(this, value);
        }
        return _val.call(this);
    }

    jQuery.fn.show = function() {
        if(this.hasClass("modal")) {
            return this.modal('show');
        }
        return _show.call(this);
    }

    jQuery.fn.hide = function() {
        if(this.hasClass("modal")) {
            return this.modal('hide');
        }
        return _hide.call(this);
    }

    jQuery.fn.cancel = function(flag) {
        const value = asBoolean(flag);
        if(value) {
            return this.addClass("cancel");
        }
        return this.removeClass("cancel");
    }

    jQuery.fn.toggleName = function() {
        const value = asBoolean(this.val());
        if(value) {
            return this.data("on");
        }
        return this.data("off");
    }

    jQuery.fn.customEvent = function(name, data) {
        const trigger = $.Event(name, {detail: data});
        $(document).trigger(trigger, data);
    }

    jQuery.fn.tableAsMultiValueMap = function(keySelector, valueSelector) {
        const result = {};
        this.find("tr")
            .each(function(idx, item) {
                const $item = $(item);
                const key = $item.find(keySelector).val();
                const value = $item.find(valueSelector).val();
                if(!key) {
                    return true;
                }
                if(!(key in result)) {
                    result[key] = [];
                }
                result[key] = result[key].concat(value.split("\n"));
            });
        return result;
    }

    String.prototype.format = function() {
        const origin = this;
        let formatted = this;
        const args = arguments.length === 1 && typeof arguments[0] === 'object' ? arguments[0] : arguments;
        formatted = formatted.replace(/{[0-9a-zA-Z_\-\.]+}/g, function(match, number) {
            if(origin.charAt(number - 1) === '\\') {
                return match;
            }
            const argVar = match.substring(1, match.length - 1);
            return typeof args[argVar] != 'undefined' ? args[argVar] : match;
        });
        return formatted.replace(/\\{/g, "{");
    }
})();

