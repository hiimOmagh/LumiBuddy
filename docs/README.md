# Documentation Index

This folder collects guides for building and contributing to LumiBuddy.

## Available Guides

- **Architecture** – High level overview of module boundaries, navigation and dependency injection.
  See the [architecture](architecture) directory for articles. Source diagrams are
  in [diagrams](diagrams), including [Dagger components](diagrams/dagger_components.dot) and
  the [module map](diagrams/module_map.dot).
- **Build notes** – Environment requirements and build tips: [build_notes.md](build_notes.md).
- **Firebase setup** – Steps to configure Firestore sync: [firebase_setup.md](firebase_setup.md).
- **Testing** – Unit test patterns and QA workflow: [testing.md](testing.md).

## Contribution Guidelines

- Discuss large changes via an issue before opening a pull request.
- Create feature branches and keep commits focused.
- Run `./gradlew test lint` and `./scripts/check_dependencies.sh` before submitting.
- Update documentation and architecture diagrams when behaviour changes.
