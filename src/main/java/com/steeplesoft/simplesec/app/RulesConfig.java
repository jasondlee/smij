package com.steeplesoft.simplesec.app;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@StaticInitSafe
@ConfigMapping(prefix = "simplesec.rules")
public interface RulesConfig {
    @WithDefault("10")
    int minLength();

    @WithDefault("64")
    int maxLength();

    @WithDefault("true")
    boolean allowWhitespace();

    @WithDefault("1")
    int minUpperCase();
    @WithDefault("1")
    int minLowerCase();
    @WithDefault("1")
    int minDigit();
    @WithDefault("1")
    int minSpecialCharacter();


    @WithDefault("5")
    int maxAlphaSequence();
    @WithDefault("5")
    int maxNumericalSequence();
    @WithDefault("5")
    int maxKeyboardSequence();
}
