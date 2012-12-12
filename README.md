#Loggly Cloud Connector

## Introduction
Logs messages into loggly without blocking the mule app. All the messages are saved in a circular queue before being sent to Loggly. A different thread consumes the messages stored in it.

## Module docs
Docs can be found [here](http://mulesoft.github.com/loggly-connector/).

## FAQ
### Why not using the `logger` message processor that is shipped by default?
The main reason is the ability to save the logs in the cloud. Loggly is a specialized service that supports filtering and searching through logs.

### What happens if the circular queue reaches its maxium size and I append a new element to it?
The older message is discarded and replaced with a new one. By no means we are ensuring that everything that you log using this component will be persisted into loggly. The policy is best-effort and no-blocking above all.

### Can I use this connector in CloudHub?
Hell, yes! That's the kind of use case we are aiming at.

### I have some XYZ requirement different than yours, can I contribute or add something to it?
Yes, please! Add a pull request and we can go over it.

# Usage

Add the following requirement to your pom.xml:

```xml
<dependency>
    <groupId>org.mule.modules</groupId>
    <artifactId>mule-module-loggly</artifactId>
    <version>1.2.1</version>
</dependency>
```

The following snippet shows how to use it inside a flow:

```xml
...
<loggly:config inputKey="${inputKey}" />
...

<flow name="testFlow">
    <loggly:logger message="This a test."/>
</flow>
```

That will log `"This is a test."` each time the flow `testFlow` is executed. 


# Authors

  * Emiliano Lesende (@3miliano) - Idea
  * Mariano de Achaval - Concurrency advisor
  * Alberto Pose (@thepose) - Implementation
  * (Add your name here!)

# License
Copyright 2012 MuleSoft, Inc.

Licensed under the Common Public Attribution License (CPAL), Version 1.0.

### Happy Hacking!

