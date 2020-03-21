package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.DatabaseField

case class JoinPoint[T](a: ColumnAlias[T, _ <: DatabaseField[T]], b: ColumnAlias[T, _ <: DatabaseField[T]])
