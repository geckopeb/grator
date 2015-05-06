package it.grator.module_source.fields

import it.grator.module_source.Module

case class IdField(
	name: String,
	required: Boolean,
	module: Module
) extends Field{
	def formType: String = "optional(longNumber)"
	def fieldType: String = "Long"
	override def nameInTable: String = "id.?"

	override def controllerForm: String = {
		s""""id" -> $formType"""
	}

	override def htmlForm: String = {
 """@row match {
    	case Some(row) => {
    		<input type="hidden" name="id" value="@row.id.get">
    	}
    	case None => {}
	  }"""
	}

	def fieldTable: String = {
		"""def id = column[Long]("id", O.PrimaryKey, O.AutoInc)"""
	}

	override def list: String = {
		val controllerName = Module.controllerName(this.moduleName)
		val moduleVarName = Module.varName(this.moduleName)
		
		s"""<a href="@controllers.routes.$controllerName.detail($moduleVarName.id.get)">@$moduleVarName.id</a>"""
	}

	override def classDefinition: String = {
		"id: Option[Long] = None"
	}
}