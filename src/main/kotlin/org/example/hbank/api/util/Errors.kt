package org.example.hbank.api.util

class UsernameAlreadyExistException: RuntimeException(Errors.USERNAME_ALREADY_EXISTS)
class EmailAlreadyExistException: RuntimeException(Errors.EMAIL_ALREADY_EXISTS)
class EmailNotFoundException: RuntimeException(Errors.EMAIL_NOT_FOUND)
class EmailAlreadyVerifiedException: RuntimeException(Errors.EMAIL_ALREADY_VERIFIED)
class EmailNotVerifiedException: RuntimeException(Errors.EMAIL_NOT_VERIFIED)
class UserNotFoundException: RuntimeException(Errors.USER_NOT_FOUND)
class CustomerNotFoundException: RuntimeException(Errors.CUSTOMER_NOT_FOUND)
class AccountNotFoundException: RuntimeException(Errors.ACCOUNT_NOT_FOUND)
class RoleNotFoundException: RuntimeException(Errors.ROLE_NOT_FOUND)
class FileNotFoundException: RuntimeException(Errors.FILE_NOT_FOUND)
class VerificationCodeNotFoundException: RuntimeException(Errors.VERIFICATION_CODE_NOT_FOUND)
class VerificationCodeExpiredException: RuntimeException(Errors.VERIFICATION_CODE_EXPIRED)
class TransactionNotFoundException: RuntimeException(Errors.TRANSACTION_NOT_FOUND)
class SelfTransactionProhibitedException: RuntimeException(Errors.ACCOUNT_IS_SAME)
class AccountInvalidStatusException: RuntimeException(Errors.ACCOUNT_INVALID_STATUS)
class AccountInvalidIdentifierException : RuntimeException(Errors.ACCOUNT_INVALID_IDENTIFIER)
class AccountTransferAmountLimitException : RuntimeException(Errors.ACCOUNT_TRANSFER_AMOUNT_LIMIT)
class AccountTransferNumberLimitException : RuntimeException(Errors.ACCOUNT_TRANSFER_NUMBER_LIMIT)
class AccountRequestAmountLimitException : RuntimeException(Errors.ACCOUNT_REQUEST_AMOUNT_LIMIT)
class AccountRequestNumberLimitException : RuntimeException(Errors.ACCOUNT_REQUEST_NUMBER_LIMIT)
class AccountInsufficientFundsException : RuntimeException(Errors.ACCOUNT_INSUFFICIENT_FUNDS)
class UnauthorizedException : RuntimeException(Errors.INVALID_USERNAME_OR_PASSWORD)
object Errors {

    const val EMAIL_IS_BLANK = "HB_VAL0101"
    const val EMAIL_INVALID_FORMAT = "HB_VAL0102"

    const val USERNAME_IS_BLANK = "HB_VAL0201"
    const val USERNAME_INVALID_SIZE = "HB_VAL0202"
    const val USERNAME_INVALID_CHARACTERS = "HB_VAL0203"

    const val PASSWORD_IS_BLANK = "HB_VAL0301"
    const val PASSWORD_INVALID_SIZE = "HB_VAL0302"
    const val PASSWORD_MISSING_DIGIT = "HB_VAL0303"
    const val PASSWORD_MISSING_UPPERCASE = "HB_VAL0304"
    const val PASSWORD_MISSING_LOWERCASE = "HB_VAL0305"
    const val PASSWORD_MISSING_SPECIAL_CHAR = "HB_VAL0306"

    const val UUID_INVALID_FORMAT = "HB_VAL0401"

    const val VERIFICATION_CODE_IS_BLANK = "HB_VAL0501"
    const val VERIFICATION_CODE_INVALID = "HB_VAL0502"

    const val ACCOUNT_IS_SAME = "HB_VAL0601"
    const val ACCOUNT_INVALID_STATUS = "HB_VAL0602"
    const val ACCOUNT_INVALID_IDENTIFIER = "HB_VAL0603"
    const val ACCOUNT_INSUFFICIENT_FUNDS = "HB_VAL0604"
    const val ACCOUNT_TRANSFER_AMOUNT_LIMIT = "HB_VAL0605"
    const val ACCOUNT_TRANSFER_NUMBER_LIMIT = "HB_VAL0606"
    const val ACCOUNT_REQUEST_AMOUNT_LIMIT = "HB_VAL0607"
    const val ACCOUNT_REQUEST_NUMBER_LIMIT = "HB_VAL0608"

    const val TRANSACTION_REFERENCE_IS_BLANK = "HB_VAL0701"
    const val TRANSACTION_REFERENCE_INVALID_FORMAT = "HB_VAL0702"

    const val INVALID_USERNAME_OR_PASSWORD = "HB_VAL0801"

    const val EMAIL_ALREADY_EXISTS = "HB_PRS0101"
    const val EMAIL_ALREADY_VERIFIED = "HB_PRS0102"
    const val EMAIL_NOT_VERIFIED = "HB_PRS0103"
    const val EMAIL_NOT_FOUND = "HB_PRS0104"

    const val USERNAME_ALREADY_EXISTS = "HB_PRS0201"

    const val USER_NOT_FOUND = "HB_PRS0301"

    const val CUSTOMER_NOT_FOUND = "HB_PRS0401"

    const val ACCOUNT_NOT_FOUND = "HB_PRS0501"

