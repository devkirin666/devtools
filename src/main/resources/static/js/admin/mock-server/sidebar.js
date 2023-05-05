'use strict';

(function() {
    $.widget.bridge('uibutton', $.ui.button);

    const BASE_URI = BASE_URL + "/api/v1/mock-urls";

    let activeUrlId;

    const changeUrlEnable = function($elem, isEnable) {
        $elem.data("value", isEnable);
        if(isEnable) {
            $elem.removeClass("text-default");
            $elem.addClass("text-primary");
            $elem.cancel(false);
        } else {
            $elem.removeClass("text-primary");
            $elem.addClass("text-default");
            $elem.cancel();
        }
    }

    const reloadUrls = function($form, $nav, viewField, isReset) {
        if(isReset) {
            $nav.find("li.nav-item.url-item").remove();
        }
        forms.get($form, function(body) {
            if(!body || !body.hasContents) {
                $form.find("input[name='page']").val(0);
            } else {
                $.each(body.contents, function(idx, item) {
                    let enableClass = (item.enabled ? "text-primary" : "text-default");
                    const activeIdClass = (item.id === activeUrlId) ? " active" : "";
                    $nav.append("" +
                        "<li class='nav-item text-nowrap url-item" + activeIdClass + "' data-id='" + item.id + "' title='" + item.uri + "'>\n" +
                        "   <a href='" + BASE_MANAGEMENT_PAGE_URL + "/urls/" + item.id + "' class='nav-link'>\n" +
                        "       <i data-action='url-enable' data-id='" + item.id + "' data-value='" + item.enabled + "' class='nav-icon fas fa-circle " + enableClass + "'></i>\n" +
                        "       <p data-toggle-name='Name' style='" + (viewField === 'URI' ? "display: none;" : "") + "'>" + item.name + "</p>\n" +
                        "       <p data-toggle-name='URI' style='" + (viewField === 'Name' ? "display: none;" : "") + "'>" + item.uri + "</p>" +
                        "   </a>\n" +
                        "</li>")
                });
                const $inputPage = $form.find("input[name='page']");
                $inputPage.val(Number($inputPage.val()) + 1);

                reloadUrls($form, $nav, viewField, false);
            }
        })
    }

    $(document).ready(function() {
        const $URL_SEARCH_FORM = $("#form-url-search");
        const $NAV_URL = $("#nav-url");
        const $TOGGLE_URL_LIST = $("#toggle-url-list");
        const $MODAL_NEW_URL = $("#modal-new-url");

        $URL_SEARCH_FORM.attr("action", BASE_URI);

        $URL_SEARCH_FORM.submit(function(e) {
            e.preventDefault();
            reloadUrls($(this), $NAV_URL, $TOGGLE_URL_LIST.toggleName(), true);
        });

        $TOGGLE_URL_LIST.change(function(e) {
            const $this = $(this);
            const field = $this.toggleName();
            $this.closest("ul")
                .find("li.nav-item.url-item > a > p").hide();
            $this.closest("ul")
                .find("p[data-toggle-name='" + field + "']").show();
        });

        $("#form-new-url").submit(function(e) {
            e.preventDefault();
            const $this = $(this);
            forms.upsert($this, function(body) {
                $MODAL_NEW_URL.hide();
                $URL_SEARCH_FORM.trigger("submit");
            });
        });

        $MODAL_NEW_URL.on("hide.bs.modal", function(e) {
            $("#form-new-url")[0].reset();
        });

        $(document).on("mock.url.enable", function(e) {
            const $elem = $("i[data-action='url-enable'][data-id='" + e.detail.id + "']");
            changeUrlEnable($elem, e.detail.enabled);
        });

        $(document).on("mock.url.removed", function(e) {
            $("li.url-item[data-id='" + e.detail.id + "']").remove();
        })

        $(document).on("mock.url.active", function(e) {
            activeUrlId = e.detail.id;
            $("li.url-item[data-id='" + e.detail.id + "']").addClass("active");
        })

        $URL_SEARCH_FORM.trigger("submit");
    });
})();