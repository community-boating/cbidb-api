package CbiUtil

case class SetDelta[T] (toCreate: Set[T], toUpdate: Set[T], toDestroy: Set[T])