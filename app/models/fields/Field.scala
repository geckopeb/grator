package models.fields

trait Field{
	def controllerForm: String
	def htmlForm: String
	def fieldTable: String
	def nameInTable: String
}