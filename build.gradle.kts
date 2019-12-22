import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
	kotlin("kapt") version "1.3.61"
}

group = "company.ryzhkov"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
//	maven ( "https://dl.bintray.com/arrow-kt/arrow-kt/" )
//	maven (" https://oss.jfrog.org/artifactory/oss-snapshot-local/")

	maven { setUrl("https://dl.bintray.com/arrow-kt/arrow-kt/")}
	maven { setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.jsonwebtoken:jjwt-impl:0.10.7")
	implementation("io.jsonwebtoken:jjwt-api:0.10.7")
	implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")

	implementation ("io.arrow-kt:arrow-fx:0.10.4")
	implementation ("io.arrow-kt:arrow-optics:0.10.4")
	implementation ("io.arrow-kt:arrow-syntax:0.10.4")
	implementation ("io.arrow-kt:arrow-fx-reactor:0.10.4")
	kapt ("io.arrow-kt:arrow-meta:0.10.4")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
