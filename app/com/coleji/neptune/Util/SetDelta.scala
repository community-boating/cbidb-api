package com.coleji.neptune.Util

case class SetDelta[T](toCreate: Set[T], toUpdate: Set[T], toDestroy: Set[T])
