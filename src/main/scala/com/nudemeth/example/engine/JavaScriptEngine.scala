package com.nudemeth.example.web.engine

import com.nudemeth.example.engine.ScriptSource

abstract class JavaScriptEngine {
  def registerScripts(scripts: Seq[ScriptSource]): JavaScriptEngine
  def invokeMethod[T](objectName: String, methodName: String, args: Any*): T
  def build: JavaScriptEngine
  def destroy: Unit = {

  }
}
