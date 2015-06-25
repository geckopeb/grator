package it.grator.module_source.fields

import it.grator.module_source._

case class IntegerField(
	name: String,
	required: Boolean,
	module: Module
) extends Field{
	def formType = if(this.required) "number" else "optional(number)"

	def fieldTable: String = {
		s"""def $name = column[Int]("$tableName"$tableRequired)"""
	}

	def fieldType: String = "Int"
}