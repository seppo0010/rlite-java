# rlite-java

[![Build Status](https://travis-ci.org/seppo0010/rlite-java.svg?branch=master)](https://travis-ci.org/seppo0010/rlite-java)

Java bindings for rlite. For more information about rlite, go to
[rlite repository](https://github.com/seppo0010/rlite)

## Installation

First install [rlite](https://github.com/seppo0010/rlite#installation)

Then clone this repository.

## Usage

### Using redis-java

```java
import com.rlite.RliteClient;

// ...

RliteClient c = new RliteClient("mydb.rld");
String argv0[] = {"SET", "key", "value"};
c.command(argv0);

String argv1[] = {"GET", "key"};
String reply = (String)c.command(argv1);
// "value"
```
