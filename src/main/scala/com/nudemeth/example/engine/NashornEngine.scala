package com.nudemeth.example.engine

import javax.script.CompiledScript

import jdk.nashorn.api.scripting.{JSObject, NashornScriptEngine, NashornScriptEngineFactory, ScriptObjectMirror}

object NashornEngine {
  private[this] var instance: Option[NashornEngine] = None
  def apply(scripts: Seq[ScriptSource]): NashornEngine = {
    instance match {
      case Some(_) =>
      case None => instance = Some(new NashornEngine(scripts))
    }
    instance.get
  }
}

sealed class NashornEngine(scripts: Seq[ScriptSource]) extends JavaScriptEngine(scripts) {
  /*
    Shared engine and compiled script. See links below:
    https://stackoverflow.com/questions/30140103/should-i-use-a-separate-scriptengine-and-compiledscript-instances-per-each-threa
    https://blogs.oracle.com/nashorn/nashorn-multithreading-and-mt-safety
  */
  private val engine = new NashornScriptEngineFactory()
    .getScriptEngine("-strict", "--no-java", "--no-syntax-extensions")
    .asInstanceOf[NashornScriptEngine]

  private val compiledScript: CompiledScript = {
    val allScript = scripts.map{
      case ScriptText(s) => s
      case ScriptURL(s) => scala.io.Source.fromURL(s)("UTF-8").mkString
    }.mkString(sys.props("line.separator"))
    engine.compile(allScript)
  }

  /**
    * Invoking javascript object method. Always create new bindings when calling this because of multithreading environment
    * @param objectName Object to call
    * @param methodName Method name to call
    * @param args Parameters to pass into the method
    * @tparam T Expected return type
    * @return Instance of expected T type
    */
  override def invokeMethod[T](objectName: String, methodName: String, args: Any*): T = {
    val bindings = engine.createBindings()
    compiledScript.eval(bindings)
    val obj = bindings.get(objectName).asInstanceOf[ScriptObjectMirror]
    obj.callMember(methodName, args.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
  }

}