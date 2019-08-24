package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Filter

case class AliasedFilter(name: String, filter: Filter)
