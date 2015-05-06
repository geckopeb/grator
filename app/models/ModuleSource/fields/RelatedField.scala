package it.grator.module_source.fields

import it.grator.module_source.Module

import it.grator.utils.TextUtils

case class RelatedField(
	name: String,
	required: Boolean,
	module: Module,
	relatedModule: Module
) extends Field{
	val formType: String = if(this.required) "longNumber" else "optional(longNumber)"
	val relatedClassName = Module.className(this.relatedModuleName)

	def relatedModuleName: String = this.relatedModule.name

	override def htmlForm: String = {
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		   @select(
		   		rowForm("$name"),
		   		models.DB.$relatedClassName.getOptions,
		   		'default -> "-- select --"
		   )"""
	}

	def fieldTable: String = {
		s"""def $name = column[$fieldType]("$tableName"$tableRequired)"""
	}
	
	def fieldType: String = "Long"

	def tableIndex: String = {
		val sqlKeyName = TextUtils.camelToUnderscores(this.moduleName+"_"+this.relatedModuleName+"_"+this.name)

		val relatedTable = Module.externalTableRef(this.relatedModuleName)

		val keyName = this.name+"Key"

		s"""def $keyName = foreignKey("$sqlKeyName", $name, $relatedTable)(_.id)"""
	}
	
	override def list: String = {
		//val varName = Module.varName(this.relatedModuleName)+this.name.capitalize
		s"@$varName.name"
	}
}