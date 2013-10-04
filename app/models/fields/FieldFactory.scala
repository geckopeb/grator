package models.fields

import models.DB.FieldRow

case class customException(smth:String)  extends Exception

object FieldFactory{
	def get(field: FieldRow): Field = {
		field.fieldType match{
			case "Id" 		=> new IdField(field)
			case "Name" 	=> new NameField(field)
			case "Text" 	=> new TextField(field)
			case "Integer" 	=> new IntegerField(field)
			case "Boolean" 	=> new BooleanField(field)
			case "Related"  => new RelatedField(field)
			case x:String	=> throw new customException("Field type "+x+" not found")
		}
	}
}