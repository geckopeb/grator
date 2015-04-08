package models
import models.DB.GratorRelationship
import utils.{TextUtils => tu}

class Relationship(val rel: GratorRelationship){
	val name = rel.name
	val varName = tu.underscoreToCamel(name)
	val fileName = name.capitalize
	val tableName = "rel_"+tu.camelToUnderscores(name)
	val upName = varName.capitalize
	val tableClass = upName+"T"
	val rowName = upName
	val classRef = "models.DB."+upName

	val tableProp = varName+"T"
	val tableRef = "this."+tableProp

	val pmVar = tu.underscoreToCamel(rel.primaryModule.get.name)
	val pmRowName = rel.primaryModuleModule.get.rowName
	val pmController = rel.primaryModuleModule.get.controllerName
	val pmVarId = pmVar+"Id"
	val pmFindByMethod = "findBy"+pmVarId.capitalize
	val pmTableColumn = tu.camelToUnderscores(pmVar)+"_id"
	val pmUnderscoreName = tu.camelToUnderscores(pmVar)
	val pmIndexVar = tu.underscoreToCamel(rel.primaryModule.get.name)+"Index"
	val pmIndexName = tableName+"_"+pmTableColumn
	val pmTableRef = rel.primaryModuleModule.get.externalTableRef
	val pmClassRef = "models.DB."+pmRowName

	val rmVar = tu.underscoreToCamel(rel.relatedModule.get.name)
	val rmRowName = rel.relatedModuleModule.get.rowName
	val rmController = rel.relatedModuleModule.get.controllerName
	val rmVarId = rmVar+"Id"
	val rmFindByMethod = "findBy"+rmVarId.capitalize
	val rmTableColumn = tu.camelToUnderscores(rmVar)+"_id"
	val rmUnderscoreName = tu.camelToUnderscores(rmVar)
	val rmIndexVar = tu.underscoreToCamel(rel.relatedModule.get.name)+"Index"
	val rmIndexName = tableName+"_"+rmTableColumn
	val rmTableRef = rel.relatedModuleModule.get.externalTableRef
	val rmClassRef = "models.DB."+rmRowName
}