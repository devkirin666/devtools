'use strict';

const utils = (function() {
    const url = (function() {
        return {
            parsePathVariables: function (uri) {
                const result = [];
                const parts = uri.split("/");
                for(let idx in parts) {
                    const part = parts[idx];
                    if(/^{.*}$/.test(part)) {
                        result.push(part.substring(1, part.length - 1));
                    }
                }
                return result;
            }
        }
    })();
    const http = (function() {
        return {
            plainHeaderAsMultiValue: function(headers) {
                const result = {}
                for(let key in headers) {
                    const headerValue = headers[key];
                    let multiValue;
                    if(Array.isArray(headerValue)) {
                        multiValue = [];
                        for(let idx in headerValue) {
                            multiValue = multiValue.concat(headerValue[idx].split("\n"));
                        }
                    } else if(typeof headerValue === 'string') {
                        multiValue = headerValue.split("\n");
                    }
                    result[key] = multiValue;
                }
                return result;
            }
        }
    })();
    return {
        url: url,
        http: http,
    }
})();