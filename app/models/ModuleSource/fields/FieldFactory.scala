package it.grator.module_source.fields

import it.grator.module_source._

import models.DB.{GratorField, GratorModule, GratorFieldType}

case class customException(smth:String)  extends Exception

object FieldFactory{

	def construct(gf: GratorField, gm: GratorModule, gModules: List[GratorModule], modules: List[Module], fieldTypes: List[GratorFieldType]): Field = {
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

		val fType = fieldTypes.filter(_.id.get == gf.fieldType).headOption

		fType match {
			case Some(gft: GratorFieldType) if gft.name == "Id" 		=> IdField(gf.name, gf.required, module)
			case Some(gft: GratorFieldType) if gft.name == "Name" 	=> NameField(gf.name, gf.required, module)
			case Some(gft: GratorFieldType) if gft.name == "Text" 	=> TextField(gf.name, gf.required, module)
			case Some(gft: GratorFieldType) if gft.name == "Integer" 	=> IntegerField(gf.name, gf.required, module)
			case Some(gft: GratorFieldType) if gft.name == "Boolean" 	=> BooleanField(gf.name, gf.required, module)
			case Some(gft: GratorFieldType) if gft.name == "RelatedDropdown"  => constructRelatedDropdown(module)
			case Some(gft: GratorFieldType) if gft.name == "RelatedCombo"  => constructRelatedCombo(module)
			case Some(gft: GratorFieldType) => {
				val ftn = gft.name
				throw new customException(s"""Field type $ftn not found""")
			}
			case _	=> throw new customException(s"""Field type not found""")
		}
	}

}
