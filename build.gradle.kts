import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.jetbrains.kotlin.jvm") version "1.5.20"
	id("org.jetbrains.kotlin.plugin.spring") version "1.5.20"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.5.20"
}

group = "com.github.xiaosongfu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	// mavenCentral()
	maven(url = "https://maven.aliyun.com/repository/central")
	maven { url = uri("https://jitpack.io") }
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

	implementation("com.squareup.okhttp3:okhttp:4.7.2")
	implementation("com.jayway.jsonpath:json-path:2.4.0")
	implementation("com.alibaba:easyexcel:2.2.8")

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("mysql:mysql-connector-java")

	implementation("io.springfox:springfox-boot-starter:3.0.0")

	implementation("com.github.xiaosongfu:jakarta-common:0.1.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
