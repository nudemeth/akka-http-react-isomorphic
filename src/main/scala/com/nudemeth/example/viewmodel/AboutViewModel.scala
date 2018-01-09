package com.nudemeth.example.viewmodel

final case class AboutViewModel(text: String, title: String) extends ViewModel(title) {
  def this(text: String) = this(text, "About")
}
