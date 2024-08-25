package com.steeplesoft.simplesec.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusComponentTest
public class PrincipalFactoryTest {
    @Inject
    PrincipalFactoryProducer factory;

    @Test
    @TestConfigProperty(key = "simplesec.jwt.revocation.support", value = "true")
    public void testPrincipalFactory() {
        assertInstanceOf(PrincipalFactoryProducer.PersistentCallerPrincipalFactory.class, factory.produce());
    }

    @Test
    @TestConfigProperty(key = "simplesec.jwt.revocation.support", value = "false")
    public void testPrincipalFactory2() {
        assertFalse(factory.produce() instanceof PrincipalFactoryProducer.PersistentCallerPrincipalFactory);
    }
}
