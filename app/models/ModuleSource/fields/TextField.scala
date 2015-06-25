package it.grator.module_source.fields

import it.grator.module_source._

case class TextField(
	name: String,
	required: Boolean,
	module: Module
) extends Field{
	def formType = if(this.required) "nonEmptyText" else "optional(text)"

	def fieldTable: String = {
		s"""def $name = column[String]("$tableName"$tableRequired)"""
	}

	def fieldType: String = "String"
}