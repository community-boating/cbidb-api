package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.DatabaseField

case class JoinPoint[T <: DatabaseField[_]](a: ColumnAlias[T], b: ColumnAlias[T])