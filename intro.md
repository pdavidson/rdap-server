---
title: Introduction to RDAP/Red Dog
---

# Introduction to RDAP/Red Dog

## Index

1. [What is RDAP?](#what-is-rdap)
2. [What is Red Dog?](#what-is-red-dog)	
3. [Architecture](#architecture)
	1. [Basic Form](#basic-form)
	2. [Custom Form](#custom-form)
4. [Status](#status)

## What is RDAP?

RDAP (_Registration Data Access Protocol_) is a successor of WHOIS--a protocol used for querying information regarding Internet resources (such as domain names, IP addresses and autonomous system numbers).

Some advantages of RDAP over WHOIS are

- Standardized request and response formats, in contrast to WHOIS' provider-defined arbitrary text.
- Reliance on Representational State Transfer (REST) technologies, a strong base which is widely known.
- Bootstrapping, the automatic determination of where a query should be sent.
- Support for Internationalized Domain Names and support for localized registration data.
- Support for identification, authentication and access control to the service.

## What is Red Dog?

Red Dog is a free and open source Java implementation of an RDAP server *currently under development*.

The Red Dog RDAP server implements the specification defined in RFCs [7480](https://tools.ietf.org/html/rfc7480), [7481](https://tools.ietf.org/html/rfc7481), [7482](https://tools.ietf.org/html/rfc7482), [7483](https://tools.ietf.org/html/rfc7483), and also the following features:

- A default Data-Access layer based on MySQL, which can be overridden by implementing an interface.
- Ability to limit the maximum number of results depending on authentication.
- Support for Nameservers as Domain attributes.
- [Regex Searches](https://www.ietf.org/id/draft-fregly-regext-rdap-search-regex-00.txt).

## Architecture

RDAP is based on a typical client-server model. The server is a RESTful service providing HTTP content in accordance with RFCs [7480](https://tools.ietf.org/html/rfc7480), [7481](https://tools.ietf.org/html/rfc7481), [7482](https://tools.ietf.org/html/rfc7482) and [7483](https://tools.ietf.org/html/rfc7483).

Ideally, Red Dog's database is a separate entity that caches the relevant information from your main database, mainly so DOS attack attempts to your RDAP Server will not disturb your core systems.

![Fig.1 - Architecture Overview](img/diagram/architecture-overview.svg)

> Note: boxes named in low caps are the Red Dog subprojects you can find [here](https://github.com/NICMx).

You can deploy this in two separate ways:

### Basic Form

> ![Warning!](img/warning.svg) The `rdap-migrator` project is still in the testing phase and as such this form cannot be fully deployed still. The migrator will be released soon.

Red Dog's basic database is a predefined relational schema:

![Fig.2 - Basic Architecture](img/diagram/architecture-basic.svg)

If you want to deploy this form, you have to provide the following configuration:

1. [Deploy `rdap-server` and the RDAP Database](server-install-basic.html).
3. [Configure `rdap-migrator`](migration.html), which involves providing export queries that will periodically copy the relevant information from `Your Main Database` to the `RDAP Database` so `rdap-server` can query it.
3. [Adding content to the Help Response](help-response.html)
4. (Optional) Fine-tuning the server:
	1. [configuration.properties](behavior-configuration.html)
	2. [Rate Limit](rate-limit.html)
	3. [User authentication](authentication.html)
		1. [Basic authentication in Tomcat](basic-authentication-tomcat.html)
		2. [Server response privacy](response-privacy.html)
		3. [Optional authentication](optional-authentication.html)

### Custom Form

> ![Warning!](img/warning.svg) This form is still under development. If you wish to implement it prematurely, please consider that certain components, such as the data access API, are bound to change in the near future.

This form allows you to use your own schema and/or database system but requires some programming.

![Fig.3 - Custom Architecture](img/diagram/architecture-advanced.svg)

By implementing the [data access API](https://github.com/NICMx/rdap-data-access-api) as your [Data access layer](data-access-layer.html), you can wrap `Your Main Database` to `rdap-server` in any way you want, which can range as anything from direct queries to `Your Main Database` to queries to non-relational databases.

If you want to deploy this form, you have to provide the following:

2. [Implement your data access layer.](data-access-layer.html)
1. [Deploy `rdap-server`](server-install-custom.html), instructing the `rdap-server` to use your implementation.
3. [Adding content to the Help Response](help-response.html)
4. (Optional) Fine-tuning the server:
	1. [configuration.properties](behavior-configuration.html)
	2. [Rate Limit](rate-limit.html)
	3. [User authentication](authentication.html)
		1. [Basic authentication in Tomcat](basic-authentication-tomcat.html)
		2. [Server response privacy](response-privacy.html)
		3. [Optional authentication](optional-authentication.html)

## Status

There are three development phases planned as of 2016-09-20:

1. Lookup Path Segment, rate-limit, basic authentication, JSON render, help command.
2. Search Path Segment, Digest authentication, rate-limit penalization, Apache Proxy support, access configuration, indexing.
3. Federated Authentication, HTML render, redirection, extensions, internationalization, query cache, client, API, installer.

