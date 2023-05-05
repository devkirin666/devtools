package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
class ExpressionContext extends StandardEvaluationContext {
    private static final PropertyAccessor PROPERTY_ACCESSOR = new MapFieldPropertyAccessor();

    public ExpressionContext() {
        addPropertyAccessor(new MapFieldPropertyAccessor());
    }

    public ExpressionContext(Object rootObject) {
        super(rootObject);
        addPropertyAccessor(PROPERTY_ACCESSOR);
    }

    static class MapFieldPropertyAccessor implements PropertyAccessor {
        private boolean isValid(Object target) {
            if(target == null) {
                return false;
            }
            return Map.class.isAssignableFrom(target.getClass());
        }

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class[]{Map.class, LinkedHashMap.class};
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            boolean result = isValid(target);
            log.debug("(canRead) targetClass = {}, result = {}", target == null ? null : target.getClass().getName(), result);
            return result;
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            if(!isValid(target)) {
                throw new AccessException("null or not Map.class");
            }
            Map<String, Object> map = (Map<String, Object>) target;
            return new TypedValue(map.get(name));
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            boolean result = isValid(target);
            log.debug("(canWrite) targetClass = {}, result = {}", target == null ? null : target.getClass().getName(), result);
            return result;
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            if(!isValid(target)) {
                throw new AccessException("null or not Map.class");
            }
            Map<String, Object> map = (Map<String, Object>) target;
            map.put(name, newValue);
        }
    }
}
