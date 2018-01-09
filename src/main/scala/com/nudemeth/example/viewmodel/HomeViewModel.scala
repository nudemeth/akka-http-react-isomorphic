package com.nudemeth.example.viewmodel

final case class HomeViewModel(greeting: String, title: String) extends ViewModel(title) {
  def this(greeting: String) = this(greeting, "Home")
}
