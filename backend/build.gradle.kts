import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.4"
    jacoco
}

group = "dev.sigil"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val otelVersion = "2.16.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    implementation("com.nimbusds:nimbus-jose-jwt:10.3.1")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:$otelVersion")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.21.1")
    }
}

val sharedJvmArgs = listOf(
    "-Xms512m",
    "-Xmx512m",
    "-XX:MaxGCPauseMillis=100",
    "-XX:+TieredCompilation",
    "-Dspring.profiles.active=local",
)

tasks.bootRun {
    jvmArgs(sharedJvmArgs)
}

tasks.withType<Test> {
    useJUnitPlatform {
        excludeTags("integration")
    }
    jvmArgs(sharedJvmArgs)
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.register<Test>("integrationTest") {
    useJUnitPlatform {
        includeTags("integration")
    }
    jvmArgs(sharedJvmArgs)
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

spotless {
    java {
        googleJavaFormat("1.27.0")
        removeUnusedImports()
        trimTrailingWhitespace()
    }
}
