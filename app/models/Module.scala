package models
import models.DB.ModuleRow
import utils.{TextUtils => tu}

class Module(val module: ModuleRow){
	val name = module.name
	val varName = tu.underscoreToCamel(name)
	val upName = varName.capitalize
	val controllerName = upName+"Controller"
	val rowName = upName
	val tableClass = upName+"T"
	val tableProp = varName+"T"
	val tableRef = "this."+tableProp
	val externalTableRef = rowName+"."+tableProp
	val formName = upName+"Form"
	val pluralName = varName+"s" //TODO add plural name field to module and update this method
}