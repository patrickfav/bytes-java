# Bytes Utility Library for Java

Bytes is a utility library that makes it easy to **create**, **parse**, **transform**,
**validate** and **convert** byte arrays in Java. It's main class `Bytes` is
a collections of bytes and the main API. It supports [endianness](https://en.wikipedia.org/wiki/Endianness)
as well as **copy-on-write** and **mutable** access, so the caller may decide to favor
performance. This can be seen as combination of the features provided by
[`BigInteger`](https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html),
[`ByteBuffer`](https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html) but
providing a lot of additional features on the micro and macro level of byte arrays (similar to Okio's [ByteString](https://github.com/square/okio)). The main goal is to minimize the need
to blindly paste code snippets from
[s](https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java)
[t](https://stackoverflow.com/questions/12893758/how-to-reverse-the-byte-array-in-java)
[a](https://stackoverflow.com/questions/3329163/is-there-an-equivalent-to-memcpy-in-java)
[c](https://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays)
[k](https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java)
[o](https://stackoverflow.com/questions/14243922/java-xor-over-two-arrays)
[v](https://stackoverflow.com/questions/28997781/bit-shift-operations-on-a-byte-array-in-java)
[e](https://stackoverflow.com/questions/13109588/base64-encoding-in-java)
[r](https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream)
[f](https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet)
[l](https://stackoverflow.com/questions/4231674/converting-an-array-of-bytes-to-listbyte)
[o](https://stackoverflow.com/questions/28703273/sorting-byte-arrays-in-numeric-order)
[w](https://stackoverflow.com/questions/4385623/bytes-of-a-string-in-java)
[.](https://stackoverflow.com/questions/23360692/byte-position-in-java)
[c](https://stackoverflow.com/questions/11437203/byte-array-to-int-array)
[o](https://stackoverflow.com/a/9670279/774398)
[m](https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array)

[![Download](https://api.bintray.com/packages/patrickfav/maven/bytes-java/images/download.svg)](https://bintray.com/patrickfav/maven/bytes-java/_latestVersion)
[![Build Status](https://travis-ci.com/patrickfav/bytes-java.svg?branch=master)](https://travis-ci.com/patrickfav/bytes-java)
[![Javadocs](https://www.javadoc.io/badge/at.favre.lib/bytes.svg)](https://www.javadoc.io/doc/at.favre.lib/bytes)
[![Coverage Status](https://coveralls.io/repos/github/patrickfav/bytes-java/badge.svg?branch=master)](https://coveralls.io/github/patrickfav/bytes-java?branch=master) 
[![Maintainability](https://api.codeclimate.com/v1/badges/43b7770f0ee00b85f92a/maintainability)](https://codeclimate.com/github/patrickfav/bytes-java/maintainability)

It's main features include:

* **Creation** from a wide variety of sources: multiple arrays, integers, [streams](https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html), random, strings, files, uuid, ...
* **Transformation** with many built-in: append, [xor](https://en.wikipedia.org/wiki/Exclusive_or), [and](https://en.wikipedia.org/wiki/Logical_conjunction), [hash](https://en.wikipedia.org/wiki/Cryptographic_hash_function), [shifts](https://en.wikipedia.org/wiki/Bitwise_operation#Bit_shifts), shuffle, reverse, [checksum](https://en.wikipedia.org/wiki/Checksum), ...
* **Validators** with the ability to arbitrarily combine multiple ones with logical expressions
* **Parsing and Encoding** in most common binary-to-text-encodings: [hex](https://en.wikipedia.org/wiki/Hexadecimal), [base32](https://en.wikipedia.org/wiki/Base32), [base64](https://en.wikipedia.org/wiki/Base64), ...
* **Immutable, Mutable and Read-Only** versions
* **Handling Strings** with encoding and normalizing strings for arbitrary charset
* **Utility Features** like `indexOf`, `count`, `isEmpty`, `bitAt`, `contains` ...
* **Flexibility** provide your own Transformers, Validators and Encoders

The code is compiled with target [Java 7](https://en.wikipedia.org/wiki/Java_version_history#Java_SE_7) to keep backwards compatibility with *Android* and older *Java* applications.
It is lightweight as it does not require any additional dependencies.

## Quickstart

Add dependency to your `pom.xml` ([check latest release](https://github.com/patrickfav/bytes-java/releases)):

```xml
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bytes</artifactId>
    <version>{latest-version}</version>
</dependency>
```

_Note:_ There is a byte-code optimized version (powered by [ProGuard](https://www.guardsquare.com/en/products/proguard)) which can be used with [classifier](https://maven.apache.org/pom.html#Maven_Coordinates) 'optimized'. This may have issues so use at your own risk.

Some simple examples:

```java
Bytes b = Bytes.wrap(someByteArray);  //reuse given reference
b.copy().reverse(); //reverse the bytes on a copied instance
String hex = b.encodeHex(); //encode base16/hex
```

```java
Bytes b = Bytes.parseHex("0ae422f3");  //parse from hex string
int result = b.toInt(); //get as signed int
```

```java
Bytes b = Bytes.from(array1);  //create from copy of array1
b.resize(2).xor(array2); //shrink to 2 bytes and xor with other array
byte[] result = b.array(); //get as byte array
```

## API Description

Per default the instance is **semi-immutable**, which means any transformation will
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

#### More Constructors

For a **null-safe version**, which uses the empty array in case of a null byte array:

```java
Bytes.wrapNullSafe(null);
Bytes.fromNullSafe(null);
```

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
Bytes.from(0.7336f, -87263.0f);
Bytes.from(0.8160183296, 3984639846.0);
```

Initializing **empty arrays** of arbitrary length:

```java
Bytes.allocate(16);
Bytes.allocate(4, (byte) 1); //fill with 0x01
Bytes.empty(); //creates zero length byte array
```

Creating cryptographically secure **random** byte arrays:

```java
Bytes.random(12);
```

Creating cryptographically unsecure **random** byte arrays for e.g. testing:

```java
Bytes.unsecureRandom(12, 12345L); // using seed makes it deterministic
```

Reading byte content of encoded `String`s:

```java
Bytes.from(utf8String)
Bytes.from(utf8StringToNormalize, Normalizer.Form.NFKD) //normalizes unicode
Bytes.from(asciiString, StandardCharset.US_ASCII) //any charset
```

And other types:

```java
Bytes.from(byteInputStream); //read whole java.io.InputStream
Bytes.from(byteInputStream, 16); //read java.io.InputStream with length limitation
Bytes.from(byteList); //List<Byte> byteList = ...
Bytes.from(myBitSet); //java.util.BitSet myBitSet = ...
Bytes.from(bigInteger); //java.math.BigInteger
Bytes.from(file); //reads bytes from any java.io.File
Bytes.from(dataInput, 16); //reads bytes from any java.io.DataInput
Bytes.from(UUID.randomUUID()); //read 16 bytes from UUID
```

For parsing binary-text-encoded strings, see below.

### Transformers

Transformers transform the internal byte array. It is possible to create
custom transformers if a specific feature is not provided by the default
 implementation (see `BytesTransformer`). Depending on the type (mutable vs
 immutable) and transformer it will overwrite the internal byte array
 or always create a copy first.

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
Bytes result = Bytes.wrap(array1).append("some string");
```

**Bitwise operations**: XOR, OR, AND, NOT as well as left and right shifts and switching bits:

```java
Bytes.wrap(array).xor(array2); // 0010 0011 xor() 1011 1000 = 1001 1011
Bytes.wrap(array).or(array2); // 0010 0011 or() 1101 0100 = 1111 0111
Bytes.wrap(array).and(array2); // 0010 0011 and() 1011 1000 = 0010 0000
Bytes.wrap(array).not(); // 0010 0011 negate() = 1101 1100
Bytes.wrap(array).leftShift(8);
Bytes.wrap(array).rightShift(8);
Bytes.wrap(array).switchBit(3, true);
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

**Hashing** the internal byte array using the [`MessageDigest`](https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html) Java crypto API:

```java
Bytes hash = Bytes.wrap(array).hashSha256();
Bytes hash = Bytes.wrap(array).hashSha1();
Bytes hash = Bytes.wrap(array).hashMd5();
Bytes hash = Bytes.wrap(array).hash("SHA-512");
```

**Reversing** of the byte order in the array

```java
Bytes result = Bytes.wrap(array).reverse();
```

#### Additional Transformers

More transformers can be accessed through the `BytesTransformers`, which
can be statically imported for a less verbose syntax:

```java
import static at.favre.lib.bytes.BytesTransformers.*;
```

**HMAC** used to calculate [keyed-hash message authentication code](https://en.wikipedia.org/wiki/HMAC):

```java
Bytes.wrap(array).transform(hmacSha256(macKey32Byte));
Bytes.wrap(array).transform(hmacSha1(macKey20Byte));
Bytes.wrap(array).transform(hmac(macKey16Byte,"HmacMd5"));
```

**Checksum** can be calculated or automatically appended:

```java
Bytes.wrap(array).transform(checksumAppendCrc32());
Bytes.wrap(array).transform(checksumCrc32());
Bytes.wrap(array).transform(checksum(new Adler32(), ChecksumTransformer.Mode.TRANSFORM, 4));
```

**GZip compression** is supported by [`GZIPInputStream`](https://docs.oracle.com/javase/7/docs/api/java/util/zip/GZIPInputStream.html):

```java
Bytes compressed = Bytes.wrap(array).transform(compressGzip());
Bytes decompressed = compressed.transform(decompressGzip());
```

**Sorting** of individual bytes with either [`Comparator`](https://docs.oracle.com/javase/7/docs/api/java/util/Comparator.html) or natural order:

```java
Bytes.wrap(array).transform(sort()); // 0x00 sorts after 0xff
Bytes.wrap(array).transform(sortUnsigned()); // 0xff sorts after 0x00
Bytes.wrap(array).transform(sort(byteComparator));
```

**Shuffling** of individual bytes:

```java
Bytes.wrap(array).transform(shuffle());
```

### Parser and Encoder for Binary-Text-Encodings

This library can parse and encode a variety of encodings: binary, decimal, [octal](https://en.wikipedia.org/wiki/Octal),
[hex](https://en.wikipedia.org/wiki/Hexadecimal) and
[base64](https://en.wikipedia.org/wiki/Base64). Additionally custom parsers are supported by providing your own
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
Bytes.from(array).encodeBase64Url(); //"SpT9_x6v7Q=="
 ```

also a **Base32** encoder (using the RFC4648 non-hex alphabet):

```java
Bytes.parseBase32("MZXQ====");
Bytes.from(array).encodeBase32();
 ```

Additionally the following radix encodings are supported:

```java
Bytes.from(array).encodeBinary(); //1110110110101111
Bytes.from(array).encodeDec(); //20992966904426477
Bytes.from(array).encodeOctal(); //1124517677707527755
Bytes.from(array).encodeRadix(36); //5qpdvuwjvu5
```

### Handling Strings

You can easily get the **UTF-8 encoded version** of a string with

```java
String s = "...";
Bytes.from(s);
```

or get the **[normalized version](https://en.wikipedia.org/wiki/Unicode_equivalence)**,
which is the recommended way to convert e.g. user names

```java
String pwd = "ℌH";
Bytes.from(pwd, Normalizer.Form.NFKD); //would be "HH" normalized
```

or get as any other **[character encodings](https://en.wikipedia.org/wiki/Character_encoding)**

```java
String asciiString = "ascii";
Bytes.from(asciiString, StandardCharsets.US_ASCII);
```

To easily append a string to an byte array you can do

```java
String userPwdHash = ...;
Bytes.from(salt).append(userPwd).hashSha256();
```

### Utility Methods

Methods that return additional information about the instance.

Finding occurrence of specific bytes:

```java
Bytes.wrap(array).contains((byte) 0xE1);
Bytes.wrap(array).indexOf((byte) 0xFD);
Bytes.wrap(array).indexOf(new byte[] {(byte) 0xFD, 0x23});
Bytes.wrap(array).indexOf((byte) 0xFD, 5); //search fromIndex 5
Bytes.wrap(array).lastIndexOf((byte) 0xAE);
Bytes.wrap(array).startsWith(new byte[] {(byte) 0xAE, 0x32});
Bytes.wrap(array).endsWidth(new byte[] {(byte) 0xAE, 0x23});
```

Length checks:

```java
Bytes.wrap(array).length();
Bytes.wrap(array).lengthBit(); //8 * array.length
Bytes.wrap(array).isEmpty();
```

Accessing part of the array as primitives from arbitrary position:

```java
Bytes.wrap(array).bitAt(4); // 0010 1000 -> false
Bytes.wrap(array).byteAt(14); // 1111 1111 -> -1
Bytes.wrap(array).unsignedByteAt(14); // 1111 1111 -> 255
Bytes.wrap(array).intAt(4);
Bytes.wrap(array).longAt(6);
```

And others:

```java
Bytes.wrap(array).count(0x01); //occurrences of 0x01
Bytes.wrap(array).count(new byte[] {0x01, 0xEF}); //occurrences of pattern [0x01, 0xEF]
Bytes.wrap(array).entropy();
```

Of course all standard Java Object methods are implemented including:
`hashCode()`, `equals()`, `toString()` as well as it being
[`Comparable`](https://docs.oracle.com/javase/7/docs/api/java/lang/Comparable.html).
In addition there is a constant time `equalsConstantTime()` method, see [here](https://codahale.com/a-lesson-in-timing-attacks/) why this
might be useful.

The `toString()` methods only shows the length and a preview of maximal 8 bytes:

```
16 bytes (0x7ed1fdaa...12af000a)
```

Bytes also implements the `Iterable` interface, so it can be used in a
foreach loop:

```java
for (Byte aByte : bytesInstance) {
    ...
}
```

The `equals` method has overloaded versions for `byte[]`, `Byte[]` and `ByteBuffer` which can be used to directly
compare the inner array:

```java
byte[] primitiveArray1 = ...
byte[] primitiveArray2 = ...
Bytes.wrap(primitiveArray1).equals(primitiveArray2); //compares primitiveArray1 with primitiveArray2
```

### Validation

A simple validation framework which can be used to check the internal byte array:

```java
import static at.favre.lib.bytes.BytesValidators.*;

Bytes.wrap(new byte[]{8, 3, 9}).validate(startsWith((byte) 8), atLeast(3)); // true
```

This is especially convenient when combining validators:

```java
Bytes.wrap(new byte[]{0, 1}).validate(atMost(2), notOnlyOf((byte)  0)); // true
```

Validators also support nestable logical expressions AND, OR as well as NOT:

```java
Bytes.allocate(0).validate(or(exactLength(1), exactLength(0))) //true
Bytes.allocate(19).validate(and(atLeast(3), atMost(20))) //true
Bytes.allocate(2).validate(not(onlyOf((byte) 0))); //false
```

Nesting is also possible:

```java
assertTrue(Bytes.allocate(16).validate(
                or(
                   and(atLeast(8),not(onlyOf(((byte) 0)))),
                   or(exactLength(16), exactLength(12))))); // true
```

### Converting

The internal byte array can be converted or exported into many different formats.
There are 2 different kinds of converters:

* Ones that create a new type which **reuses the same shared memory**
* Ones that create a **copy** of the internal array, which start with `to*`

#### Shared Memory Conversion

Not technically a conversation, but it is of course possible to access the internal array:

```java
Bytes.wrap(array).array();
```

Conversion to [`InputStream`](https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html)
 and [`ByteBuffer`](https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html):

```java
Bytes.wrap(array).inputStream();
Bytes.wrap(array).buffer();
```

If you just want a duplicated instance, sharing the same array:

```java
Bytes.wrap(array).duplicate();
```

For the conversion to read-only and mutability, see below.

#### Copy Conversion

To primitives (if the internal array is not too long)

```java
Bytes.wrap(array).toByte();
Bytes.wrap(array).toUnsignedByte();
Bytes.wrap(array).toInt();
Bytes.wrap(array).toDouble();
```

To primitive arrays

```java
Bytes.wrap(array).toIntArray(); // of type int[]
Bytes.wrap(array).toLongArray(); // of type long[]
```

To other collections

```java
Bytes.wrap(array).toList(); // of type List<Byte>
Bytes.wrap(array).toBoxedArray(); // of type Byte[]
Bytes.wrap(array).toBitSet(); //of type java.util.BitSet
```

to [`BigInteger`](https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html) of course

```java
Bytes.wrap(array).toBigInteger();
```

and others

```java
Bytes.wrap(array).toUUID(); // convert 16 byte to UUID
Bytes.wrap(array).toCharArray(StandardCharsets.UTF-8); // converts to encoded char array
```

### Mutable and Read-Only

Per default the instance is immutable, i.e. every transformation will create a
a new internal byte array (very similar to the API of `BigInteger`). While
this is usually the default way to design such a construct because it shows
[various advantages](https://softwareengineering.stackexchange.com/questions/151733/if-immutable-objects-are-good-why-do-people-keep-creating-mutable-objects)
this can introduce a major performance issue when handling big arrays
or many transformations.

#### Mutable Bytes

All transformers (if possible) reuse or overwrite the same internal memory
to avoid unneeded array creation to minimize time and space complexity.
To create a mutable instance just do:

```java
MutableBytes b = Bytes.from(array).mutable();
```

Mutable classes also enable further APIs for directly modify the internal array:

```java
b.setByteAt(3, (byte) 0xF1)
b.overwrite(anotherArray) //directly overwrite given array
b.fill(0x03) // fills with e.g. 3
b.wipe() //fills with zeros
b.secureWipe() //fills with random data
```

Create a immutable version again with:

```java
Bytes b2 = b.immutable();
```

*Note:* a copy will inherit mutability/read-only properties:

```java
Bytes b = Bytes.from(array).mutable().copy();
assertTrue(b.isMutable());
```

##### AutoClosable for try-with-resources

In security-relevant environments it is best practice to wipe the memory of secret data, such as
secret keys. This can be used with Java 7 feature try-with-resource like this:

```java
try (MutableBytes b = Bytes.wrap(aesBytes).mutable()) {
    SecretKey s = new SecretKeySpec(b.array(), "AES");
    ...
}
```

#### Readonly Bytes

On the other hand, if you want a export a instance with limited access,
especially no easy way to alter the internal byte array, read-only instances
may be created by:

```java
Bytes b = Bytes.from(array).readOnly();
```

Every call to the following conversation methods will throw a `ReadOnlyBufferException`:

```java
readOnlyBytes.array();
readOnlyBytes.byteBuffer();
readOnlyBytes.inputStream();
```

## Download

The artifacts are deployed to [jcenter](https://bintray.com/bintray/jcenter) and [Maven Central](https://search.maven.org/).

### Maven

Add the dependency of the [latest version](https://github.com/patrickfav/bytes/releases) to your `pom.xml`:

```xml
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bytes</artifactId>
    <version>{latest-version}</version>
</dependency>
```

### Gradle

Add to your `build.gradle` module dependencies:

    implementation group: 'at.favre.lib', name: 'bytes', version: '{latest-version}'

### Local Jar Library

[Grab jar from latest release.](https://github.com/patrickfav/bytes/releases/latest)

### OSGi

The library should be prepared to be used with the OSGi framework with the help of the [bundle plugin](http://felix.apache.org/documentation/subprojects/apache-felix-maven-bundle-plugin-bnd.html).


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

Use the Maven wrapper to create a jar including all dependencies

    mvnw clean install

### Checkstyle Config File

This project uses my [`common-parent`](https://github.com/patrickfav/mvn-common-parent) which centralized a lot of
the plugin versions as well as providing the checkstyle config rules. Specifically they are maintained in [`checkstyle-config`](https://github.com/patrickfav/checkstyle-config). Locally the files will be copied after you `mvnw install` into your `target` folder and is called
`target/checkstyle-checker.xml`. So if you use a plugin for your IDE, use this file as your local configuration.

## Tech Stack

* Java 7 (+ [errorprone](https://github.com/google/error-prone) static analyzer)
* Maven

# Credits

* Byte util methods derived from `primitives.Bytes` from [Google Guava](https://github.com/google/guava) (Apache v2)
* Entropy class derived from [Twitter Commons](https://github.com/twitter/commons) (Apache v2)
* Base64 implementation and some util methods from [Okio](https://github.com/square/okio) (Apache v2)

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
