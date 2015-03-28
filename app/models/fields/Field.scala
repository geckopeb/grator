package models.fields
import models.FieldUtils
import models.DB.FieldRow

trait Field{
	def field: FieldRow
	def controllerForm: String

	lazy val name: String = FieldUtils.underscoreToCamel(this.field.name)

	lazy val moduleVarName: String = field.moduleModule.varName

	def htmlForm: String = {
		val moduleName = field.module.name
		val name = field.name
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		    @helper.inputText(rowForm("$name"))"""
	}
	
	def fieldTable: String

	def nameInTable: String = {
		if(this.field.required){
			field.name	
		} else {
			field.name+".?"
		}
		
	}

	def list: String = {
		val name = this.name
		val varName = this.moduleVarName

		s"@$varName.$name"
	}

	def fieldType: String

	def classDefinition: String = {
		val name = this.name
		val fieldType = this.fieldType
		if(this.field.required){
			s"""$name: $fieldType"""
		} else {
			s"""$name: Option[$fieldType]"""
		}
	}
}