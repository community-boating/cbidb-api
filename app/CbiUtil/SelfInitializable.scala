package CbiUtil

class SelfInitializable[T](selfInitializer: () => T) extends Initializable[T] {
  def selfInitialize(): Unit = {
    if (this.isInitialized) throw new Exception("Attempted to selfInitialize() already initialized value")
    else this.set(selfInitializer())
  }

  override def get: T = {
    if (!this.isInitialized) {
      selfInitialize()
    }
    this.value.get
  }
}
