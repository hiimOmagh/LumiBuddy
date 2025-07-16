# Module Map

The project is organised into several Gradle modules grouped into **core**, **feature** and **shared
** layers. The diagram below shows the high level dependencies.

![Module Map](../diagrams/module_map.png)

Each feature module depends on `core-infra` and `core-data` which in turn rely on `core-domain`.
Some features also depend on each other or on the `shared-ml` helpers.