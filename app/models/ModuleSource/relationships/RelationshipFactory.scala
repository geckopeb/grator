package it.grator.module_source.relationships

import it.grator.module_source.Module
import models.DB.{GratorRelationship, GratorModule}

case class customException(smth:String)  extends Exception

object RelationshipFactory{
	def construct(gr: GratorRelationship, gModules: List[GratorModule], modules: List[Module]): Relationship = {
		def constructManyToMany: ManyToManyRelationship = {
			val pm = for {
				gModule <- gModules if gr.primaryModuleId == gModule.id.get
				pmodule <- modules if pmodule.name == gModule.name
			} yield pmodule
			val rm = for {
				gModule <- gModules if gr.relatedModuleId == gModule.id.get
				rmodule <- modules if rmodule.name == gModule.name
			} yield rmodule

			ManyToManyRelationship(gr.name, pm.head, gr.primaryModuleLabel, gr.primaryModuleSubpanel, rm.head, gr.relatedModuleLabel, gr.relatedModuleSubpanel)
		}

		gr.relType match {
			case "ManyToMany" => constructManyToMany
			case x:String	=> throw new customException("Relationship type "+x+" not found")
		}
	}
}