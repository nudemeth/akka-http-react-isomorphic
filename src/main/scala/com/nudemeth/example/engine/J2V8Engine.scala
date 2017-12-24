package com.nudemeth.example.engine

import com.eclipsesource.v8.{V8, V8Array}
import com.nudemeth.example.util.NativeUtils
import org.apache.commons.pool2.impl.{DefaultPooledObject, GenericObjectPool}
import org.apache.commons.pool2.{BasePooledObjectFactory, ObjectPool, PooledObject}

import scala.util.{Failure, Success, Try}

object J2V8Engine {
  val instance: J2V8Engine = new J2V8Engine()
  NativeUtils.loadLibraryFromJar(System.mapLibraryName("/libj2v8_win32_x86_64"))
}

sealed class J2V8Engine private(allScripts: Option[String] = None, enginePool: Option[ObjectPool[V8]] = None) extends JavaScriptEngine {

  private val NUM_OF_INITIAL_OBJECT = 50

  private class J2V8EngineFactory(allScripts: String) extends BasePooledObjectFactory[V8] {
    override def create(): V8 = {
      val engine = V8.createV8Runtime()
      engine.executeVoidScript(allScripts)
      engine.getLocker.release()
      engine
    }

    override def destroyObject(p: PooledObject[V8]): Unit = {
      p.getObject.getLocker.acquire()
      p.getObject.release()
      super.destroyObject(p)
    }

    override def wrap(obj: V8): PooledObject[V8] = {
      new DefaultPooledObject[V8](obj)
    }
  }

  override def registerScripts(scripts: Seq[ScriptSource]): JavaScriptEngine = {
    new J2V8Engine(Some(scripts.map{
      case ScriptText(s) => s
      case ScriptURL(s) => scala.io.Source.fromURL(s)("UTF-8").mkString
    }.mkString(sys.props("line.separator"))))
  }

  override def build: J2V8Engine = {
    if (allScripts.isEmpty) {
      throw new UnsupportedOperationException("No scripts have been registered. Please call registerScripts method first.")
    }
    val enginePool = initializeEnginePool(allScripts.get, NUM_OF_INITIAL_OBJECT)
    new J2V8Engine(allScripts, Some(enginePool))
  }

  override def invokeMethod[T](objectName: String, methodName: String, args: Any*): T = synchronized {
    val tryResult = for {
      engine <- tryGetEngine()
      result <- tryInvokeMethod[T](engine, objectName, methodName, args: _*)
    } yield {
      result
    }

    tryResult match {
      case Failure(ex) => throw ex
      case Success(r) => r
    }
  }

  private def initializeEnginePool(script: String, numOfInitialObject: Int): ObjectPool[V8] = {
    val enginePool = new GenericObjectPool[V8](new J2V8EngineFactory(script))
    (0 until numOfInitialObject).foreach(_ => enginePool.addObject())
    enginePool
  }

  private def tryGetEngine(): Try[V8] = Try {
    enginePool.get.borrowObject()
  }

  private def tryInvokeMethod[T](engine: V8, objectName: String, methodName: String, args: Any*): Try[T] = Try {
    engine.getLocker.acquire()
    val obj = engine.getObject(objectName)
    val paramz = new V8Array(engine)
    args.foldLeft(paramz)((params, value) => {
      value match {
        case s:String => params.push(s)
        case i:Int => params.push(i)
        case d:Double => params.push(d)
        case b:Boolean => params.push(b)
        case o => throw new IllegalArgumentException(s"Object type ${o.getClass.getName} is not supported")
      }
    })
    val result = obj.executeStringFunction(methodName, paramz).asInstanceOf[T]
    paramz.release()
    obj.release()
    engine.getLocker.release()
    enginePool.get.returnObject(engine)
    result
  }

  override def destroy: Unit = {
    enginePool.get.close()
    super.destroy
  }
}
