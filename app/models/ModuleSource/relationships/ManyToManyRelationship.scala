package it.grator.module_source.relationships

import it.grator.module_source.Module

case class ManyToManyRelationship(
	name: String,
	primaryModule: Module,
	primaryModuleLabel: String,
    primaryModuleSubpanel: String,
	relatedModule: Module,
	relatedModuleLabel: String,
    relatedModuleSubpanel: String
) extends Relationship{

}