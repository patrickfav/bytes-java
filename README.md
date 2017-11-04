# Bytes Java

[![Download](https://api.bintray.com/packages/patrickfav/maven/bytes-java/images/download.svg)](https://bintray.com/patrickfav/maven/bytes-java/_latestVersion)
[![Build Status](https://travis-ci.org/patrickfav/bytes-java.svg?branch=master)](https://travis-ci.org/patrickfav/bytes-java)
[![Javadocs](https://www.javadoc.io/badge/at.favre.lib/bytes.svg)](https://www.javadoc.io/doc/at.favre.lib/bytes)
[![Coverage Status](https://coveralls.io/repos/github/patrickfav/bytes-java/badge.svg?branch=master)](https://coveralls.io/github/patrickfav/bytes-java?branch=master)


## API Description

Per default the instance is **immutable**, which means any transformation will
create a copy of the internal array (it is, however, possible to get and
modify the internal array). There is a **mutable** version which supports
in-place modification for better performance and a **read-only** version which
restricts the access to the internal array.

### Constructors

There are 3 basic constructors:

 * `wrap()` which reuses the given array reference; this is equivalent to `ByteBuffer.wrap()`
 * `from()` which always creates a new internal array reference (i.e. a copy of the passed reference)
 * `parse()` which parses from binary-text-encoded strings (see other section)

Here is a simple example to show the difference:

```java
byte[] myArray = ...
Bytes bWrap = Bytes.wrap(myArray);
assertSame(myArray, bWrap.array());

byte[] myArray2 = ...
Bytes bFrom = Bytes.from(myArray2);
assertNotSame(myArray2, bFrom.array());
assertArrayEquals(myArray2, bFrom.array());
```

The following code is equivalent:

```java
Bytes.wrap(myArray).copy() ~ Bytes.from(myArray)
```

### More Constructors

**Concatenating** of multiple byte arrays or bytes:

```java
Bytes.from(array1, array2, array3);
Bytes.from((byte) 0x01, (byte) 0x02, (byte) 0x03);
```

Creating byte arrays from **primitive integer** types and arrays:

```java
Bytes.from(8);  //00000000 00000000 00000000 00001000
Bytes.from(1897621543227L);
Bytes.from(1634, 88903, 77263);
```

Initializing **empty arrays** of arbitrary length:

```java
Bytes.allocate(16);
Bytes.allocate(4, (byte) 1); //fill with 0x01
```

Creating **random** byte arrays for e.g. testing:

```java
Bytes.random(12);
```

Reading byte content of encoded `String`s:

```java
Bytes.from(utf8String)
Bytes.from(utf8StringToNormalize, Normalizer.Form.NFKD) //normalizes unicode
Bytes.from(asciiString, StandardCharset.US_ASCII) //any charset
```

And other types:

```java
Bytes.from(byteInputStream); //any inputStream
Bytes.from(byteList); //List<Byte> byteList = ...
Bytes.from(myBitSet); //BitSet myBitSet = ...
Bytes.from(bigInteger);
Bytes.from(file); //reads bytes from any java.io.File
```

For parsing binary-text-encoded strings, see below.

### Transformers

Transformer transform the internal byte array. It is possible to create
custom transformers if a specific feature is not provided by the default
 implementation (see `BytesTransformer`).

```java
Bytes result = Bytes.wrap(array1).transform(myCustomTransformer);
```

#### Built-In Transformers

For **appending** byte arrays or primitive integer types to current instances.
*Note:* this will create a new copy of the internal byte array; for dynamically
 growing byte arrays see `ByteArrayOutputStream`.

```java
Bytes result = Bytes.wrap(array1).append(array2);
Bytes result = Bytes.wrap(array1).append(1341);
Bytes result = Bytes.wrap(array1).append((byte) 3);
```

**Bitwise operations**: XOR, OR, AND, NOT as well as left and right shifts and switching bits:

```java
Bytes result = Bytes.wrap(array).xor(array2);
Bytes result = Bytes.wrap(array).or(array2);
Bytes result = Bytes.wrap(array).and(array2);
Bytes result = Bytes.wrap(array).negate();
Bytes result = Bytes.wrap(array).leftShift(8);
Bytes result = Bytes.wrap(array).rightShift(8);
Bytes result = Bytes.wrap(array).switchBit(3, true);
```

**Copy** operations, which copies the internal byte array to a new instance:

```java
Bytes copy = Bytes.wrap(array).copy();
Bytes copy = Bytes.wrap(array).copy(3, 17); //copy partial array
```

**Resizing** the internal byte array:

```java
Bytes resized = Bytes.wrap(array).resize(3); //from {3, 9, 2, 1} to {9, 2, 1}
```

Other transformers:

```java
Bytes result = Bytes.wrap(array).shuffle();
Bytes result = Bytes.wrap(array).sort(myComparator);
Bytes result = Bytes.wrap(array).reverse();
```

### Parser and Encoder for Binary-Text-Encodings

This library can parse and encode a variety of encodings: binary, decimal, octal,
hex, base36 and base64. Additionally custom parsers are supported by providing your own
implementation:

```java
Bytes.parse("8sK;S*j=r", base85Decoder);
Bytes.encode(base85Encoder);
 ```

**Hex** can be upper and lowercase and also supports `0x` prefix for parsing:

```java
Bytes.parseHex("a0e13eaa1a")
Bytes.parseHex("0xA0E1")

Bytes.from(array).encodeHex() //a0e13eaa1a
 ```

This lib has it's own build in **Base64** encoder:

```java
Bytes.parseBase64("SpT9/x6v7Q==");

Bytes.from(array).encodeBase64(); //"SpT9/x6v7Q=="
 ```

Additionally the following encodings are supported:

```java
Bytes.from(array).encodeBinary(); //1110110110101111
Bytes.from(array).encodeDec(); //20992966904426477
Bytes.from(array).encodeOctal(); //1124517677707527755
Bytes.from(array).encodeBase36(); //5qpdvuwjvu5
```

### Utility Methods

### Validation

A simple validation framework which can be used to check the internal byte array:

```java
Bytes.wrap(new byte[]{8, 3, 9}.validate(BytesValidators.startsWith((byte) 8), BytesValidators.atLeast(3)); // true
```

This is especially convenient when combining validators:

```java
Bytes.wrap(new byte[]{0, 1}.validate(BytesValidators.atMost(2), BytesValidators.notOnlyOf((byte)  0)); // true
```

Validators also support nestable logical expressions AND, OR as well as NOT:

```java
Bytes.allocate(0).validate(or(exactLength(1), exactLength(0))) //true
Bytes.allocate(21).validate(and(atLeast(3), atMost(20))) //true
Bytes.allocate(2).validate(not(onlyOf((byte) 0))); //false
```

Nesting is also possible:

```java
assertTrue(Bytes.allocate(16).validate(
                or(
                   and(atLeast(8),not(onlyOf(((byte) 0)))),
                   or(exactLength(16), exactLength(12)))));
```

### Converting

### Mutable and Read-Only

## Digital Signatures

### Signed Jar

The provided JARs in the Github release page are signed with my private key:

    CN=Patrick Favre-Bulle, OU=Private, O=PF Github Open Source, L=Vienna, ST=Vienna, C=AT
    Validity: Thu Sep 07 16:40:57 SGT 2017 to: Fri Feb 10 16:40:57 SGT 2034
    SHA1: 06:DE:F2:C5:F7:BC:0C:11:ED:35:E2:0F:B1:9F:78:99:0F:BE:43:C4
    SHA256: 2B:65:33:B0:1C:0D:2A:69:4E:2D:53:8F:29:D5:6C:D6:87:AF:06:42:1F:1A:EE:B3:3C:E0:6D:0B:65:A1:AA:88

Use the jarsigner tool (found in your `$JAVA_HOME/bin` folder) folder to verify.

### Signed Commits

All tags and commits by me are signed with git with my private key:

    GPG key ID: 4FDF85343912A3AB
    Fingerprint: 2FB392FB05158589B767960C4FDF85343912A3AB

## Build

### Jar Sign

If you want to jar sign you need to provide a file `keystore.jks` in the
root folder with the correct credentials set in environment variables (
`OPENSOURCE_PROJECTS_KS_PW` and `OPENSOURCE_PROJECTS_KEY_PW`); alias is
set as `pfopensource`.

If you want to skip jar signing just change the skip configuration in the
`pom.xml` jar sign plugin to true:

    <skip>true</skip>

### Build with Maven

Use maven (3.1+) to create a jar including all dependencies

    mvn clean install

## Tech Stack

* Java 7
* Maven

# License

Copyright 2017 Patrick Favre-Bulle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
