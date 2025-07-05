# Crane Framework

Crane is a lightweight Java 21+ backend framework designed for building small and efficient services without relying on heavy frameworks like Spring. It aims to provide essential building blocks in a modular structure.

## Modules

### `crane-core`
Provides the basic application context, dependency injection, lifecycle management and web server.

### `crane-data`
Offers a simple abstraction over JDBC with predefined CRUD operations and basic entity annotations.

### `crane-mail`
Offers a easy way to configure and send emails. Build on top of jakarta mail.

## Goals

- Lightweight and modular
- Minimal dependencies
- Simple to use and extend

## Getting Started

```bash
git clone https://github.com/mehmetsbs/crane-framework.git
cd crane-framework
./mvnw clean install
```

Add the modules you need as dependencies to your project.

Status:
Crane is in active development and not yet production-ready. APIs may change. Soon an example project will be published here. Later on an initializer app will be published.

License:
This project is licensed under the Apache 2.0 License.
