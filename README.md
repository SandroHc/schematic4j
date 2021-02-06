# Schematic4j

Java parser for the .schem/.schematic/.litematic Minecraft formats. 🗺

## Supported formats

Format | Extension | Links
--- | --- | ---
[Sponge Schematic Format](https://github.com/SpongePowered/Schematic-Specification) | .schem | Spec: [v1](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md) • [v2](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md)
[~~Litematica~~](https://github.com/maruohon/litematica) 🚧 | .litematic | [Spec](https://github.com/maruohon/litematica/blob/master/src/main/java/fi/dy/masa/litematica/schematic/LitematicaSchematic.java) • [Discussion](https://github.com/maruohon/litematica/issues/53#issuecomment-520279558)
[~~Schematica~~](https://curseforge.com/minecraft/mc-mods/schematica) 🚧 | .schematic | [Spec](https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/world/schematic/SchematicAlpha.java)
[~~WorldEditor~~](https://enginehub.org/worldedit/) 🚧 | .schematic | [Spec](https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/extent/clipboard/io/MCEditSchematicReader.java)
[~~MCEdit2~~](https://www.mcedit.net/) 🚧 | .schematic | [Spec](https://github.com/mcedit/mcedit2/blob/master/src/mceditlib/schematic.py)
~~MCEditor-Unified~~ 🚧 | .schematic | [Spec](https://github.com/Podshot/MCEdit-Unified/blob/master/pymclevel/schematic.py)
~~Legacy MCEdit~~ 🚧 | .schematic | [Spec](https://github.com/mcedit/pymclevel/blob/master/schematic.py)
~~BuilderTools - PocketMine~~ 🚧 | .schematic | [Spec](https://github.com/CzechPMDevs/BuilderTools/blob/default/BuilderTools/src/czechpmdevs/buildertools/async/SchematicCreateTask.php)

**TODO**: Investigate how [Jupisoft's Minecraft Tools](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-tools/2947154-minecraft-tools-in-c-for-1-14-with-full-source) and [Mineways](http://www.realtimerendering.com/erich/minecraft/public/mineways/) convert between pre-1.13 and new format.

## Installation

Add the following dependency to your build file.

If using Gradle (`build.gradle`):
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'net.sandrohc:schematic4j:master-SNAPSHOT'
}
```

If using Maven (`pom.xml`):
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependency>
    <groupId>net.sandrohc</groupId>
    <artifactId>schematic4j</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

If you want to use development versions, see: https://jitpack.io/#net.sandrohc/schematic4j

## Usage

Here are some examples on how to use this library:

```java
Path path = Path.of("your.schem");
Schematic schematic = SchematicUtil.load(path);

schematic.getName();
schematic.getBlock(0, 0, 0).name;
```