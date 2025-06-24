package org.example.hbank.api.util

import jakarta.servlet.http.HttpServletRequest
import kotlinx.datetime.toKotlinInstant
import org.example.hbank.api.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Clock

@RestControllerAdvice
class GlobalExceptionHandler(private val clock: Clock) {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = exception.bindingResult.fieldErrors
            .mapNotNull(FieldError::getDefaultMessage)
            .joinToString(", ")

        return createErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = message,
            path = request.requestURI
        )
    }

    @ExceptionHandler(UsernameAlreadyExistException::class)
    fun handleUsernameAlreadyExistException(
        exception: UsernameAlreadyExistException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(EmailAlreadyExistException::class)
    fun handleEmailAlreadyExistException(
        exception: EmailAlreadyExistException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(EmailAlreadyVerifiedException::class)
    fun handleEmailAlreadyVerifiedException(
        exception: EmailAlreadyVerifiedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(EmailNotVerifiedException::class)
    fun handleEmailNotVerifiedException(
        exception: EmailAlreadyVerifiedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFoundException(
        exception: EmailNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        exception: UserNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.NOT_FOUND,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleCustomerNotFoundException(
        exception: CustomerNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.NOT_FOUND,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(
        exception: AccountNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.NOT_FOUND,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(RoleNotFoundException::class)
    fun handleRoleNotFoundException(
        exception: RoleNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(
        exception: FileNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.NOT_FOUND,
        message = exception.message ?: "",
        path = request.requestURI
    )

     @ExceptionHandler(VerificationCodeNotFoundException::class)
     fun handleVerificationCodeNotFoundException(
         exception: VerificationCodeNotFoundException,
         request: HttpServletRequest
     ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

     @ExceptionHandler(VerificationCodeExpiredException::class)
     fun handleVerificationCodeExpiredException(
         exception: VerificationCodeExpiredException,
         request: HttpServletRequest
     ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(TransactionNotFoundException::class)
    fun handleTransactionNotFoundException(
        exception: TransactionNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.NOT_FOUND,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(SelfTransactionProhibitedException::class)
    fun handleSelfTransactionProhibitedException(
        exception: SelfTransactionProhibitedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountInvalidStatusException::class)
    fun handleAccountStatusInvalidException(
        exception: AccountInvalidStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountInvalidIdentifierException::class)
    fun handleAccountInvalidIdentifierException(
        exception: AccountInvalidIdentifierException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountTransferAmountLimitException::class)
    fun handleAccountTransferAmountLimitException(
        exception: AccountTransferAmountLimitException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountTransferNumberLimitException::class)
    fun handleAccountTransferNumberLimitException(
        exception: AccountTransferNumberLimitException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    @ExceptionHandler(AccountInsufficientFundsException::class)
    fun handleAccountInsufficientFundsException(
        exception: AccountInsufficientFundsException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(
        status = HttpStatus.BAD_REQUEST,
        message = exception.message ?: "",
        path = request.requestURI
    )

    private fun createErrorResponse(
        status: HttpStatus,
        message: String,
        path: String
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = clock.instant().toKotlinInstant(),
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = path
        )
        return ResponseEntity(errorResponse, status)
    }
}

