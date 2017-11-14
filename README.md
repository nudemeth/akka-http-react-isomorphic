# akka-http-react-isomorphic

An example of light weight isomorphic web application using Scalatra as a back-end framework and React as a front-end framework. Nashorn is used for server rendering.

## Prerequisites ##
1. Java >= 1.8.0_144
2. NodeJs >= 6.11.3
3. sbt >= 1.0.2

## Build & Run ##

```sh
$ cd [working directory]
$ ./sbt
> run
```
Then open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Packaging ##

```sh
$ cd [working directory]
$ ./sbt
> assembly
```

`assembly` task will compile your project, run your tests, and then pack your class files, all your dependencies and webapp resources into a single JAR file: **target/scala_X.X.X/projectname-assembly-X.X.X.jar**. If you want more configurable, please have a look at [sbt-assembly](https://github.com/sbt/sbt-assembly).

## Bugs and Issues ##
Have a bug or an issue with this template? [Open a new issue](https://github.com/nudemeth/scalatra-react-isomorphic/issues)

## License ##
Code released under the [Unlicense](https://github.com/nudemeth/scalatra-react-isomorphic/blob/master/LICENSE) license

