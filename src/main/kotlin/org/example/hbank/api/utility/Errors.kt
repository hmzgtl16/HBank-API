package org.example.hbank.api.utility

object Errors {

    const val USERNAME_ALREADY_EXISTS = "0wpe_eun0101"
    const val USERNAME_INVALID = "0wpe_eun0102"

    const val EMAIL_ALREADY_EXISTS = "0wpe_eel0201"
    const val EMAIL_INVALID = "0wpe_eel0202"
    const val EMAIL_NOT_FOUND = "0wpe_eel0203"
    const val EMAIL_NOT_VERIFIED = "0wpe_eel0204"
    const val EMAIL_ALREADY_VERIFIED = "0wpe_eel0205"

    const val PASSWORD_INVALID = "0wpe_epw0301"
    const val PASSWORD_NOT_MATCHES = "0wpe_epw0302"

    const val TOKEN_NOT_FOUND = "0wpe_etk0401"
    const val TOKEN_INVALID = "0wpe_etk0402"
    const val TOKEN_EXPIRED = "0wpe_etk0403"

    const val USER_NOT_FOUND = "0wpe_eus0501"

    const val CUSTOMER_NOT_FOUND = "0wpe_ecs0601"

    const val ACCOUNT_NOT_FOUND = "0wpe_eac0701"
    const val ACCOUNT_NUMBER_NOT_FOUND = "0wpe_eac0702"
    const val PERSONAL_ACCOUNT_NOT_FOUND = "0wpe_eac0703"
    const val WALLET_NOT_FOUND = "0wpe_eac0704"

    const val TRANSACTION_NOT_FOUND = "0wpe_etr0801"

    const val REQUEST_SENDER_USER_NOT_FOUND = "0wpe_erq0901"
    const val REQUEST_SENDER_CUSTOMER_NOT_FOUND = "0wpe_erq0902"
    const val REQUEST_SENDER_ACCOUNT_NOT_FOUND = "0wpe_erq0903"
    const val REQUEST_RECIPIENT_USER_NOT_FOUND = "0wpe_erq0904"
    const val REQUEST_RECIPIENT_CUSTOMER_NOT_FOUND = "0wpe_erq0905"
    const val REQUEST_RECIPIENT_ACCOUNT_NOT_FOUND = "0wpe_erq0906"
    const val REQUEST_DAILY_LIMIT = "0wpe_erq0907"
    const val REQUEST_MONTHLY_LIMIT = "0wpe_erq0908"
    const val REQUEST_YEARLY_LIMIT = "0wpe_erq0909"
    const val REQUEST_AMOUNT_LIMIT = "0wpe_erq0910"
    const val REQUEST_DAILY_NUMBER_LIMIT = "0wpe_erq0911"
    const val REQUEST_MONTHLY_NUMBER_LIMIT = "0wpe_erq0912"
    const val REQUEST_YEARLY_NUMBER_LIMIT = "0wpe_erq0913"
    const val REQUEST_SAME_ACCOUNT = "0wpe_erq0914"
    const val REQUEST_SENDER_ACCOUNT_DEACTIVATED = "0wpe_erq0915"
    const val REQUEST_RECIPIENT_ACCOUNT_DEACTIVATED = "0wpe_erq0916"
    const val REQUEST_NOT_FOUND = "0wpe_erq0917"
    const val REQUEST_ALREADY_ACCEPTED = "0wpe_erq0918"
    const val REQUEST_ALREADY_DECLINED = "0wpe_erq0919"
    const val REQUEST_ALREADY_VERIFIED = "0wpe_erq0920"
    const val REQUEST_ALREADY_COMPLETED = "0wpe_erq0921"
    const val REQUEST_ALREADY_CANCELED = "0wpe_erq0922"
    const val REQUEST_NOT_ACCEPTED = "0wpe_erq0923"
    const val REQUEST_NOT_RECIPIENT = "0wpe_erq0924"
    const val REQUEST_NOT_REQUEST = "0wpe_erq0925"
    const val REQUEST_INSUFFICIENT_BALANCE = "0wpe_erq0926"
    const val REQUEST_NOT_SENDER = "0wpe_erq0927"

    const val TRANSFER_PAYER_USER_NOT_FOUND = "0wpe_etf1001"
    const val TRANSFER_PAYER_CUSTOMER_NOT_FOUND = "0wpe_etf1002"
    const val TRANSFER_PAYER_ACCOUNT_NOT_FOUND = "0wpe_etf1003"
    const val TRANSFER_RECIPIENT_USER_NOT_FOUND = "0wpe_etf1004"
    const val TRANSFER_RECIPIENT_CUSTOMER_NOT_FOUND = "0wpe_etf1005"
    const val TRANSFER_RECIPIENT_ACCOUNT_NOT_FOUND = "0wpe_etf1006"
    const val TRANSFER_DAILY_LIMIT = "0wpe_etf1007"
    const val TRANSFER_MONTHLY_LIMIT = "0wpe_etf1008"
    const val TRANSFER_YEARLY_LIMIT = "0wpe_etf1009"
    const val TRANSFER_AMOUNT_LIMIT = "0wpe_etf1010"
    const val TRANSFER_DAILY_NUMBER_LIMIT = "0wpe_etf1011"
    const val TRANSFER_MONTHLY_NUMBER_LIMIT = "0wpe_etf1012"
    const val TRANSFER_YEARLY_NUMBER_LIMIT = "0wpe_etf1013"
    const val TRANSFER_SAME_ACCOUNT = "0wpe_etf1014"
    const val TRANSFER_PAYER_DEACTIVATED_ACCOUNT = "0wpe_etf1015"
    const val TRANSFER_RECIPIENT_DEACTIVATED_ACCOUNT = "0wpe_etf1016"
    const val TRANSFER_INSUFFICIENT_BALANCE = "0wpe_etf1017"
    const val TRANSFER_NOT_FOUND = "0wpe_etf1018"
    const val TRANSFER_NOT_TRANSFER = "0wpe_etf1019"
    const val TRANSFER_NOT_PAYER = "0wpe_etf1020"
    const val TRANSFER_ALREADY_VERIFIED = "0wpe_etf1021"
    const val TRANSFER_ALREADY_COMPLETED = "0wpe_etf1022"
    const val TRANSFER_ALREADY_CANCELED = "0wpe_etf1023"

    const val MESSAGE_USER_NOT_FOUND = "0wpe_ems1101"
    const val MESSAGE_TOKEN_NOT_FOUND = "0wpe_ems1102"

    const val FILE_NOT_FOUND = "0wpe_efl1102"

    const val ROLE_NOT_FOUND = "0wpe_erk0201"

    // REGISTER USER RESPONSE

    const val REGISTER_USER_RESPONSE_SUCCESS = "RUR200RS"

    const val EMAIL_VERIFICATION_RESPONSE_EMAIL_VERIFICATION_SUCCESS = "EVR200VS"
    const val EMAIL_VERIFICATION_RESPONSE_EMAIL_SENT_SUCCESS = "EVR200ESS"
    const val RESET_PASSWORD_RESPONSE_EMAIL_SENT_SUCCESS = "RPR200ESS"
    const val RESET_PASSWORD_RESPONSE_RESET_PASSWORD_SUCCESS = "RPR200RPS"


}
