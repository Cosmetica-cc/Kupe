# Kupe

Cross-loader, cross-version GUI library for Minecraft. For all your navigation needs. Like the great and legendary navigator Kupe,
who sailed to Aotearoa.

The classes are split into `api` and `impl` packages. `api` classes are the only ones kept stable across
environments and versions. In addition, a few api methods are marked as `@LeavesSandbox` and return minecraft classes.
These are not guaranteed to be completely stable across environments, but will be kept stable where possible. They are provided in case
a modder wishes to have greater flexibility than the library itself provides without referencing implementation classes.

## Setting Up

You can include Kupe in your project via gradle. First, add the cosmetica maven to your repositories:

```groovy
repositories {
    maven { url 'https://maven.cosmetica.cc/' }
}
```

Then, add the mod as a dependency:

```groovy
dependencies {
    modImplementation 'cc.cosmetica:Kupe:1.0-SNAPSHOT'
}
```

## Creating a Component


### Creating a Screen


### Reactive States


### Creating a leaf Component


## Further Examples

For further examples on how to use the library, check out the test mod, in the `testmod-common` directory. Bootstrap code to register
screens can be found in the loader-specific testmod directories. The test mod is provided under CC0, so feel free to steal code
from it as much as you like.

The Kupe library itself is licensed under Apache-2.0, an open-source license.

[Cosmetica](https://Cosmetica-cc/Cosmetica-2) also provides a good example of how to use the library. Its code is licensed
under Apache-2.0.
