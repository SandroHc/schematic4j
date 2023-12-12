# Schematic4j

Java parser for the .schem/.schematic/.litematic Minecraft formats. 🗺

## Supported formats

Format | Extension | Links
--- | --- | ---
[Sponge Schematic Format](https://github.com/SpongePowered/Schematic-Specification) | .schem | Spec: [v1](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md) • [v2](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md)

## Installation

Add the following dependency to your build file.

If using Gradle (`build.gradle`):
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'net.sandrohc:schematic4j:1.0.1'
}
```

If using Maven (`pom.xml`):
```xml
<dependency>
    <groupId>net.sandrohc</groupId>
    <artifactId>schematic4j</artifactId>
    <version>1.0.1</version>
</dependency>
```

For development builds, please see: https://jitpack.io/#net.sandrohc/schematic4j

## Usage

Here are some examples on how to use this library:

```java
Path path = Path.of("your.schem");
Schematic schematic = SchematicUtil.load(path);

schematic.getName();
schematic.getBlock(0, 0, 0).name;
```