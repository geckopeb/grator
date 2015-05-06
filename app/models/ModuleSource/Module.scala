package it.grator.module_source

import it.grator.module_source.fields._
import it.grator.module_source.relationships._
import utils._
import views.html._

case class Module(
	name: String //name must be in camelCase
){
	def packageName = Module.packageName

	def varName = Module.varName(this.name)
	def className = Module.className(this.name)
	def fullClassName = Module.fullClassName(this.name)

	def controllerName = Module.controllerName(this.name)
	def tableClassName = Module.tableClassName(this.name)
	def queryName = Module.queryName(this.name)
	def externalTableRef = Module.externalTableRef(this.name)
	def formName = Module.formName(this.name)
	def findByMethod = Module.findByMethod(this.name)
	def varNameId = Module.varNameId(this.name)
	def tableName = Module.tableName(this.name)
	def pluralName = Module.pluralName(this.name)//TODO add plural name field to module and update this method

	def relationshipTableColumn = Module.relationshipTableColumn(name)
	def relationshipIndexVar = Module.relationshipIndexVar(name) 
	def relationshipIndexName = Module.relationshipIndexName(name) 
	
	def getId(hasRelationships: Boolean) = Module.getId(this.name, hasRelationships)

	def tupleType(app: App): String = {
		val rFields = this.relatedFields(app)

		val packageName = this.packageName
		val className = this.className

		val simpleType = s"""$packageName.$className"""

		if(rFields.isEmpty){ 
			simpleType
		} else {
			val restType = for {rField <- rFields} yield (packageName+"."+rField.relatedModule.className)
			val restTypeStr = restType.mkString(", ")

			s"""( $simpleType, $restTypeStr )"""
		}
	}

	def tupleExtractor(app: App): String = {
		val rFields = this.relatedFields(app)

		val varName = this.varName

		if(rFields.isEmpty){
			varName
		} else {
			val restName = for {rField <- rFields} yield ( rField.varName )
			val restNameStr = restName.mkString(", ")

			s"""( $varName, $restNameStr )"""
		}
	}

	def fields(app: App): List[Field] = app.fields.filter(_.module == this)

	def relatedFields(app: App) = this.fields(app).flatMap {
		case rField: RelatedField => Some(rField)
		case _ => None
	}

	def primaryRelationships(app: App): List[Relationship] = {
		app.relationships.filter(_.primaryModule == this)
	}

	def generatePath(app: App, folder: String, fileName: String, fileTermination: String): String = {
	    app.path+folder+fileName+fileTermination
	}

	def generateController(app: App): Unit = {
    	val path = this.generatePath(app, "app/controllers/", this.controllerName, ".scala")

    	FileUtils.writeToFile(path,views.html.templates.module.controller_template(this,this.fields(app), this.relatedFields(app), this.primaryRelationships(app)).toString)
  	}

	def generateClass(app: App): Unit = {
		val path = this.generatePath(app, "app/models/DB/", this.className,".scala")

		FileUtils.writeToFile(path,views.html.templates.module.class_template(app, this,this.fields(app), this.relatedFields(app)).toString)
	}

	def generateDetailView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/", "detail", ".scala.html")

		val detail = views.html.templates.module.module_views.detail_template(app, this, this.relatedFields(app), this.primaryRelationships(app)).toString

		FileUtils.writeToFile(path,detail)
	}

	def generateEditView(app: App): Unit = {
    	val path = this.generatePath(app, "app/views/"+this.name+"/", "edit", ".scala.html")
    	val edit = views.html.templates.module.module_views.edit_template(this).toString

    	FileUtils.writeToFile(path, edit)
  	}

  	def generateFormView(app: App): Unit = {
	    val path = this.generatePath(app, "app/views/"+this.name+"/", "form", ".scala.html")
	    val form = 	views.html.templates.module.module_views.form_template(this, this.fields(app)).toString

	    FileUtils.writeToFile(path,form)
  	}

  	def generateIndexView(app: App): Unit = {
	    val path = this.generatePath(app, "app/views/"+this.name+"/", "index", ".scala.html")

	    val index = views.html.templates.module.module_views.index_template(this, this.relatedFields(app), this.tupleType(app)).toString

	    FileUtils.writeToFile(path,index)
  	}

	def generateInsertView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/", "insert", ".scala.html")

		val insert = views.html.templates.module.module_views.insert_template(this).toString

		FileUtils.writeToFile(path, insert)
	}

	def generateListView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/", "list", ".scala.html")

		val list = views.html.templates.module.module_views.list_template(app, this, this.fields(app), this.relatedFields(app)).toString

		FileUtils.writeToFile(path,list)
	}

	def generateDeleteView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/", "delete", ".scala.html")

		val delete = views.html.templates.module.module_views.delete_template(this).toString

		FileUtils.writeToFile(path,delete)
	}

	def generateViews(app: App): Unit = {
		this.generateDetailView(app)
		this.generateEditView(app)
		this.generateFormView(app)
		this.generateIndexView(app)
		this.generateInsertView(app)
		this.generateListView(app)
		this.generateDeleteView(app)
	}

	def generateRelationshipRow(app: App, rel: Relationship): Unit = {
		val path = this.generatePath(app, "app/models/DB/", rel.className, ".scala")

		val relRow = views.html.templates.relationship.class_template(rel).toString

		FileUtils.writeToFile(path, relRow)
	}

	def generateRelationshipViews(app: App, rel: Relationship): Unit = {
		val path = this.generatePath(app, "app/views/"+this.varName+"/subpanels/"+rel.varName+"/","subpanel" ,".scala.html")

		val subpanel = views.html.templates.relationship.subpanel.subpanel_template(rel).toString

		FileUtils.writeToFile(path,subpanel) 

		val insertPath = this.generatePath(app, "app/views/"+this.varName+"/subpanels/"+rel.varName+"/","insert" ,".scala.html")

		val insert = views.html.templates.relationship.subpanel.insert_relationship_template(this,rel).toString

		FileUtils.writeToFile(insertPath, insert)
	}
	
	def generateRelationship(app: App, rel: Relationship): Unit = {
		this.generateRelationshipRow(app, rel)
		this.generateRelationshipViews(app, rel)
	}

	def generateRelationships(app: App): Unit = {
		for (rel <- this.primaryRelationships(app)){
	      this.generateRelationship(app,rel)
	    }
	}

	def generateAll(app: App): Unit = {
		this.generateController(app)
		this.generateClass(app)
		this.generateViews(app)
		this.generateRelationships(app)
	}
}

