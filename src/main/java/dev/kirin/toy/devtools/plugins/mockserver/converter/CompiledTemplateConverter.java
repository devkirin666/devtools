package dev.kirin.toy.devtools.plugins.mockserver.converter;

import dev.kirin.common.template.Template;
import dev.kirin.common.template.compile.TemplateParseContext;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.common.template.json.JsonAdaptor;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;

@Converter(autoApply = true)
public class CompiledTemplateConverter implements AttributeConverter<CompiledTemplate, String> {
    private final JsonAdaptor jsonAdaptor;
    private final Template template;
    private final TemplateParseContext parseContext;

    public CompiledTemplateConverter(JsonAdaptor jsonAdaptor, Template template) {
        this.jsonAdaptor = jsonAdaptor;
        this.template = template;
        this.parseContext = template.makeParseContext();
    }

    @Override
    public String convertToDatabaseColumn(CompiledTemplate attribute) {
        if(attribute == null) {
            return null;
        }
        CompiledTemplate compiled = template.compile(this.parseContext, attribute.getContent());
        return jsonAdaptor.toJson(compiled);
    }

    @Override
    public CompiledTemplate convertToEntityAttribute(String dbData) {
        if(StringUtils.hasText(dbData)) {
            return jsonAdaptor.fromJson(dbData, CompiledTemplate.class);
        }
        return CompiledTemplate.builder()
                .tokens(Collections.emptyList())
                .build();
    }
}