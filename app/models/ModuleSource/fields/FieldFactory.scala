package it.grator.module_source.fields

import it.grator.module_source._

import models.DB.{GratorField, GratorModule}

case class customException(smth:String)  extends Exception

object FieldFactory{

	def construct(gf: GratorField, gm: GratorModule, gModules: List[GratorModule], modules: List[Module]): Field = {
		def constructRelatedDropdown(module: Module): RelatedDropdownField = {
			val rm = gModules.filter(_.id.get == gf.relatedModuleId.get).head
			val relatedModule = modules.filter(_.name == rm.name).head

			RelatedDropdownField(gf.name, gf.required, module, relatedModule)
		}

		def constructRelatedCombo(module: Module): RelatedComboField = {
			val rm = gModules.filter(_.id.get == gf.relatedModuleId.get).head
			val relatedModule = modules.filter(_.name == rm.name).head

			RelatedComboField(gf.name, gf.required, module, relatedModule)
		}

		val module = modules.filter(_.name == gm.name).head

		gf.fieldType match {
			case "Id" 		=> IdField(gf.name, gf.required, module)
			case "Name" 	=> NameField(gf.name, gf.required, module)
			case "Text" 	=> TextField(gf.name, gf.required, module)
			case "Integer" 	=> IntegerField(gf.name, gf.required, module)
			case "Boolean" 	=> BooleanField(gf.name, gf.required, module)
			case "RelatedDropdown"  => constructRelatedDropdown(module)
			case "RelatedCombo"  => constructRelatedCombo(module)
			case x:String	=> throw new customException(s"""Field type $x not found""")
		}
	}

}