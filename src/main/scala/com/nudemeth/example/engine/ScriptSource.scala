package com.nudemeth.example.engine

import java.net.URL

sealed trait ScriptSource
case class ScriptText(script: String) extends ScriptSource
case class ScriptURL(script: URL) extends ScriptSource
