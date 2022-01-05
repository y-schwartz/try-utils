# try-utils

A Java util for writing try/catch blocks in a functional style

### Maven dependency:

```xml
<dependency>
 <groupId>org.yschwartz</groupId>
 <artifactId>try-utils</artifactId>
 <version>1.0</version>
</dependency>
```

### Examples

Simple try

`Try.of(() -> foo()).execute();`

`Try.of(() -> foo()).catchAny().thenDo(e -> log.error(e)).execute();`

`Try.of(() -> foo()).catchException(RuntimeException.class).thenDo(e -> log.error(e)).execute();`

`Try.of(() -> foo()).finallyDo(() -> bar()).execute();`

Retry

`Try.of(() -> foo()).retry().filter(ExceptionA.class).execute();`

`Try.of(() -> foo()).retry().fixedDelay(100).maxAttempts(10).execute();`

Try with resource (auto close resource)

`Try.of(resource, r -> foo(r)).execute();`