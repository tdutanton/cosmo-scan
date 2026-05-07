// === build.gradle.kts (root) ===
plugins {
    id("org.springframework.boot") version "3.3.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "cosmo-scan"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    // Java-версия
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    // 👇 Зависимости через add()
    dependencies {
        add("implementation", "org.springframework.boot:spring-boot-starter")

        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")

        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

        add("testCompileOnly", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("file.encoding", "UTF-8")
        jvmArgs("-Dfile.encoding=UTF-8")
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
        }

        // 👇 Связка с отчётом внутри блока Test
        finalizedBy("jacocoTestReport")
    }

// Настройка JaCoCo - используем named<T> для type-safe доступа
    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.withType<Test>())
        reports {
            html.required.set(true)
            xml.required.set(false)
            csv.required.set(false)
        }
    }
}