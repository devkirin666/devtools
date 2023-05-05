package dev.kriin.common.spring.extension.encryptor;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.extension.encryptor.EncryptorConfig;
import dev.kirin.common.spring.extension.encryptor.EncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
class JasyptStringEncryptorTest {
    private static final String METHOD_NAME = "stringEncryptor";
    private EncryptorConfig config = new EncryptorConfig();
    private EncryptorProperties properties = new EncryptorProperties();

    @Test
    void testEncrypt() throws Exception {
        String value = "test";

        String keyFile = System.getProperty("app.encryptor.key-file");
        if(!StringUtils.hasText(keyFile)) {
            log.warn("Not set key file. using default key file.");
        } else {
            Field field = properties.getClass().getDeclaredField("keyFile");
            field.setAccessible(true);
            field.set(properties, keyFile);
        }
        Method method = config.getClass().getDeclaredMethod(METHOD_NAME, EncryptorProperties.class);
        method.setAccessible(true);
        StringEncryptor encryptor = (StringEncryptor) method.invoke(config, properties);
        String encrypted = encryptor.encrypt(value);

        Assertions.assertNotNull(encrypted);
        Assertions.assertNotEquals(StringUtil.BLANK, encrypted);

        Assertions.assertEquals(value, encryptor.decrypt(encrypted));
    }
}
