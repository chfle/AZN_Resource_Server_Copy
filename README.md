<div align="center">
<h3 align="center">AZN-Resource-Server</h3>
</div>

![example workflow](https://github.com/LokCenter/AZN_Spring_ResourceServer/actions/workflows/maven-publish.yml/badge.svg)

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/LokCenter/AZN_Spring_ResourceServer/graphs/commit-activity)


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#collaborators">Collaborators</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

<img src="readme/azure.png" alt="project image">

API-Backend AZN_Spring


* OAuth 2.0
* AAD Cloud integration
* JWT

### Built With

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Security](https://spring.io/projects/spring-security)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Memcached](https://github.com/couchbase/spymemcached)
* [Spring Cloud Azure](https://spring.io/projects/spring-cloud-azure)
* [Lombok](https://projectlombok.org/)

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

* Java 17+
* Maven 3+
* PostgreSQL & Memcached

### Installation


1. Clone the repo
   ```sh
   git clone https://github.com:LokCenter/AZN_Spring_ResourceServer.git
   ```
2. Build Project
   ```sh
   mvn spring-boot:run
   ```

<!-- ROADMAP -->
## Roadmap

- [X] OAuth 2.0 integration
- [ ] Rest API
  - [X] Scopes
  - [ ] DB integration
    - [ ] Memcached support
      - [X] Save DayplanData on read
    - [ ] PostgreSQL support


## Collaborators
<ul>
  <li><a href="https://github.com/chfle">chfle</a> - <a href="mailto:christian.lehnert.home@protonmail.com">christian.lehnert.home@protonmail.com</a></li> 
  <li><a href="https://github.com/lszillat">lszillat</a> - <a href="mailto:leonard.szillat@gmail.com">leonard.szillat@gmail.com</a></li>
</ul>

<!-- CONTACT -->
## Contact

Christian Lehnert - [@ChrLehnert](https://twitter.com/ChrLehnert) - <a href="mailto:christian.lehnert.home@protonmail.com">christian.lehnert.home@protonmail.com</a>

<img width="100" src="readme/profile_chle.jpg">

<p align="right">(<a href="#top">back to top</a>)</p>
