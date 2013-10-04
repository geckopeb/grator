package models.fields

import models.DB.FieldRow

class RelatedField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val name = field.name
		val fieldType = if(field.required){
			"longNumber"
		} else {
			"optional(longNumber)"
		}
		s""""$name" -> $fieldType"""
	}

	def fieldTable: String = {
		val name = field.name
		val fieldType = this.fieldType
		val required = if(field.required){", O.NotNull"} else {""}
		s"""def $name = column[$fieldType]("$name"$required)"""
	}

	def fieldType: String = "Long"

	def tableIndex: String = {
		val name = field.name
		val relatedName = field.relatedModule.get.name
		val keyName = field.module.name+"_"+relatedName+"_"+name
		val relatedTable = relatedName.capitalize+"Table"
		val varName = if(this.name == name){name+"Id"}else{this.name}
		s"""def $varName = foreignKey("$keyName", $name, $relatedTable)(_.id)"""
	}
}