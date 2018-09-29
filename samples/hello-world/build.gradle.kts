plugins {
    application
    kotlin("jvm") version "1.2.61"
}

application {
    // mainClassName = "samples.HelloWorld"
    mainClassName = "samples.HelloWorldKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    testCompile("junit:junit:4.12")
    compile(kotlin("stdlib"))
}

repositories {
    jcenter()
}