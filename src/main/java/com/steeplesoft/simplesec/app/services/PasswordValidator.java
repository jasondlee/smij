package com.steeplesoft.simplesec.app.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import com.steeplesoft.simplesec.app.RulesConfig;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class PasswordValidator {
    @Inject
    RulesConfig rulesConfig;

    private org.passay.PasswordValidator validator;

    @PostConstruct
    public void buildValidator() {
        List<Rule> rules = new ArrayList<>(
                List.of(
                        new LengthRule(rulesConfig.minLength(), rulesConfig.maxLength()),
                        new CharacterRule(EnglishCharacterData.UpperCase, rulesConfig.minUpperCase()),
                        new CharacterRule(EnglishCharacterData.LowerCase, rulesConfig.minLowerCase()),
                        new CharacterRule(EnglishCharacterData.Digit, rulesConfig.minDigit()),
                        new CharacterRule(EnglishCharacterData.Special, rulesConfig.minSpecialCharacter()),
                        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, rulesConfig.maxAlphaSequence(), false),
                        new IllegalSequenceRule(EnglishSequenceData.Numerical, rulesConfig.maxNumericalSequence(), false),
                        new IllegalSequenceRule(EnglishSequenceData.USQwerty, rulesConfig.maxKeyboardSequence(), false)
                )
        );

        if (!rulesConfig.allowWhitespace()) {
            // no whitespace
            rules.add(new WhitespaceRule());
        }

        validator = new org.passay.PasswordValidator(rules);
    }

    public List<String> validatePassword(String password) {
        RuleResult results = validator.validate(new PasswordData(password));

        if (results.isValid()) {
            return List.of();
        } else {
            return validator.getMessages(results);
        }
    }

}
