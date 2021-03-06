package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class All extends LambdaTestRun {
  val act = {
    label("example")(new Example().act) +
    label("except")(new Except().act) +
    label("mutable")(new Mutable().act) +
    label("immutable")(new Immutable().act) +
    label("generate")(new Generate().act) +
    label("wrap")(new Wrap().act) +
    label("scalaCheck")(new ScalaCheck().act) +
    label("parallel")(new Parallel().act) +
    label("tags")(new Tag().act) +
    label("bad")(new Bad().act)
  }
}

object All extends App {
    run("all", new All)
}
