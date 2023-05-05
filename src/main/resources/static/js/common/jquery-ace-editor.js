'use strict';

(function() {
    const DEFAULT_OPTION = {
        maxLines: 20,
    };

    ace.config.set("basePath", BASE_URL + "/static/plugins/ace-editor");

    const EDITOR_BEAUTIFIER = ace.require("ace/ext/beautify");
    const DEFAULT_VALUES = {
        lang: "plain_text",
        theme: "chrome",
    }

    const CREATED_EDITORS = {};

    class AceEditorWrapper {
        constructor(elem, option) {
            this.$elem = elem;
            this.editor = ace.edit(elem[0]);
            this.editor.setOptions(option);
            this.id = this.editor.id;

            const $form = this.$elem.closest("form");
            if($form) {
                $form.on("reset", function(e) {
                    this.reset();
                })
            }

            this.observer = new IntersectionObserver(function(entries, observer) {
                $(entries).each(function(idx, entry) {
                    const $target = $(entry.target);
                    if($target.is(":visible")) {
                        $target.aceEditor().resize();
                    }
                })
            }, {threshold: 1.0});
            this.observer.observe(this.$elem[0]);
        }
    }

    AceEditorWrapper.prototype.changeLanguage = function(lang) {
        this.editor.session.setMode("ace/mode/" + (lang ? lang : DEFAULT_VALUES.lang));
        return this;
    };

    AceEditorWrapper.prototype.changeTheme = function(theme) {
        this.editor.setTheme("ace/theme/" + (theme ? theme : DEFAULT_VALUES.theme));
        return this;
    };

    AceEditorWrapper.prototype.beautify = function() {
        if(EDITOR_BEAUTIFIER) {
            EDITOR_BEAUTIFIER.beautify(this.editor.session);
        } else {
            console.warn("Not loaded beautify module. module = 'ext-beautify.js'");
        }
        return this;
    };
    AceEditorWrapper.prototype.setContent = function(content) {
        this.editor.setValue(content);
        this.resize();
        return this;
    };
    AceEditorWrapper.prototype.onChange = function(callback) {
        const _this = this;
        const _editor = this.editor;
        this.editor.session.on("change", function(e) {
            callback.call(_this, _editor, e);
        });
        return this;
    };
    AceEditorWrapper.prototype.resize = function() {
        this.editor.resize();
        this.editor.session.selection.clearSelection();
        return this;
    };

    AceEditorWrapper.prototype.reset = function() {
        this.editor.session.setValue("", -1);
        this.editor.resize();
        return this;
    };

    jQuery.fn.aceEditor = function(options) {
        let id = this.data("editor-id");
        if(!(id in CREATED_EDITORS)) {
            const editor = new AceEditorWrapper(this, options && typeof options === 'object' ? options : DEFAULT_OPTION);
            editor.changeLanguage(this.data("editor-lang"))
                .changeTheme(this.data("editor-theme"))

            id = editor.id;
            this.attr("data-editor-id", id);
            CREATED_EDITORS[id] = editor;
            const $target = $("#" + this.data("editor-value-for"));
            if($target.length > 0) {
                editor.onChange(function(editor, e) {
                    $target.val(editor.session.getValue());
                })
            }
        }
        return CREATED_EDITORS[id];
    }

    $(document).ready(function() {
        $("[data-code-editor='ace']").each(function(idx, item) {
            const $item = $(item);
            $item.aceEditor();
        });
    })
})();