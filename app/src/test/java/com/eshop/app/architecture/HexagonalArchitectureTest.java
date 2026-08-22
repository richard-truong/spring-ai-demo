package com.eshop.app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    private static final JavaClasses CORE = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.eshop.core");

    private static final JavaClasses APP = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.eshop.app");

    @Test
    void coreMustNotDependOnSpringFramework() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.core..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .allowEmptyShould(true);
        rule.check(CORE);
    }

    @Test
    void coreMustNotDependOnJpa() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta.persistence..", "javax.persistence..", "org.hibernate..")
            .allowEmptyShould(true);
        rule.check(CORE);
    }

    @Test
    void coreMustNotDependOnServletOrTransportFramework() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta.servlet..", "javax.servlet..", "io.jsonwebtoken..")
            .allowEmptyShould(true);
        rule.check(CORE);
    }

    @Test
    void coreMustNotDependOnAppModule() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.core..")
            .should().dependOnClassesThat().resideInAPackage("com.eshop.app..")
            .allowEmptyShould(true);
        rule.check(CORE);
    }

    @Test
    void domainMustNotDependOnApplication() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.core.domain..")
            .should().dependOnClassesThat().resideInAPackage("com.eshop.core.application..")
            .allowEmptyShould(true);
        rule.check(CORE);
    }

    @Test
    void inboundAdaptersMustNotDependOnOutboundAdapters() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.app.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("com.eshop.app.adapter.out..")
            .allowEmptyShould(true);
        rule.check(APP);
    }

    @Test
    void inboundAdaptersMustNotDependOnConcreteUseCaseImplementations() {
        ArchRule rule = noClasses().that().resideInAPackage("com.eshop.app.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("com.eshop.core.application.usecase..")
            .allowEmptyShould(true);
        rule.check(APP);
    }

    @Test
    void outboundAdaptersMustImplementOutboundPorts() {
        ArchRule rule = classes().that().resideInAPackage("com.eshop.app.adapter.out..")
            .and().areNotInterfaces()
            .and().haveSimpleNameNotEndingWith("Entity")
            .should().implement(resideInAPackage("com.eshop.core.application.port.out.."));
        rule.check(APP);
    }

}
