package it.grator.module_source

import models.DB._

import it.grator.module_source.fields.{
  Field => F,
  FieldFactory => FF
}

import it.grator.module_source.{Module => M}

import it.grator.module_source.relationships.{
  Relationship => R,
  RelationshipFactory => RF
}

import it.grator.module_source.subpanels.{
  Subpanel => S,
  SubpanelFactory => SF
}

object AppFactory{

	def construct(
		name: String,
		path: String,
		gModules: List[GratorModule],
		gFields: List[GratorField],
		gRelationships: List[GratorRelationship],
    gSubpanels: List[GratorSubpanel],
    gFieldTypes: List[GratorFieldType]
	): App = {

		def generateFields(modules: List[Module]): List[F] = for {
	      gModule <- gModules
	      gField  <- gFields if(gField.moduleId == gModule.id.get)
	    } yield(FF.construct(gField, gModule, gModules, modules, gFieldTypes))

    def generateSubpanels(modules: List[Module], fields: List[F]): List[S] = for {
      gSubpanel <- gSubpanels
      gToModule <- gModules if gSubpanel.toModule == gToModule.id.get
      gFromModule <- gModules if gSubpanel.fromModule == gFromModule.id.get
      gFromField <- gFields if gSubpanel.fromField == gFromField.id.get
    } yield(SF.construct(gSubpanel, gToModule, gFromModule, gFromField, modules, fields))

        val modules = gModules.map( gm => M(gm.name,gm.hasTab) )

        val fields = generateFields(modules)

        val relationships = gRelationships.map(gr => RF.construct(gr, gModules, modules))

        val subpanels = generateSubpanels(modules, fields)

        App(name, path, modules, fields, relationships, subpanels)
	}
}
