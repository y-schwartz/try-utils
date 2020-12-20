# try-utils
Utils for writing Java try/catch code blocks in a functional style

[Javadoc](https://htmlpreview.github.io/?https://raw.githubusercontent.com/y-schwartz/try-utils/main/docs/org/yschwartz/try_utils/TryUtils.html)


# Maven dependency

    <dependency>
	    <groupId>org.yschwartz</groupId> 
	    <artifactId>try-utils</artifactId>  
	    <version>1.0.0</version>  
	</dependency>


# Examples
The following:

    TryUtils.tryRunning(() -> {
	    // do stuff that may throw an exception
    }).execute();

Is equal to:

    try {
	    // do stuff that may throw an exception
    } catch (Exception e) {  
	    throw new RuntimeException(e);  
    }

> Note: Any checked exception that is thrown and not caught is wrapped in a runtime exception

The following:

    TryUtils.tryRunning(() -> {
	    // do stuff that may throw an exception
    }).catchException(RuntimeException.class).thenDo(e -> {
	    // do stuff with e
    }).catchException(Exception.class).thenDo(e -> {
	    // do stuff with e
    }).finallyRun(() -> {
	    // do finally stuff
    }).execute();

Is equal to:

    try {
	    // do stuff that may throw an exception
    } catch (RuntimeException e) {  
	    // do stuff with e
    } catch (Exception e) {  
	    // do stuff with e
    } finally {
	    // do finally stuff
    }

> Note: The order of the catch definitions does matter, when an exception is caught the first catch that matches the thrown exception will be executed

The following:

    TryUtils.tryCalling(() -> {
	    // do stuff that may throw an exception and return a value
    }).catchAny().thenReturn(e -> {
	    // do stuff with e and return a value
    }).finallyRun(() -> {
	    // do finally stuff
    }).execute();

Is equal to:

    try {
	    // do stuff that may throw an exception and return a value
    } catch (Exception e) {  
	    // do stuff with e and return a value
    } finally {
	    // do finally stuff
    }
The following:

    AutoCloseable resource;
    TryUtils.tryRunningResource(resource, s -> {
	    // do stuff with the resource s that may throw an exception
    }).catchAny().thenDo(e -> {
	    // do stuff with e
    }).finallyRun(() -> {
	    // do finally stuff
    }).execute();

Is equal to:

    AutoCloseable resource;
    try (AutoCloseable s = resource) {
	    // do stuff with the resource s that may throw an exception
    } catch (Exception e) {  
	    // do stuff with e
    } finally {
	    // do finally stuff
    }
The following:

    AutoCloseable resource;
    TryUtils.tryCallingResource(resource, s -> {
	    // do stuff with the resource s and return a value
    }).catchAny().thenReturn(e -> {
	    // do stuff with e and return a value
    }).finallyRun(() -> {
	    // do finally stuff
    }).execute();

Is equal to:

    AutoCloseable resource;
    try (AutoCloseable s = resource) {
	    // do stuff with the resource s and return a value
    } catch (Exception e) {  
	    // do stuff with e
    } finally {
	    // do finally stuff
    }
