package it.grator.module_source.relationships

import it.grator.module_source.Module
import utils._

trait Relationship{
	def packageName = "models.DB"

	def name: String
	def primaryModule: Module
	def primaryModuleLabel: String
    def primaryModuleSubpanel: String
	def relatedModule: Module
	def relatedModuleLabel: String
    def relatedModuleSubpanel: String

    def className = this.name.capitalize
    def fullClassName = this.packageName+"."+this.className
    def formName = this.className+"Form"
    def varName = this.name
    def varNameId = this.name+"Id"

    def queryName = this.name+"T"
    def tableClassName = this.name.capitalize+"T"
    def tableName = TextUtils.camelToUnderscores(this.name)

    def externalTableRef = {
		val packageName = this.packageName
		val className = this.className
		val queryName = this.queryName

		s"""$packageName.$className.$queryName"""
	}
}