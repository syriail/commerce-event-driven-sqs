import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.9.24"

	kotlin("jvm") version kotlinVersion
	kotlin("kapt") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion

	kotlin("org.springframework.boot") version "3.3.0"
	kotlin("io.spring.dependency-management") version "1.1.5"
}

group = 'com.ghrer.commerce'

java {
	sourceCompatibility = '17'
}

repositories {
	mavenCentral()
}

tasks.withType(KotlinCompile) {
	kotlinOptions {
		freeCompilerArgs += '-Xjsr305=strict'
		jvmTarget = '17'
	}
}

tasks.named('test') {
	useJUnitPlatform()
}

tasks.named("bootJar") {
	archiveFileName.set("app.jar")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.1.0")

	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
