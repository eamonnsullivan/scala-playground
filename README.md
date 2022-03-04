# scala-playground
Bootstrap a console with various versions.



## Scala 2.11 -> 2.12

[Announcement](https://www.scala-lang.org/news/2.12.0/)

 * Traits compile to interfaces, because Java 8+ now allow concrete methods in interfaces.

   Easy to thwart this -- don't define fields, call super, extend a class, etc.

 * Function literals allows for any Single Abstract Method (SAM).

    ```scala

        scala> import java.lang.Runnable
        import java.lang.Runnable

        scala> val r: Runnable = () => println("Run!")
        r: Runnable = $Lambda$4888/1773827130@68bfe64f

        scala> r.run()
        Run!
    ```

   In 2.11, that's a bit more awkward:
   ```scala

   scala> import java.lang.Runnable
   import java.lang.Runnable

   scala> val r: Runnable = () => println("Run!")
   <console>:12: error: type mismatch;
    found   : () => Unit
    required: Runnable
          val r: Runnable = () => println("Run!")
   ```



 * Better type inference (apparently)

   ```scala
        scala> trait MyFun { def apply(x: Int): String }

        defined trait MyFun

        scala> object T {}
        defined object T

        scala> object T {
             | def m(f: Int => String) = 0
             | def m(f: MyFun) = 1
             | }
        defined object T

        scala> T.m(x => x.toString)
        res1: Int = 0
   ```

    I'm not sure I understand this example, because it also worked for me in 2.11.

 * Compiler efficiency improvements
   * Java 8-style bytecode for lambdas

     Less need for generated anonymous classes.

   * More efficient representations of local lazy vals.
   * New optimiser with lots of discrete options.

      By default, adding -opt will just eliminate dead code. -opt:help gives more info on other options, such as inlining.


 * Library changes
   * Either is now right-biased

     This means it now support map, flatMap, contains, toOption, etc. on the right-hand side.

   * Several improvements to Futures

     Long series of blogs here: <https://viktorklang.com/blog/>

     There's a new flatten method, for example for those dreaded futures of futures. But most of this blog series was way over my head.

  * Some breaking changes.

    On 2.12, the built-in type system takes precedence over some implicits, like in SAM thingies:

    ```scala
        scala> trait MySam { def i(): Int }
        defined trait MySam

        scala> implicit def convert(fun: () => Int): MySam = new MySam { def i() = 1 }

        scala> val sam1: MySam = () => 2
        sam1: MySam = $Lambda$5398/1306278882@260346c

        scala> sam1.i
        res1: Int = 2 // implicit ignored
    ```

    While in 2.11

    ```scala
        scala> trait MySam { def i(): Int }
        defined trait MySam

        scala> implicit def convert(fun: () => Int): MySam = new MySam { def i() = 1 }

        scala> val sam1: MySam = () => 2
        sam1: MySam = $anon$1@42221a4a

        scala> sam1.i
        res0: Int = 1 //implicit convertion applied.
    ```


## Scala 2.12 -> 2.13

[Announement](https://github.com/scala/scala/releases/tag/v2.13.0)


 * Big changes to collections.
   * Simpler method signatures (no more CanBuildFrom)
   * Simpler type hierarchy
     * No more Traversable and TraversableOnce
 * Immutable scala.Seq

      Now an  alias for collection.immutable.Seq. This also changes the type of varargs in methods and pattern matches.

    * Alphanumeric method names

       All symbolic operators are just aliases for descriptive alphanumeric method names.
       Examples: \`++\` (alias for concat), \`+:\` (alias for prepended), \`:+\` (alias for appended), and so on.

    * Apparently, collection.View actually works now.

       "a systematic way to turn every collection into a lazy one and vice versa"

       "If xs is some collection, then xs.view is the same collection, but with all transformers implemented lazily. To get back from a view to a strict
        collection, you can use the to conversion operation with a strict collection factory as parameter (e.g. xs.view.to(List))."

       ```scala
           scala> val v = Vector(1 to 10: _*)
           val v = Vector(1 to 10: _*)
           val v: scala.collection.immutable.Vector[Int] = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

           scala> v map (_ + 1) map (_ * 2)
           v map (_ + 1) map (_ * 2)
           val res0: scala.collection.immutable.Vector[Int] = Vector(4, 6, 8, 10, 12, 14, 16, 18, 20, 22)

           scala> v.view map (_ + 1) map (_ * 2)
           v.view map (_ + 1) map (_ * 2)
           val res1: scala.collection.IndexedSeqView[Int] = IndexedSeqView(<not computed>)

           scala> (v.view map (_ + 1) map (_ * 2)).to(Vector)
           (v.view map (_ + 1) map (_ * 2)).to(Vector)
           val res2: scala.collection.immutable.Vector[Int] = Vector(4, 6, 8, 10, 12, 14, 16, 18, 20, 22)
       ```

    * Completely rewritten HashMap and Set.
    * mutable.Stack rewritten and undeprecated.
      immutable.Stack removed.
    * New collections:
      * immutable.LazyList

        replaces immutable.Stream

      * immutable.ArraySeq
      * mutable.CollisionProofHashMap

        Sounds like a dare.

      * mutable.ArrayDeque


   * New to(Collection) method.

     replaces .to[Collection]

   * Added in-place operations on mutable collections

    filterInPlace, mapInPlace, etc.

   * Added .maxOption, .minOption, .maxByOption, .minByOption

     Safely handle the empty case.

 * Standard library
   * Future overhauled (again).

     Focus was on making Future and Promise work better in different failure situations.

 * Language

   Things that jumped out at me: scala.util.Using.

   new `pipe` and `tap`.

   * postFixOps disabled by default.

     ```scala
        scala> val xs = Seq(1, 2, 3, 4)
        val xs = Seq(1, 2, 3, 4)
        val xs: Seq[Int] = List(1, 2, 3, 4)

        scala> xs size
        xs size
                  ^
               error: postfix operator size needs to be enabled
               by making the implicit value scala.language.postfixOps visible.
               This can be achieved by adding the import clause 'import scala.language.postfixOps'
               or by setting the compiler option -language:postfixOps.
               See the Scaladoc for value scala.language.postfixOps for a discussion
               why the feature needs to be explicitly enabled.

        scala> xs.size
        xs.size
        val res9: Int = 4
     ```
  * Underscore is no longer a legal identifier unless backquoted (bug#10384)

     `val _ =` is now a pattern match (and discards the value without incurring a warning)
     `implicit val _ =` is also now a pattern match (and is useless, because it no longer adds to implicit scope)

  * Upgrading from 2.12? Enable -Xmigration while upgrading to request migration advice from the compiler.
  * Literal types
  * Partial unifications
  * by-name implicits
 * compiler 5-10% faster and improved optimiser.
