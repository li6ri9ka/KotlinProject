package org.example.domain.error

open class DomainException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : DomainException(message)
class AccessDeniedException(message: String) : DomainException(message)
class ValidationException(message: String) : DomainException(message)