object Module {
	def getRelationships(module: Module): List[Relationship] = ???

	def packageName = "models.DB"
	def varName(name: String) = name
	def className(name: String) = name.capitalize
	def fullClassName(name: String) = Module.packageName+"."+Module.className(name)
	def controllerName(name: String) = name.capitalize+"Controller"
	def tableClassName(name: String) = name.capitalize+"T"
	def queryName(name: String) = name+"T"
	
	def externalTableRef(name: String) = {
		val packageName = Module.packageName
		val className = Module.className(name)
		val queryName = Module.queryName(name)

		s"""$packageName.$className.$queryName"""
	}

	def formName(name: String) = name+"Form"
	def findByMethod(name: String) = "findBy"+Module.className(name)
	def varNameId(name: String) = name+"Id"
	//This is the table name in the database.
	def tableName(name: String) = TextUtils.camelToUnderscores(name)
	def pluralName(name: String) = name+"s" //TODO add plural name field to module and update this method

	def relationshipTableColumn(name: String) = TextUtils.camelToUnderscores(name)+"_id"
	def relationshipIndexVar(name: String) = TextUtils.underscoreToCamel(name)+"Index"
	def relationshipIndexName(name: String) = Module.tableName(name)+"_"+Module.relationshipTableColumn(name)

	def getId(name: String, hasRelationships: Boolean): String = {
		val varName = Module.varName(name)
		if(hasRelationships){
			varName+".id.get"
		} else {
			varName+"._1.id.get"
		}
	}

	
}