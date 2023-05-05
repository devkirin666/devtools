package dev.kirin.toy.devtools.plugins.mockserver.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface MockController {
    @AliasFor(annotation = Controller.class)
    String value() default "";
}
