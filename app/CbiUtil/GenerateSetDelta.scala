package CbiUtil

object GenerateSetDelta {
	// return (toCreate, toUpdate, toDestroy)
	def apply[T](
						authoritative: Set[T],
						slave: Set[T],
						getID: (T => String),
						getHash: (T => String) = (t: T) => t.hashCode().toString
				): SetDelta[T] = {
		def toMap(ts: Set[T]): Map[String, (String, T)] = ts.foldLeft(Map.empty: Map[String, (String, T)])((map, t) => map + (getID(t) -> (getHash(t), t)))

		val authoritativeById: Map[String, (String, T)] = toMap(authoritative)
		val slaveById: Map[String, (String, T)] = toMap(slave)

		val authoritativeIDs = authoritativeById.keySet
		val slaveIDs = slaveById.keySet

		val toCreateIDs = authoritativeIDs -- slaveIDs
		val toDeleteIDs = slaveIDs -- authoritativeIDs
		val toUpdateIDs = (authoritativeIDs intersect slaveIDs).filter(id => authoritativeById(id)._1 != slaveById(id)._1)
		toUpdateIDs.foreach(id => {
			println("authoritative: " + authoritativeById(id)._1)
			println(authoritativeById(id)._2)
			println("slave: " + slaveById(id)._1)
			println(slaveById(id)._2)
		})

		SetDelta(
			toCreate = toCreateIDs.map(id => authoritativeById(id)._2),
			toUpdate = toUpdateIDs.map(id => authoritativeById(id)._2),
			toDestroy = toDeleteIDs.map(id => slaveById(id)._2)
		)
	}
}
