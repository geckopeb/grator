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

object AppFactory{

	def construct(
		name: String,
		path: String,
		gModules: List[GratorModule],
		gFields: List[GratorField],
		gRelationships: List[GratorRelationship]
	): App = {

		def generateFields(modules: List[Module]): List[F] = for {
	      gModule <- gModules
	      gField  <- gFields if(gField.moduleId == gModule.id.get)
	    } yield(FF.construct(gField, gModule, gModules, modules))

        val modules = gModules.map( gm => M(gm.name) )

        val fields = generateFields(modules)

        val relationships = gRelationships.map(gr => RF.construct(gr, gModules, modules))

        App(name, path, modules, fields, relationships)
	}
}