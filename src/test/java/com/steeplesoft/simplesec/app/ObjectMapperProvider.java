package com.steeplesoft.simplesec.app;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

public class ObjectMapperProvider {
    @Produces
    @ApplicationScoped
    public JsonMapper produceObjectMapper() {
        return JsonMapper.builder().addModule(new JavaTimeModule()).build();

    }
}
