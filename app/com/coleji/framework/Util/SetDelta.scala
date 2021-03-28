package com.coleji.framework.Util

case class SetDelta[T](toCreate: Set[T], toUpdate: Set[T], toDestroy: Set[T])
