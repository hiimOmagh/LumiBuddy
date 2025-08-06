# Build Notes

This project uses Android Gradle Plugin (AGP) **8.10.1** together with Gradle 9.0.0-milestone-9.
Gradle itself runs on **JDK 17**. Other JDK versions may work but have not been tested.

Run builds with:

```bash
./gradlew --warning-mode=all
```

Running with `--warning-mode=all` prints any deprecation messages so upgrade steps are clear. Refer
to [release_build.md](release_build.md) for signing and distribution instructions.
