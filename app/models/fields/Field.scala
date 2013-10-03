package models.fields
import models.DB.FieldRow

trait Field{
	def field: FieldRow
	def controllerForm: String
	def htmlForm: String
	def fieldTable: String
	def nameInTable: String
	def list: String
}