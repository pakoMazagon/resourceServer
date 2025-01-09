package com.tpv.resource_server.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.OffsetDateTime;

@Configuration
public class BeansConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.setAllowCredentials(true);
        cors.addAllowedOrigin("http://127.0.0.1:5173");
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new SimpleModule().addDeserializer(OffsetDateTime.class, new JsonDeserializer<>() {
                    @Override
                    public OffsetDateTime deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
                            throws IOException {
                        return jsonParser.getText() != null ? OffsetDateTime.parse(jsonParser.getText()) : null;
                    }
                }));
//                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


}
