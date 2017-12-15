package com.nudemeth.example.web.engine

import javax.script.{Bindings, CompiledScript}

import com.nudemeth.example.engine._
import jdk.nashorn.api.scripting.{NashornScriptEngine, NashornScriptEngineFactory, ScriptObjectMirror}
import org.apache.commons.pool2.impl.{DefaultPooledObject, GenericObjectPool}
import org.apache.commons.pool2.{BasePooledObjectFactory, ObjectPool, PooledObject}

import scala.util.{Failure, Success, Try}

object NashornEngine {
  val instance: NashornEngine = new NashornEngine()
}

sealed class NashornEngine private(allScripts: Option[String] = None, engine: Option[NashornScriptEngine] = None, bindingsPool: Option[ObjectPool[Bindings]] = None) extends JavaScriptEngine {

  private val NUM_OF_INITIAL_OBJECT = 50

  private class NashornBindingsFactory(engine: NashornScriptEngine, compiledScript: CompiledScript) extends BasePooledObjectFactory[Bindings] {
    override def create(): Bindings = {
      val bindings = engine.createBindings()
      compiledScript.eval(bindings)
      bindings
    }

    override def wrap(obj: Bindings): PooledObject[Bindings] = {
      new DefaultPooledObject[Bindings](obj)
    }
  }

  /*
    Shared engine and compiled script. See links below:
    https://stackoverflow.com/questions/30140103/should-i-use-a-separate-scriptengine-and-compiledscript-instances-per-each-threa
    https://blogs.oracle.com/nashorn/nashorn-multithreading-and-mt-safety
  */
  override def registerScripts(scripts: Seq[ScriptSource]): JavaScriptEngine = {
    new NashornEngine(Some(scripts.map{
      case ScriptText(s) => s
      case ScriptURL(s) => scala.io.Source.fromURL(s)("UTF-8").mkString
    }.mkString(sys.props("line.separator"))))
  }

  override def build: NashornEngine = {
    if (allScripts.isEmpty) {
      throw new UnsupportedOperationException("No scripts have been registered. Please call registerScripts method first.")
    }
    val engine = new NashornScriptEngineFactory()
      .getScriptEngine("-strict", "--no-java", "--no-syntax-extensions")
      .asInstanceOf[NashornScriptEngine]
    val compiledScript = engine.compile(allScripts.get)
    val bindingsPool = initializeBindingsPool(engine, compiledScript, NUM_OF_INITIAL_OBJECT)
    new NashornEngine(allScripts, Some(engine), Some(bindingsPool))
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
    val tryResult = for {
      bindings <- tryGetBindings
      result <- tryInvokeMethod[T](bindings, objectName, methodName, args: _*)
    } yield {
      result
    }

    tryResult match {
      case Failure(ex) => throw ex
      case Success(r) => r
    }
  }

  private def initializeBindingsPool(engine: NashornScriptEngine, compiledScript: CompiledScript , numOfInitialObject: Int): ObjectPool[Bindings] = {
    val bindingsPool = new GenericObjectPool[Bindings](new NashornBindingsFactory(engine, compiledScript))
    (0 until numOfInitialObject).foreach(_ => bindingsPool.addObject())
    bindingsPool
  }

  private def tryGetBindings: Try[Bindings] = Try {
    bindingsPool.get.borrowObject
  }

  private def tryInvokeMethod[T](bindings: Bindings, objectName: String, methodName: String, args: Any*): Try[T] = Try {
    val obj = bindings.get(objectName).asInstanceOf[ScriptObjectMirror]
    val result = obj.callMember(methodName, args.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
    bindingsPool.get.returnObject(bindings)
    result
  }

  override def destroy: Unit = {
    bindingsPool.get.close()
    super.destroy
  }

}