# Schematic4j

[![Latest version](https://img.shields.io/maven-central/v/net.sandrohc/schematic4j?label=version)](https://central.sonatype.com/artifact/net.sandrohc/schematic4j)

Java parser for the .schem/.schematic/.litematic Minecraft formats. 🗺

## Supported formats

| Format                     | Extension  | Links                                                                    |
|----------------------------|------------|--------------------------------------------------------------------------|
| [Sponge Schematic][sponge] | .schem     | Spec: [v1][sponge-spec-v1] • [v2][sponge-spec-v2] • [v3][sponge-spec-v3] |
| [Litematica][litematica]   | .litematic | [Spec][litematica-spec] • [Discussion][litematica-discussion]            |
| [Schematica][schematica]   | .schematic | [Spec][schematica-spec]                                                  |

[sponge]: https://github.com/SpongePowered/Schematic-Specification
[sponge-spec-v1]: https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
[sponge-spec-v2]: https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md
[sponge-spec-v3]: https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-3.md
[litematica]: https://github.com/maruohon/litematica
[litematica-spec]: https://github.com/maruohon/litematica/blob/pre-rewrite/fabric/1.20.x/src/main/java/fi/dy/masa/litematica/schematic/LitematicaSchematic.java
[litematica-discussion]: https://github.com/maruohon/litematica/issues/53#issuecomment-520279558
[schematica]: https://curseforge.com/minecraft/mc-mods/schematica
[schematica-spec]: https://minecraft.fandom.com/wiki/Schematic_file_format

## Installation

Add the following dependency to your build file.

If using Gradle (`build.gradle`):
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'net.sandrohc:schematic4j:1.1.0'
}
```

If using Maven (`pom.xml`):
```xml
<dependency>
    <groupId>net.sandrohc</groupId>
    <artifactId>schematic4j</artifactId>
    <version>1.1.0</version>
</dependency>
```

For development builds, please see: https://jitpack.io/#net.sandrohc/schematic4j

## Usage

Here are some examples on how to use this library:

```java
// Load schematic from a file.
// Currently supported formats include .schematic, .schem and .litematic.
Schematic schematic = SchematicLoader.load("/path/to/your.schematic"); 

schematic.name();
schematic.width();
schematic.height();
schematic.length();
schematic.block(0, 0, 0).name;
schematic.blocks().collect(Collectors.toList());
schematic.blockEntities().collect(Collectors.toList());
schematic.entities().collect(Collectors.toList());
```