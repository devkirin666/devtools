package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vars {
    private String uuid;
    private long timestamp;
    private String datetime;

    static Vars generate() {
        return Vars.builder()
                .uuid(UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .datetime(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()))
                .build();
    }
}