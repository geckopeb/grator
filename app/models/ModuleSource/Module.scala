package it.grator.module_source

import it.grator.module_source.fields._
import it.grator.module_source.relationships._
import it.grator.utils._
import views.html._

case class Module(
	name: String //name must be in camelCase
){
	def packageName = Module.packageName

	def varName = Module.varName(this.name)
	def futureVarName = Module.futureVarName(this.name)
	def tupleName = Module.tupleName(this.name)
	def className = Module.className(this.name)
	def fullClassName = Module.fullClassName(this.name)

	def controllerName = Module.controllerName(this.name)
	def tableClassName = Module.tableClassName(this.name)
	def queryName = Module.queryName(this.name)
	def externalTableRef = Module.externalTableRef(this.name)
	def formName = Module.formName(this.name)
	def findByMethod = Module.findByMethod(this.name)
	def findByMethodWithRelateds = Module.findByMethodWithRelateds(this.name)

	def writesMethod = Module.writesMethod(this.name) //toJson
	def relatedComboJsonMethod = Module.relatedComboJsonMethod(this.name)
	def jsRoutes = Module.jsRoutes(this.name)

	def varNameId = Module.varNameId(this.name)
	def tableName = Module.tableName(this.name)
	def pluralName = Module.pluralName(this.name)//TODO add plural name field to module and update this method

	def relationshipTableColumn = Module.relationshipTableColumn(name)
	def relationshipIndexVar = Module.relationshipIndexVar(name)
	def relationshipIndexName = Module.relationshipIndexName(name)

	def hasRelatedFields(app: App) = Module.hasRelatedFields(this, app)

	def templateRow(app: App) = Module.templateRow(this, app)
	def templateId(app: App) = Module.templateId(this, app)

	def varOrTuple(app: App) = Module.varOrTuple(this, app)

	def findByIdMethod(app: App) = Module.findByIdMethod(this, app)

	/*
	ESP: Pendiente implementar tupleType y renombrar tupleType a fullTupleType
	Tuple type no tiene que tener el full path de la clase.
	*/

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

	def detailTupleExtractor(app: App): String = {
		val rels = this.primaryRelationships(app)

		if(rels.isEmpty){
			s"""(Some($varName))"""
		} else {
			val restName = for {rel <- rels} yield ( rel.varName )
			val restNameStr = restName.mkString(", ")

			s"""Some($varName), $restNameStr"""
		}
	}

	def detailParams(app: App): String = {
		val rels = this.primaryRelationships(app)

		if(rels.isEmpty){
			s"""($varName)"""
		} else {
			val restName = for {rel <- rels} yield ( rel.varName )
			val restNameStr = restName.mkString(", ")

			s"""$varName, $restNameStr"""
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

    	FileUtils.writeToFile(path,views.html.templates.module.controller_template(app, this, this.fields(app), this.relatedFields(app), this.primaryRelationships(app)).toString)
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

    	val edit = views.html.templates.module.module_views.edit_template(app, this).toString

    	FileUtils.writeToFile(path, edit)
  	}

  	def generateFormView(app: App): Unit = {
	    val path = this.generatePath(app, "app/views/"+this.name+"/", "form", ".scala.html")
	    val form = 	views.html.templates.module.module_views.form_template(app, this, this.fields(app)).toString

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

	def generateRelatedComboView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/widgets/", "related_combo", ".scala.html")

		/*
		ESP: no es posible interpolar la palabra case en un template,
		La única forma es generandolo como string, lo cual no se puede realizar dentro del mismo,
		Y en este punto no tenemos disponible el campo, este template es el que manejaría a todos
		Los campos del tipo relatedCombo que apuntan al mismo módulo.
		val varName = this.varName
		*/

		val matchExpression = s"""@$varName match{
          case Some(row) => {
            <input id="@{fieldName}_input_search" value="@row.name">
          }
          case None => {
            <input id="@{fieldName}_input_search" value="">
          }
        }"""

		val combo = views.html.templates.module.module_views.widgets.related_combo(app, this, matchExpression).toString

		FileUtils.writeToFile(path, combo)
	}

	def generateRelatedDropdownView(app: App): Unit = {
		val path = this.generatePath(app, "app/views/"+this.name+"/widgets/", "related_dropdown", ".scala.html")

		/*
		ESP: no es posible interpolar la palabra case en un template,
		La única forma es generandolo como string, lo cual no se puede realizar dentro del mismo,
		Y en este punto no tenemos disponible el campo, este template es el que manejaría a todos
		Los campos del tipo relatedCombo que apuntan al mismo módulo.
		val varName = this.varName
		*/

		val matchExpression = s"""@$varName match{
          case Some(row) => {
            <input id="@{fieldName}_input_search" value="@row.name">
          }
          case None => {
            <input id="@{fieldName}_input_search" value="">
          }
        }"""

		val dropdown = views.html.templates.module.module_views.widgets.related_dropdown(app, this, matchExpression).toString

		FileUtils.writeToFile(path, dropdown)
	}

	def generateViews(app: App): Unit = {
		this.generateDetailView(app)
		this.generateEditView(app)
		this.generateFormView(app)
		this.generateIndexView(app)
		this.generateInsertView(app)
		this.generateListView(app)
		this.generateDeleteView(app)
		this.generateRelatedComboView(app)
		this.generateRelatedDropdownView(app)
	}

	def generateRelationshipRow(app: App, rel: Relationship): Unit = {
		val path = this.generatePath(app, "app/models/DB/", rel.className, ".scala")

		val relRow = views.html.templates.relationship.class_template(app, rel).toString

		FileUtils.writeToFile(path, relRow)
	}

	def generateRelationshipViews(app: App, rel: Relationship): Unit = {
		val path = this.generatePath(app, "app/views/"+this.varName+"/subpanels/"+rel.varName+"/","subpanel" ,".scala.html")

		val subpanel = views.html.templates.relationship.subpanel.subpanel_template(app, rel).toString

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
	def futureVarName(name: String) = "future"+name
	def tupleName(name: String) = Module.varName(name)+"Tuple"
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
	def findByMethodWithRelateds(name: String) = Module.findByMethod(name)+"WithRelateds"
	def writesMethod(name: String) = Module.varName(name)+"Writes" //toJson
	def relatedComboJsonMethod(name: String) = "relatedCombo"
	def jsRoutes(name: String) = "jsRoutes"+Module.className(name)

	def varNameId(name: String) = name+"Id"
	//This is the table name in the database.
	def tableName(name: String) = TextUtils.camelToUnderscores(name)
	def pluralName(name: String) = name+"s" //TODO add plural name field to module and update this method

	def relationshipTableColumn(name: String) = TextUtils.camelToUnderscores(name)+"_id"
	def relationshipIndexVar(name: String) = TextUtils.underscoreToCamel(name)+"Index"
	def relationshipIndexName(name: String) = Module.tableName(name)+"_"+Module.relationshipTableColumn(name)

	def hasRelatedFields(module: Module, app: App): Boolean = {
		! module.relatedFields(app).isEmpty
	}

	// ESP templateRow y varOrTuple deberían revisarse...
	def templateRow(module: Module, app: App): String = {
		if(module.hasRelatedFields(app)){
			module.tupleName+"._1"
		} else {
			module.varName
		}
	}

	def templateId(module: Module, app: App): String = {
		Module.templateRow(module,app)+".id.get"
	}

	// ESP templateRow y varOrTuple deberían revisarse...
	def varOrTuple(module: Module, app: App): String = {
		if(module.hasRelatedFields(app)){
			module.tupleName
		} else {
			module.varName
		}
	}

	def findByIdMethod(module: Module, app: App): String = if(module.hasRelatedFields(app)) "findByIdWithRelateds" else "findById"
}
