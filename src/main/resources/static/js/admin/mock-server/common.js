'use strict';

const BASE_MANAGEMENT_PAGE_URL = $("meta[name='base-page']").attr("content");

const STATUS_CLASS = {
    "2": "success",
    "3": "secondary",
    "4": "warning",
    "5": "danger",
}

const METHOD_CLASS = {
    POST: "success",
    PUT: "info",
    GET: "primary",
    PATCH: "orange",
    DELETE: "danger",
    HEAD: "black",
    OPTIONS: "secondary",
    TRACE: "lightblue",
}

const VALID_RESULT_VALUES = ["ok", "true", "yes", "y", "success"];

const mockServerUtil = (function() {
    const updateColorGroup = function($elem, value) {
        const colorGroup = $elem.data("color-group");
        if(colorGroup) {
            const currentColor = $elem.data("current-color");
            if(currentColor) {
                $elem.removeClass(currentColor);
            }

            switch (colorGroup) {
                case "status":
                    const statusColor = "btn-" + STATUS_CLASS[("" + value).charAt(0)];
                    $elem.addClass(statusColor);
                    $elem.data("current-color", statusColor);
                    break;
                case "method":
                    const methodColor = "btn-" + METHOD_CLASS[value];
                    $elem.addClass(methodColor);
                    $elem.data("current-color", methodColor);
                    break;
                case "result":
                    const resultColor = VALID_RESULT_VALUES.includes(("" + value).toLowerCase()) ? "btn-success" : "btn-danger";
                    $elem.addClass(resultColor);
                    $elem.data("current-color", resultColor);
                    break;
            }
        }
        return $elem;
    }
    return {
        getStatusDetail: function(status) {
            return {
                style: STATUS_CLASS[("" + status).charAt(0)],
                comment: HTTP_STATUS_CODES[status],
            }
        },
        colorGroup: updateColorGroup,
        formToggleView: function(type) {
            if(type == null || type === 'undefined') {
                throw new Error("'type' is required.");
            }
            if(type === 'view') {
                $("[data-form-toggle='edit']").hide();
                return $("[data-form-toggle='view']").show();
            } else {
                $("[data-form-toggle='view']").hide();
                return $("[data-form-toggle='edit']").show();
            }
        },
        updateFormData: function($form, name, data, eachCallback) {
            $form.find("[data-group='" + name + "']")
                .each(function(idx, elem) {
                    const $elem = $(elem);
                    const fieldName = $elem.data("name");
                    const target = $elem.data("target");
                    let bodyValue = ifEmpty(data[fieldName], "");
                    switch (target) {
                        case "text":
                            $elem.text(bodyValue);
                            break;
                        case "value":
                            $elem.val(bodyValue);
                            break;
                        case "code":
                            const lang = $elem.data("language");
                            let highlightedCode = hljs.highlight(bodyValue, {language: lang});

                            const $codeElem = $("<pre class='code-view language-" + lang + " hljs'></pre>")
                                .append(highlightedCode.value);
                            $elem.html("");
                            $elem.append($codeElem);
                            break;
                        case "editor":
                            $elem.aceEditor()
                                .setContent(bodyValue)
                            break;
                        default:
                            throw new Error("Not support 'target'. target = " + target);
                    }
                    updateColorGroup($elem, bodyValue);
                    if(eachCallback) {
                        eachCallback($elem, bodyValue);
                    }
                });
        },
        resetData: function ($elem) {
            $elem.find("[data-clear-target]")
                .each(function(idx, item) {
                    const $item = $(item);

                    const target = $item.data("clear-target");
                    let content = $item.data("clear-content");
                    if(!content) {
                        content = "";
                    }
                    switch (target) {
                        case "form":
                            $item[0].reset();
                            break;
                        case "html":
                            $item.html(content);
                            break;
                        case "text":
                            $item.text(content);
                            break;
                        case "value":
                            $item.val(content);
                            break;
                        default:
                            console.warn("Invalid target. target = " + target, $item);
                            break;
                    }
                })
        }
    }
})();

(function() {
    $(document).ready(function() {
        $("[data-toggle='tooltip']").tooltip({placement: 'auto'});

        $(document).on('DOMNodeInserted', function(e) {
            const $target = $(e.target);
            if ($target.data("toggle") === 'tooltip') {
                const placement = $target.data("tooltip-place");
                $target.tooltip({placement: placement ? placement : 'auto'});
            }
        });

        $("[data-toggle='editor-lang']").click(function (e) {
            e.preventDefault();
            const $this = $(this);
            $("#" + $this.data("editor-id")).aceEditor()
                .changeLanguage($this.data("editor-lang"));
            $("#" + $this.data("toggle-button")).text($this.text());
        });
    });
})();