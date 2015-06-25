package it.grator.module_source.fields

import it.grator.module_source._

import utils._

import play.api.Logger

trait RelatedField extends Field{
  def relatedModule: Module
  
  val formType: String = if(this.required) "longNumber" else "optional(longNumber)"
  val relatedClassName = Module.className(this.relatedModuleName)

  def relatedModuleName: String = this.relatedModule.name

  def fieldTable: String = {
    s"""def $name = column[$fieldType]("$tableName"$tableRequired)"""
  }
  
  def fieldType: String = "Long"

  def tableIndex: String = {
    val sqlKeyName = TextUtils.camelToUnderscores(this.moduleName+"_"+this.relatedModuleName+"_"+this.name)
    Logger.debug(sqlKeyName)

    val relatedTable = Module.externalTableRef(this.relatedModuleName)

    val keyName = this.name+"Key"

    s"""def $keyName = foreignKey("$sqlKeyName", $name, $relatedTable)(_.id)"""
  }
  
  override def list: String = {
    //val varName = Module.varName(this.relatedModuleName)+this.name.capitalize
    s"@$varName.name"
  }
}