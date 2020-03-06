# Overview

[![build_status](https://travis-ci.org/xxlabaza/netty-spring-boot-starter.svg?branch=master)](https://travis-ci.org/xxlabaza/netty-spring-boot-starter)
[![maven_central](https://maven-badges.herokuapp.com/maven-central/com.xxlabaza.utils/netty-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xxlabaza.utils/netty-spring-boot-starter)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Spring boot starter that provides auto-configuration for [Netty](https://netty.io) clients and servers.

## Usage

The minimal project could look like this:

Maven's **pom.xml**:

```xml
  <dependencies>
    ...
    <dependency>
      <groupId>com.xxlabaza.utils</groupId>
      <artifactId>netty-spring-boot-starter</artifactId>
      <version>1.0.0</version>
    </dependency>
    ...
  </dependencies>
```

Your single **Main.java** with an echo server handler:

```java
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

  public static void main (String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  ChannelHandler echoChannelHandler () {
    return new EchoChannelHandler();
  }

  @Sharable
  class EchoChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead (ChannelHandlerContext context, Object object) throws Exception {
      context.writeAndFlush(object);
    }
  }
}
```

And **application.yml** (see [NettyClientProperties](src/main/java/com/xxlabaza/utils/netty/config/client/NettyClientProperties.java) and [NettyServerProperties](src/main/java/com/xxlabaza/utils/netty/config/server/NettyServerProperties.java) for the full properties lists):

```yml
spring:
  application.name: echo-server

xxlabaza.netty.server:
  # the value could be just port, like here,
  # or a complete string like localhost:9090
  bind: 9090
```

So, the code above creates and runs the Echo server on port **9090**. We can see it during the start process (pay attention on **NettyServersApplicationListener** and **NettyServer** log records):

```bash
> mvn package; java -jar target/my-netty-server-1.0.0.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.5.RELEASE)

2020-03-05 21:38:09.776  INFO 19438 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Starting Main on IB-ALABAZIN-M with PID 19438 (/Users/alabazin/jp/my-netty-server/target/my-netty-server-1.0.0.jar started by alabazin in /Users/alabazin/jp/my-netty-server)
2020-03-05 21:38:09.783  INFO 19438 --- [           main] com.xxlabaza.test.my.netty.server.Main   : No active profile set, falling back to default profiles: default
2020-03-05 21:38:10.466  INFO 19438 --- [           main] .n.c.s.l.NettyServersApplicationListener : starting NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 21:38:10.519  INFO 19438 --- [           main] com.xxlabaza.utils.netty.NettyServer     : started NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 21:38:10.527  INFO 19438 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Started Main in 2.336 seconds (JVM running for 3.144)
```

Yep, that was easy, but let's imagine that we need a second `ChannelHandler` for, let's say, logging. The following code illustrates that situation:

**Main.java**:

```java
import static io.netty.handler.logging.LogLevel.INFO;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class Main {

  public static void main (String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  @Order(0)
  ChannelHandler loggingChannelHandler () {
    return new LoggingHandler(INFO);
  }

  @Bean
  @Order(1)
  ChannelHandler echoChannelHandler () {
    return new EchoChannelHandler();
  }

  @Sharable
  class EchoChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead (ChannelHandlerContext context, Object object) throws Exception {
      context.writeAndFlush(object);
    }
  }
}
```

Note, I used the **@Order** annotation on top of the beans, because I wanted to specify their order in a channel initializer *pipeline*.

After the starting and receiving a message we can see the following picture:

```bash
> mvn package; java -jar target/my-netty-server-1.0.0.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.5.RELEASE)

2020-03-05 21:44:48.600  INFO 19874 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Starting Main on IB-ALABAZIN-M with PID 19874 (/Users/alabazin/jp/my-netty-server/target/my-netty-server-1.0.0.jar started by alabazin in /Users/alabazin/jp/my-netty-server)
2020-03-05 21:44:48.604  INFO 19874 --- [           main] com.xxlabaza.test.my.netty.server.Main   : No active profile set, falling back to default profiles: default
2020-03-05 21:44:49.314  INFO 19874 --- [           main] .n.c.s.l.NettyServersApplicationListener : starting NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 21:44:49.388  INFO 19874 --- [           main] com.xxlabaza.utils.netty.NettyServer     : started NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 21:44:49.400  INFO 19874 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Started Main in 1.37 seconds (JVM running for 2.277)
2020-03-05 21:46:42.707  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] REGISTERED
2020-03-05 21:46:42.708  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] ACTIVE
2020-03-05 21:46:44.280  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] READ: 6B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 70 6f 70 61 0d 0a                               |popa..          |
+--------+-------------------------------------------------+----------------+
2020-03-05 21:46:44.281  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] WRITE: 6B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 70 6f 70 61 0d 0a                               |popa..          |
+--------+-------------------------------------------------+----------------+
2020-03-05 21:46:44.281  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] FLUSH
2020-03-05 21:46:44.283  INFO 19874 --- [orkers-pool-3-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1fa457ae, L:/127.0.0.1:9090 - R:/127.0.0.1:65422] READ COMPLETE
```

Now you can see the logs of the inbound and outbound messages in the console.

Unfortunately, not all `ChannelHandler`s are stateless and can have a **@Share** annotation on top of the class. Sometimes we need a state inside a `ChannelHandler`, and we would like to have separate instances for each new connection. For that purposes, you can use `ChannelHandlerProvider`, which *supplies* the new instances of a specified `ChannelHandler`, like in this example:

```java
...
import static java.lang.Integer.MAX_VALUE;

import com.xxlabaza.utils.netty.handler.ChannelHandlerProvider;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.util.function.Supplier;
...
  @Bean
  @Order(2)
  ChannelHandler lengthFieldBasedFrameDecoder () {
    Supplier<ChannelHandler> supplier = () -> new LengthFieldBasedFrameDecoder(MAX_VALUE, 0, 2);
    return ChannelHandlerProvider.from(supplier);
  }
...
```

In the code above, we created a `ChannelHandler` supplier and put it in the `ChannelHandlerProvider` instance, which will provide the new instances for each new connection in our channel initializer.

Ok. Looks awesome, but what about a client? Let's add one:

In **application.yml** I added a section with the client's settings:

```yml
spring:
  application.name: echo-server

xxlabaza.netty:
  server:
    bind: 9090
  client:
     # the value, same as server.bindm could be just port, like here,
     # or a complete string like localhost:9090
    connect: 9090
```

**Main.java** now contains two `ChannelInitializer`s declarations (**NettyClientChannelInitializer** for client and **NettyServerChannelInitializer** for server) to determine which Spring context's beans to use for the client and which for the server:

```java
import static io.netty.handler.logging.LogLevel.INFO;

import com.xxlabaza.utils.netty.config.client.builder.NettyClientChannelInitializer;
import com.xxlabaza.utils.netty.config.server.builder.NettyServerChannelInitializer;
import com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

  public static void main (String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  NettyClientChannelInitializer clientChannelInitializer () {
    return ChannelHandlerInitializerPipeline.clientPipeline(
        emptyChannelHandler()
    );
  }

  @Bean
  NettyServerChannelInitializer serverChannelInitializer () {
    return ChannelHandlerInitializerPipeline.serverPipeline(
        loggingChannelHandler(),
        echoChannelHandler()
    );
  }

  @Bean
  ChannelHandler loggingChannelHandler () {
    return new LoggingHandler(INFO);
  }

  @Bean
  ChannelHandler echoChannelHandler () {
    return new EchoChannelHandler();
  }

  @Bean
  ChannelHandler emptyChannelHandler () {
    return new EmptyChannelHandler();
  }

  @Sharable
  class EchoChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead (ChannelHandlerContext context, Object object) throws Exception {
      context.writeAndFlush(object);
    }
  }

  @Sharable
  class EmptyChannelHandler extends ChannelInboundHandlerAdapter {

  }
}
```

The client has a single `EmptyChannelHandler` (doesn't do anything) and connects to the server automatically (thanks for **xxlabaza.netty.client.auto-connect** property in `true`, by default). We can see that behavior with the help of the `loggingChannelHandler`:

```bash
> mvn package; java -jar target/my-netty-server-1.0.0.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.5.RELEASE)

2020-03-05 23:17:03.901  INFO 26205 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Starting Main on IB-ALABAZIN-M with PID 26205 (/Users/alabazin/jp/my-netty-server/target/my-netty-server-1.0.0.jar started by alabazin in /Users/alabazin/jp/my-netty-server)
2020-03-05 23:17:03.904  INFO 26205 --- [           main] com.xxlabaza.test.my.netty.server.Main   : No active profile set, falling back to default profiles: default
2020-03-05 23:17:04.677  INFO 26205 --- [           main] .n.c.s.l.NettyServersApplicationListener : starting NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 23:17:04.751  INFO 26205 --- [           main] com.xxlabaza.utils.netty.NettyServer     : started NettyServer(bind=0.0.0.0/0.0.0.0:9090)
2020-03-05 23:17:04.758  INFO 26205 --- [           main] u.n.c.c.l.NettyClientApplicationListener : starting NettyClient(remote=0.0.0.0/0.0.0.0:9090)
2020-03-05 23:17:04.773  INFO 26205 --- [           main] com.xxlabaza.utils.netty.NettyClient     : started NettyClient(remote=0.0.0.0/0.0.0.0:9090)
2020-03-05 23:17:04.782  INFO 26205 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Started Main in 1.408 seconds (JVM running for 2.606)
2020-03-05 23:17:04.799  INFO 26205 --- [       pool-1-1] io.netty.handler.logging.LoggingHandler  : [id: 0x2ab2a057, L:/10.116.52.78:9090 - R:/10.116.52.78:50381] REGISTERED
2020-03-05 23:17:04.800  INFO 26205 --- [       pool-1-1] io.netty.handler.logging.LoggingHandler  : [id: 0x2ab2a057, L:/10.116.52.78:9090 - R:/10.116.52.78:50381] ACTIVE
```

Hey, but what if I want to have several clients or servers in the same Spring application context? Well, there are two special config classes for that: `NettyServerConfig` and `NettyClientConfig`. You can easily use them for creating so many clients and servers as you want:

Let's add one more client and server to the **application.yml**:

```yml
spring:
  application.name: echo-server

xxlabaza.netty:
  server:
    bind: 9990
  client:
    connect: 9990

my.long.prefix.server:
  bind: 9901

my.long.prefix.client:
  connect: 9901
  auto-connect: false
```

Then, we add according configurations to **Main.java**:

```java
import static com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline.clientPipeline;
import static com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline.pipelineOf;
import static com.xxlabaza.utils.netty.handler.ChannelHandlerInitializerPipeline.serverPipeline;
import static io.netty.handler.logging.LogLevel.INFO;

import com.xxlabaza.utils.netty.config.client.NettyClientConfig;
import com.xxlabaza.utils.netty.config.client.builder.NettyClientChannelInitializer;
import com.xxlabaza.utils.netty.config.server.NettyServerConfig;
import com.xxlabaza.utils.netty.config.server.builder.NettyServerChannelInitializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

  public static void main (String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  NettyServerConfig secondServer () {
    return NettyServerConfig.builder()
        .propertiesPrefix("my.long.prefix.server")
        .channelInitializer(pipelineOf(
            echoChannelHandler()
        ))
        .build();
  }

  @Bean
  NettyClientConfig secondClient () {
    return NettyClientConfig.builder()
        .propertiesPrefix("my.long.prefix.client")
        .build();
  }

  @Bean
  NettyClientChannelInitializer clientChannelInitializer () {
    return clientPipeline(
        emptyChannelHandler()
    );
  }

  @Bean
  NettyServerChannelInitializer serverChannelInitializer () {
    return serverPipeline(
        loggingChannelHandler(),
        echoChannelHandler()
    );
  }

  @Bean
  ChannelHandler loggingChannelHandler () {
    return new LoggingHandler(INFO);
  }

  @Bean
  ChannelHandler echoChannelHandler () {
    return new EchoChannelHandler();
  }

  @Bean
  ChannelHandler emptyChannelHandler () {
    return new EmptyChannelHandler();
  }

  @Sharable
  class EchoChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead (ChannelHandlerContext context, Object object) throws Exception {
      context.writeAndFlush(object);
    }
  }

  @Sharable
  class EmptyChannelHandler extends ChannelInboundHandlerAdapter {

  }
}
```

What do we have here?
1. The default client and server, because we specified the **xxlabaza.netty.** prefix in the **application.yml**;
2. a **secondServer** instance, which takes its configuration from properties with the prefix **my.long.prefix.server** and has a manually instantiated channel initializer (if you would like, you can customize the other parts of the `NettyServerConfig`);
3. a **secondClient** bean. It builds from the properties with **my.long.prefix.client** prefix.

So, the two servers will automatically start with the application context, the default client too (see [NettyClientProperties](src/main/java/com/xxlabaza/utils/netty/config/client/NettyClientProperties.java) and [NettyServerProperties](src/main/java/com/xxlabaza/utils/netty/config/server/NettyServerProperties.java) default values accordingly), but the second client needs to be connected manually (`NettyClient.connect` or `NettyClient.send`):

```bash
> mvn package; java -jar target/my-netty-server-1.0.0.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.5.RELEASE)

2020-03-06 02:23:36.263  INFO 30531 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Starting Main on IB-ALABAZIN-M with PID 30531 (/Users/alabazin/jp/my-netty-server/target/my-netty-server-1.0.0.jar started by alabazin in /Users/alabazin/jp/my-netty-server)
2020-03-06 02:23:36.266  INFO 30531 --- [           main] com.xxlabaza.test.my.netty.server.Main   : No active profile set, falling back to default profiles: default
2020-03-06 02:23:37.052  INFO 30531 --- [           main] .n.c.s.l.NettyServersApplicationListener : starting NettyServer(bind=0.0.0.0/0.0.0.0:9990)
2020-03-06 02:23:37.103  INFO 30531 --- [           main] com.xxlabaza.utils.netty.NettyServer     : started NettyServer(bind=0.0.0.0/0.0.0.0:9990)
2020-03-06 02:23:37.103  INFO 30531 --- [           main] .n.c.s.l.NettyServersApplicationListener : starting NettyServer(bind=0.0.0.0/0.0.0.0:9901)
2020-03-06 02:23:37.104  INFO 30531 --- [           main] com.xxlabaza.utils.netty.NettyServer     : started NettyServer(bind=0.0.0.0/0.0.0.0:9901)
2020-03-06 02:23:37.107  INFO 30531 --- [           main] u.n.c.c.l.NettyClientApplicationListener : starting NettyClient(remote=0.0.0.0/0.0.0.0:9990)
2020-03-06 02:23:37.128  INFO 30531 --- [           main] com.xxlabaza.utils.netty.NettyClient     : started NettyClient(remote=0.0.0.0/0.0.0.0:9990)
2020-03-06 02:23:37.133  INFO 30531 --- [           main] com.xxlabaza.test.my.netty.server.Main   : Started Main in 1.263 seconds (JVM running for 2.339)
2020-03-06 02:23:37.143  INFO 30531 --- [orkers-pool-7-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1442fa3d, L:/192.168.236.7:9990 - R:/192.168.236.7:51918] REGISTERED
2020-03-06 02:23:37.144  INFO 30531 --- [orkers-pool-7-2] io.netty.handler.logging.LoggingHandler  : [id: 0x1442fa3d, L:/192.168.236.7:9990 - R:/192.168.236.7:51918] ACTIVE
```

Also, you may want to just configure a `Bootstrap` or a `ServerBootstrap` instance (add additional options or, for example, use a shared `ByteBufAllocator`) before using them, so for that, you can use [NettyClientBootstrapConfigurer](src/main/java/com/xxlabaza/utils/netty/config/client/builder/NettyClientBootstrapConfigurer.java) or [NettyServerBootstrapConfigurer](src/main/java/com/xxlabaza/utils/netty/config/server/builder/NettyServerBootstrapConfigurer.java) interfaces like this:

```java
import com.xxlabaza.utils.netty.config.server.builder.NettyServerBootstrapConfigurer;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.stereotype.Component;

@Component
class MyCustomServerBootstrapConfigurer implements NettyServerBootstrapConfigurer {

  @Override
  public void configure (ServerBootstrap serverBootstrap) {
    // do your awesome configuration here
  }
}
```

## Development

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

For building the project you need only a [Java compiler](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

> **IMPORTANT:** the project requires Java version starting from **8**

And, of course, you need to clone the project from GitHub:

```bash
$> git clone https://github.com/xxlabaza/netty-spring-boot-starter
$> cd netty-spring-boot-starter
```

### Building

For building routine automation, I am using [maven](https://maven.apache.org).

To build the project, do the following:

```bash
$> ./mvnw compile
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.970 s
[INFO] Finished at: 2020-03-05T21:23:42+03:00
[INFO] ------------------------------------------------------------------------
```

### Running the tests

To run the project's test, do the following:

```bash
$> ./mvnw test
...
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  19.383 s
[INFO] Finished at: 2020-03-05T21:24:42+03:00
[INFO] ------------------------------------------------------------------------
```

Also, if you do `package` or `install` goals, the tests launch automatically.

## Deploy

To deploy the project in Maven Central, use the following command:

```bash
$> ./mvnw \
    -DskipAllTests=true \
    -Dspotbugs.skip=true \
    -Dpmd.skip=true \
    -Dcheckstyle.skip \
    -Dmaven.javadoc.skip=false \
    --settings .settings.xml \
    deploy -B
```

It maybe usefull to import `gpg`'s secret keys and ownertrust from somewhere:

```bash
$> echo "${GPG_SECRET_KEYS}" | base64 --decode | "${GPG_EXECUTABLE}" --batch --passphrase "${GPG_PASSPHRASE}" --import
...
$> echo "${GPG_OWNERTRUST}" | base64 --decode | "${GPG_EXECUTABLE}" --batch --passphrase "${GPG_PASSPHRASE}" --import-ownertrust
...
```

## Built With

* [Java](http://www.oracle.com/technetwork/java/javase) - is a systems and applications programming language

* [Lombok](https://projectlombok.org) - is a java library that spicing up your java

* [Junit](http://junit.org/junit4/) - is a simple framework to write repeatable tests

* [AssertJ](http://joel-costigliola.github.io/assertj/) - AssertJ provides a rich set of assertions, truly helpful error messages, improves test code readability

* [Maven](https://maven.apache.org) - is a software project management and comprehension tool

## Changelog

To see what has changed in recent versions of the project, see the [changelog](./CHANGELOG.md) file.

## Contributing

Please read [contributing](./CONTRIBUTING.md) file for details on my code of conduct, and the process for submitting pull requests to me.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/appulse-projects/utils-java/tags).

## Authors

* **[Artem Labazin](https://github.com/xxlabaza)** - creator and the main developer

## License

This project is licensed under the Apache License 2.0 License - see the [license](./LICENSE) file for details
