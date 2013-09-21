package utils.persistence

trait PersistenceManager{
	def save[Row <: RowManager](row: Row):Row
}