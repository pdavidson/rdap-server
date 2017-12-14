---
title: Configuring Red Dog's reference implementation
breadcrums: ["Documentation", "documentation.html", "Installation/Configuration", "documentation.html#installationconfiguration", "Option 2 - Overriding SQL Provider queries", "documentation.html#option-2---overriding-sql-provider-queries"]
wheretogo: ["Deploying rdap-server with the customized SQL Provider", "server-install-option-2.html", "Deploying rdap-server with the reference SQL Provider", "server-install-option-3.html"]
---

# {{ page.title }}

## Index

1. [Introduction](#introduction)
1. [Configuring `data-access.properties`](#configuring-data-accessproperties)
   1. [`schema`](#schema)
   1. [`data-access-implementation`](#data-access-implementation)
   1. [`zones`](#zones)
   1. [`is_reverse_ipv4_enabled`](#is_reverse_ipv4_enabled)
   1. [`is_reverse_ipv6_enabled`](#is_reverse_ipv6_enabled)
   1. [`nameserver_as_domain_attribute`](#nameserver_as_domain_attribute)


## Introduction

Red Dog offers a reference data access implementation: **SQL Provider** (git project [rdap-sql-provider](https://github.com/NICMx/rdap-sql-provider)). If used, this implementation can be configured as needed altering the file `WEB-INF/data-access.properties` in the installation directory. Here's a preview of the file's beginning:

```
#Optional. Set the schema that the rdap-sql-provider will use. Default: rdap
#schema =

#Optional. Set the data access implementation class that the rdap-server will use 
#when in the classpath there are two implementations.
#data-access-implementation = 

#Optional. Managed zones (separated by commas), useful only for domains. Example: mx, lat, com. Default: *
# A wildcard "*" can be used to allow the search of domains in all zones (eg. 'zones = *'),
# the wildcard can't be mixed with other zones. Example: isn't valid to use 'zones = com, *'
#zones = 
```

The next section will explain each of the properties that can be configured to customize Red Dog's reference implementation behavior.

## Configuring `data-access.properties`

The `data-access.properties` file has several properties, each one with a specific task. In this section those properties and its expected behavior will be explained.

### `schema`

Database schema that will be used by the SQL Provider when executing the queries. This value will be used to replace the `{schema}` custom parameter at queries.

**Example.** The file [`META-INF/sql/Zone.sql`](https://github.com/NICMx/rdap-sql-provider/blob/master/src/main/resources/META-INF/sql/Zone.sql) has the query `SELECT * FROM {schema}.zone`, assuming that the property `schema` has a value `rdap`, then the query that will be executed at the database is `SELECT * FROM rdap.zone`.

This table shows the specs of the property:

| Required? | Type | Default | Example |
|-----------|------|---------|---------|
| ![No](img/red_x.svg) | String | rdap | schema = mydbschema |

### `data-access-implementation`

Data access implementation class that the server will use as principal whenever the classpath has two implementations. This is explained at [Implementing your Data Access Layer](data-access-layer.html).

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | String | null | data-access-implementation = com.example.MyDataAccessImplementation |

### `zones`

List of managed zones by the server, the character '\*' can be used to state that all the zones will be served. This parameter helps the server to serve only the domains under the zones that are needed by the implementer. If the server receives a request of a domain under an unmanaged zone, then the response will be a **404** HTTP response code.

**Example**. If the DNR manages the zones _abc_, _def_, and _ghi_ but wishes that the server only returns the domains under the zones _abc_ and _ghi_, the property can be configured as follows: `zones = abc, ghi`. If the DNR wishes that all of its zones will be served, then the configuration would be: `zones = *` or `zones = abc, def, ghi`.

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | String (can be a list separated by commas) | * | zones = abc, ghi |

### `is_reverse_ipv4_enabled`

Flag to indicate if the server will respond to reverse IPv6 domain searches. See more about [IPv4 reverse resolution](https://en.wikipedia.org/wiki/Reverse_DNS_lookup#IPv4_reverse_resolution).

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false | is_reverse_ipv4_enabled = true |

### `is_reverse_ipv6_enabled`

Flag to indicate if the server will respond to reverse IPv6 domain searches. See more about [IPv6 reverse resolution](https://en.wikipedia.org/wiki/Reverse_DNS_lookup#IPv6_reverse_resolution).

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false | is_reverse_ipv6_enabled = true |

### `nameserver_as_domain_attribute`

Boolean flag to indicate if **Nameservers** are used as **Domain** object attributes. If the **Nameservers** are used as attributes, then any search by a specific **Nameserver** will be rejected, and the **Nameservers** related to a **Domain** object won’t have nested objects (eg. Links, Status, Events, etc.).

The following table shows some examples on what's expected from the implementation depending on the `nameserver_as_domain_attribute` value (assuming that domain _example.com_ exists and has related nameservers):

| Property value | Search request | Expected response (HTTP response code and content) |
|----------------|---------------------------------------------  |--------------------------|
| false | https://example.com/rdap/domain/example.com | ![Yes](img/green_bkg_check.svg) **200**, domain object with nameservers **including** all nameserver attributes |
| false | https://example.com/rdap/domains?name=example.com | ![Yes](img/green_bkg_check.svg) **200**, domain(s) object(s) with nameservers **including** all nameserver attributes |
| false | https://example.com/rdap/nameserver/ns1.example.com | ![Yes](img/green_bkg_check.svg) **200**, nameserver object with all its attributes |
| false | https://example.com/rdap/nameservers?name=ns1.example.com | ![Yes](img/green_bkg_check.svg) **200**, nameserver(s) object(s) with all its attributes |
| true | https://example.com/rdap/domain/example.com | ![Yes](img/green_bkg_check.svg) **200**, domain object with nameservers **excluding** the nameserver attributes: ipAddresses, status, remarks, links, events, and entities |
| true | https://example.com/rdap/domains?name=example.com | ![Yes](img/green_bkg_check.svg) **200**, domain(s) object(s) with nameservers **excluding** the nameserver attributes: ipAddresses, status, remarks, links, events, and entities |
| true | https://example.com/rdap/nameserver/ns1.example.com | ![No](img/red_x.svg) **501**, error response with description "This server does not implement nameservers requests." |
| true | https://example.com/rdap/nameservers?name=ns1.example.com | ![No](img/red_x.svg) **501**, error response with description "This server does not implement nameservers requests." |

This table shows the specs of the property:

| Required? | Type | Default | Example |
|--------------------|--------|---------|-------------|
| ![No](img/red_x.svg) | Boolean | false | nameserver_as_domain_attribute = true |