# LambdaTest

*Note to 47 Degrees people. This repo will be changed from private to public once documentation and reviews are done.
Comments, suggestions and pull requests are welcome.
Since the goals are to keep this system small and extensible,
additions of most features that could be easily added as extensions
are not wanted. Instead they should be placed in other optional libraries.*

LambdaTest has the following features.

* Written entirely in Scala.
* Fully functional using immutable testing state.
* Simple with relatively little code (about 1K lines of Scala).
* Easy to customize and extend.
* Can include ScalaCheck properties as tests.
* Tests can be run either via SBT or directly.

## Comparison with ScalaTest and Specs2

| Feature                | LambdaTest | ScalaTest    | Specs2       |
| ---------------------- | ---------- | ------------ | ------------ |
| lines of code          |  ~1K       | ~ 400K       |  ~50K        |
| written in             | Scala      | Scala/Java   | Scala/Java   |
| functional/immutable   | yes        | no           | no           | 
| easy to customize      | yes        | no           | no           |
| easy to generate tests | yes        | no           | no           |
| wrap code              | wrappers   | before/after | before/after |
| supports ScalaCheck    | yes        | yes          | yes          |

LambdaTest has a simple clean fully functional/immutable API that makes it
easy to define new kinds of assertions and compound testing forms (such as wrappers). 
This simple API also provides the option of having code that generates 
an entire test suite rather than having to write each test manually.

Wrappers offer many advantages over use of before and after.

* Single function rather than 2
* Can catch exceptions
* Can hide state need both before and after
* Can be applied at any level

All these features are discussed below with links to demo code that illustrate the
feature.

## Concepts

* **Reporter**. Used to report test results.
* **State**. All state needed to run tests is stored in objects of type `LambdaState`. State objects are immutable. 
* **Action**. Actions transform an old state to a new state and are objects of type `LambdaAct`. Each `LambdaAct` object contains an ordered list of transforms. Each transform maps an old state to a new state. A `LambdaAct` with exactly one transform is called a single action, with no transforms is call an empty action and with two or more transforms is called a multiple action. Actions can be combined using the infix `+` operator.

Actions are created using functions that create objects of type `LambdaAct`. These functions are also informally refered to as actions.

Multiple actions can also be specified as a list of actions. This is done via an implicit conversion from `Seq[LambdaAct]` to `LambdaAct`. See the [Generate](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Generate.scala) 
demo for an example that used this conversion.

## Documentation

* This README file.
* ScalaDoc for APIs.
* Sample demos in `src/test/scala/demo`
* Description of and defaults for options in `src/main/resources/reference.conf`.

## Quick Start

Include LambdaTest jars

    "com.fortyseven" % "lambda-test" % "1.0.0" % "test"
   
Each test file should  
 
    include com.fortyseven.lambdatest._

Each test file class should extend the `LambdaTest` trait.

Each test class must define a local val `act` whose value is the
tests to be run.

If you want to run tests via SBT then the `build.sbt` file should specify

    testFrameworks += new TestFramework("com.fortysevendeg.lambdatest.sbtinterface.LambdaFramework")
   
To see a simple example look at the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
 
## Actions
 
 Actions are created using functions contained in 
 
    src/main/scala/com/fortysevendeg/lambdatest/package.scala
    
Additional actions can be easily defined.  
    
#### Simple Actions

Simple actions do not contain other actions.

* **`assert`**. Tests a boolean predicate. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`assertEq`**. Tests that two values are equal. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`assertEx`**. Test that an exception is raised. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo
* **`assertSC`**. Used to test a ScalaCheck property. See the [ScalaCheck](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/ScalaCheck.scala) demo.
* **`exec`**. Used to insert Scala code. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.

#### Compound Actions

Compound actions contain other actions within themselves.

* **`label`**. Introduced a labeled block of code. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`test`**. Defines a named test. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.

#### Action Nesting Rules

* Assertions must either directly or indirectly inside a test.
* A test may not be directly or indirectly inside another test.

## Running Tests

* **Via SBT**. Classes that extend the `LambdaTest` trait can be run using the SBT test commands.
* **Directly**. This is done using the run function. See the companion objects in the demos. Note these companion objects are not needed for running via SBT.
 
## Immutablility and Side Effects

The testing system itself contains no mutable data. It does however have one kind of side effect: tests results are output. It would have been more pure to move the output entirely after the core functionallity, but it is nice to see test output incrementally as tests are run so a little purity is sacrificed.

Although LambdaTest is ideal for testing pure functional code, it can also be used to test code with mutable state and side effects. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.

## Including Scala Code

Suppose we have a test that contains several assertions.
We can add arbitrary code as follows.

* **Before all assertions**. Put the code in the body of the test action. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.
* **Between assertions**. Add exec actions between assertions. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.
* **After all assertions**. The can be done using a user defined compound action called a wrapper. See the [Wrap](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Wrap.scala) demo. Wrappers are used instead of the before and after features of other testing frameworks. Wrappers avoid the need to use mutable state and can easily capture exceptions

## Exceptions

Expected exceptions can be checked using the assertEx action.

Unexpected exceptions are caught and treated as test failures. Where possible an unexpected exception will not stop later tests from being run.
 
## Parallel Execution

The run function and the label and test actions have an optional parallel parameter.

By default those actions directly inside are run sequentially in order.

If parallel is true, actions directly inside are run in parallel. Note that the output still occurs in the specified order.

## Extensions

LambdaTest is designed to be easily extensible.

* **Test generation**. The clean simple design of LambdaTest makes generation of tests easy. See the [Generate](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Generate.scala) demo for some simple examples.
* **New actions**. New actions are easily defined. See the code in [package.scala](https://github.com/47deg/LambdaTest/blob/master/src/main/scala/com/fortysevendeg/lambdatest/package.scala) for examples. Actions typically use the methods of `LambdaState` and the eval method of `LambdaAct`. These methods should not be called directly in user test code. Wrappers are one kind of user defined action (see the [Wrap](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Wrap.scala) demo).
* **Alternate reporters**. Reporters collect and display the results of tests. Reporters are included for both StdOut (for tests run directly) and SBT (for tests run using the SBT test command). Custom reporters can created by extending the `LambdaReporter` trait.

## Contributing

Comments, suggestions and pull requests are welcome.
Since the goals are to keep this system small and extensible,
additions of most features that could be easily added as extensions
are not wanted. Instead they should be placed in other optional libraries.




    
  

