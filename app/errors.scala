package object errors {

    sealed trait Error extends RuntimeException
    
    case class InvalidArgumentsError(argument: String, description: String)
        extends Error
    
}
