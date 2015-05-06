package it.grator.module_source.fields

import it.grator.module_source.Module

trait Field{
	def name: String
	def formType: String
	def required: Boolean
	def fieldType: String
	def fieldTable: String
	def module: Module

	def moduleName: String = this.module.name
	def varName: String = this.name
	def tableName: String = this.name
	def moduleVarName: String = Module.varName(this.moduleName)

	def tableRequired = if(this.required){", O.NotNull"} else {", O.Nullable"}
	
	def controllerForm: String = {
		s""""$name" -> $formType"""
	}

	def htmlForm: String = {
		s"""<legend>
				@Messages("$moduleName.$name")
		   </legend>
		    @helper.inputText(rowForm("$name"))"""
	}

	def nameInTable: String = {
		if(this.required){
			this.name	
		} else {
			this.name+".?"
		}
		
	}

	def list: String = {
		s"@$moduleVarName.$name"
	}

	def classDefinition: String = {
		if(this.required){
			s"""$name: $fieldType"""
		} else {
			s"""$name: Option[$fieldType]"""
		}
	}
}