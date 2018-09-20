/*
 * SonarQube Java Custom Rules Example
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package br.jus.tst.sonar;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.fest.assertions.Assertions.assertThat;
import org.mockito.Mockito;
import org.sonar.check.Cardinality;
import org.sonar.squidbridge.annotations.RuleTemplate;

public final class TstJavaRulesDefinitionTest {

    @Test
    public void test() {
        TstJavaRulesDefinition rulesDefinition = new TstJavaRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);
        RulesDefinition.Repository repository = Optional
            .ofNullable(
                context.repository(TstJavaRulesDefinition.REPOSITORY_KEY)
            )
            .orElse(new DummyRepository());

        assertThat(repository.name()).isEqualTo("Tribunal Superior do Trabalho");
        assertThat(repository.language()).isEqualTo("java");
        assertThat(repository.rules()).hasSize(RulesList.getChecks().size());

        assertRuleProperties(repository);
        assertAllRuleParametersHaveDescription(repository);
    }
    
    @Test
    public void ruleClassesShouldHaveRuleAnnotation() {
        final TstJavaRulesDefinition definition = new TstJavaRulesDefinition();
        Throwable thrown = Assertions
            .catchThrowable(() -> definition.newRule(String.class, null));
        Assertions.assertThat(thrown)
            .hasMessageContaining("No Rule annotation was found");
    }

    @Test
    public void ruleKeyShouldBeDefined() {
        final TstJavaRulesDefinition definition = new TstJavaRulesDefinition();
        Throwable thrown = Assertions
            .catchThrowable(() -> definition.newRule(RuleKeyNotDefined.class, null));
        Assertions.assertThat(thrown)
            .hasMessageContaining("No key is defined in Rule annotation");
    }

    @Test
    public void ruleShouldExistInRepository() {
        final TstJavaRulesDefinition definition = new TstJavaRulesDefinition();
        final RulesDefinition.Context context = new RulesDefinition.Context();
        RulesDefinition.NewRepository repository = context
            .createRepository("test", "java");
        Throwable thrown = Assertions
            .catchThrowable(() -> definition.newRule(DummyRule.class, repository));
        Assertions.assertThat(thrown)
            .hasMessageContaining("No rule was created for");
    }

    @Test
    public void cardinalityShouldNotBeDefined() {
        final TstJavaRulesDefinition definition = new TstJavaRulesDefinition();
        final RulesDefinition.NewRepository repository = Mockito
            .mock(RulesDefinition.NewRepository.class);
        Mockito.when(repository.rule(Mockito.anyString()))
            .thenReturn(Mockito.mock(RulesDefinition.NewRule.class));
        Throwable thrown = Assertions
            .catchThrowable(() -> definition.newRule(RuleWithCardinality.class, repository));
        Assertions.assertThat(thrown)
            .hasMessageContaining("Cardinality is not supported");
    }

    @Test
    public void testRuleWithTemplate() {
        final TstJavaRulesDefinition definition = new TstJavaRulesDefinition();
        final RulesDefinition.NewRepository repository = Mockito
            .mock(RulesDefinition.NewRepository.class);
        final RulesDefinition.NewRule rule = Mockito
            .mock(RulesDefinition.NewRule.class);
        Mockito.when(repository.rule(Mockito.anyString())).thenReturn(rule);
        definition.newRule(RuleWithTemplate.class, repository);
        Mockito.verify(rule).setTemplate(true);
    }

    private void assertRuleProperties(Repository repository) {
        final String key = "br.jus.tst.sonar.checks.ApiDocumentationRule";
        Rule rule = Optional
            .ofNullable(repository.rule(key))
            .orElseThrow(
                () -> new NullPointerException(
                    String.format("Rule not found for key %s", key)
                )
            );
        assertThat(rule).isNotNull();
        assertThat(rule.name()).isEqualTo("Class annotated with @Path should also be annotated with @Api");
        assertThat(rule.type()).isEqualTo(RuleType.CODE_SMELL);
    }

    /**
     * Every rule parameter must have a description.
     * @param repository Rule's repository.
     */
    private void assertAllRuleParametersHaveDescription(
        final Repository repository) {
        repository.rules().stream()
            .flatMap(rl -> rl.params().stream())
            .forEach(
                param -> assertThat(param.description())
                    .as(String.format("description for %s", param.key()))
                    .isNotEmpty()
            );
    }

    /**
     * Prevents NullPointerException.
     */
    private static class DummyRepository implements RulesDefinition.Repository {

        @Override
        public String name() {
            return "";
        }

        @Override
        public String key() {
            return "";
        }

        @Override
        public String language() {
            return "";
        }

        @Override
        public Rule rule(final String Key) {
            return null;
        }

        @Override
        public List<Rule> rules() {
            return Collections.emptyList();
        }

    }

    @org.sonar.check.Rule
    private static class RuleKeyNotDefined {
        
    }
    @org.sonar.check.Rule(key = "test")
    private static class DummyRule {
        
    }
    @org.sonar.check.Rule(key = "test", cardinality = Cardinality.MULTIPLE)
    private static class RuleWithCardinality {
        
    }
    @org.sonar.check.Rule(key = "test")
    @RuleTemplate
    private static class RuleWithTemplate {
        
    }
}