    const val ROLE_NOT_FOUND = "HB_PRS0601"

    const val FILE_NOT_FOUND = "HB_PRS0701"

    const val VERIFICATION_CODE_NOT_FOUND = "HB_PRS0801"
    const val VERIFICATION_CODE_EXPIRED = "HB_PRS0802"

    const val TRANSACTION_NOT_FOUND = "HB_PRS0901"

    //

    const val CREDENTIAL_EXPIRED = "Credential expired"
    const val TRANSFER_UNKNOWN_IDENTIFIER_TYPE = "Unknown identifier type"
    const val REQUEST_UNKNOWN_IDENTIFIER_TYPE = "Unknown identifier type"
    const val FROM_ACCOUNT_NOT_FOUND = "From account not found"
    const val TO_ACCOUNT_NOT_FOUND = "To account not found"

    const val TOKEN_INVALID = "HB_etk0402"
    const val TOKEN_EXPIRED = "HB_etk0403"

    const val REQUEST_SENDER_USER_NOT_FOUND = "HB_erq0901"
    const val REQUEST_SENDER_CUSTOMER_NOT_FOUND = "HB_erq0902"
    const val REQUEST_SENDER_ACCOUNT_NOT_FOUND = "HB_erq0903"
    const val REQUEST_RECIPIENT_USER_NOT_FOUND = "HB_erq0904"
    const val REQUEST_RECIPIENT_CUSTOMER_NOT_FOUND = "HB_erq0905"
    const val REQUEST_RECIPIENT_ACCOUNT_NOT_FOUND = "HB_erq0906"
    const val REQUEST_DAILY_LIMIT = "HB_erq0907"
    const val REQUEST_MONTHLY_LIMIT = "HB_erq0908"
    const val REQUEST_YEARLY_LIMIT = "HB_erq0909"
    const val REQUEST_AMOUNT_LIMIT = "HB_erq0910"
    const val REQUEST_NUMBER_LIMIT = "HB_erq0911"
    const val REQUEST_MONTHLY_NUMBER_LIMIT = "HB_erq0912"
    const val REQUEST_YEARLY_NUMBER_LIMIT = "HB_erq0913"
    const val REQUEST_SAME_ACCOUNT = "HB_erq0914"
    const val REQUEST_SENDER_ACCOUNT_DEACTIVATED = "HB_erq0915"
    const val REQUEST_RECIPIENT_ACCOUNT_DEACTIVATED = "HB_erq0916"
    const val REQUEST_NOT_FOUND = "HB_erq0917"
    const val REQUEST_ALREADY_ACCEPTED = "HB_erq0918"
    const val REQUEST_ALREADY_DECLINED = "HB_erq0919"
    const val REQUEST_ALREADY_VERIFIED = "HB_erq0920"
    const val REQUEST_ALREADY_COMPLETED = "HB_erq0921"
    const val REQUEST_ALREADY_CANCELED = "HB_erq0922"
    const val REQUEST_NOT_ACCEPTED = "HB_erq0923"
    const val REQUEST_NOT_RECIPIENT = "HB_erq0924"
    const val REQUEST_NOT_REQUEST = "HB_erq0925"
    const val REQUEST_INSUFFICIENT_BALANCE = "HB_erq0926"
    const val REQUEST_DEACTIVATED_ACCOUNT = "HB_erq0927"

    const val TRANSFER_PAYER_USER_NOT_FOUND = "HB_etf1001"
    const val TRANSFER_PAYER_CUSTOMER_NOT_FOUND = "HB_etf1002"
    const val TRANSFER_PAYER_ACCOUNT_NOT_FOUND = "HB_etf1003"
    const val TRANSFER_RECIPIENT_USER_NOT_FOUND = "HB_etf1004"
    const val TRANSFER_RECIPIENT_CUSTOMER_NOT_FOUND = "HB_etf1005"
    const val TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND = "HB_etf1006"
    const val TRANSFER_MONTHLY_LIMIT = "HB_etf1008"
    const val TRANSFER_YEARLY_LIMIT = "HB_etf1009"
    const val TRANSFER_AMOUNT_LIMIT = "HB_etf1010"
    const val TRANSFER_NUMBER_LIMIT = "HB_etf1011"
    const val TRANSFER_MONTHLY_NUMBER_LIMIT = "HB_etf1012"
    const val TRANSFER_YEARLY_NUMBER_LIMIT = "HB_etf1013"
    const val TRANSFER_SAME_ACCOUNT = "HB_etf1014"
    const val TRANSFER_DEACTIVATED_ACCOUNT = "HB_etf1015"
    const val TRANSFER_RECIPIENT_DEACTIVATED_ACCOUNT = "HB_etf1016"
    const val TRANSFER_INSUFFICIENT_BALANCE = "HB_etf1017"
    const val TRANSFER_NOT_FOUND = "HB_etf1018"
    const val TRANSFER_NOT_TRANSFER = "HB_etf1019"
    const val TRANSFER_NOT_PAYER = "HB_etf1020"
    const val TRANSFER_ALREADY_VERIFIED = "HB_etf1021"
    const val TRANSFER_ALREADY_COMPLETED = "HB_etf1022"
    const val TRANSFER_ALREADY_CANCELED = "HB_etf1023"



    // REGISTER USER RESPONSE



}

