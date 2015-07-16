package it.grator.module_source.subpanels

import models.DB._
import it.grator.module_source.fields.{Field, RelatedField}
import it.grator.module_source.Module

import play.api.Logger

object SubpanelFactory{
	def construct(
		gSubpanel: GratorSubpanel,
		gToModule: GratorModule,
		gFromModule: GratorModule,
		gFromField: GratorField,
		modules: List[Module],
		fields: List[Field]
	): Subpanel = {
		val toModule = modules.filter(_.name == gToModule.name).head
		val fromModule = modules.filter(_.name == gFromModule.name).head

		val rFields = fields.flatMap {
			case rField: RelatedField => Some(rField)
			case _ => None
		}

		val fromField = rFields.filter(f => (f.module == fromModule && f.name == gFromField.name))

		Subpanel(gSubpanel.name, toModule, fromModule, fromField.head)
	}
}
