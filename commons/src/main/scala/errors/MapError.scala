package errors

sealed trait MapError extends Throwable

object MapError:
  final case class IllegalMapFormat(message: String) extends MapError:
    override def getMessage: String = message