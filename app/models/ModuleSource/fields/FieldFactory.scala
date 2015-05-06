package it.grator.module_source.fields

import it.grator.module_source.Module

import models.DB.{GratorField, GratorModule}

case class customException(smth:String)  extends Exception

object FieldFactory{

	def construct(gf: GratorField, gm: GratorModule, gModules: List[GratorModule], modules: List[Module]): Field = {
		def constructRelated(module: Module): RelatedField = {
			val rm = gModules.filter(_.id.get == gf.relatedModuleId.get).head
			val relatedModule = modules.filter(_.name == rm.name).head

			RelatedField(gf.name, gf.required, module, relatedModule)
		}

		val module = modules.filter(_.name == gm.name).head

		gf.fieldType match {
			case "Id" 		=> IdField(gf.name, gf.required, module)
			case "Name" 	=> NameField(gf.name, gf.required, module)
			case "Text" 	=> TextField(gf.name, gf.required, module)
			case "Integer" 	=> IntegerField(gf.name, gf.required, module)
			case "Boolean" 	=> BooleanField(gf.name, gf.required, module)
			case "Related"  => constructRelated(module)
			case x:String	=> throw new customException("Field type "+x+" not found")
		}
	}

}