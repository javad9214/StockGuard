package com.example.login.util

import com.example.login.R

/**
 * Phone number validation utility for Iranian phone numbers
 * Format: 10 digits, starting with 9
 * Example: 9105107256
 */
object PhoneValidator {

    private const val PHONE_LENGTH = 10
    private const val VALID_START_DIGIT = '9'

    /**
     * Validates if the phone number is in correct format
     * @return ValidationResult with success/error state and message
     */
    fun validate(phoneNumber: String): ValidationResult {
        return when {
            phoneNumber.isEmpty() -> {
                ValidationResult.Error(ValidationError.EMPTY)
            }
            phoneNumber.startsWith("0") -> {
                ValidationResult.Error(ValidationError.STARTS_WITH_ZERO)
            }
            !phoneNumber.all { it.isDigit() } -> {
                ValidationResult.Error(ValidationError.CONTAINS_NON_DIGITS)
            }
            phoneNumber.length < PHONE_LENGTH -> {
                ValidationResult.Error(ValidationError.TOO_SHORT)
            }
            phoneNumber.length > PHONE_LENGTH -> {
                ValidationResult.Error(ValidationError.TOO_LONG)
            }
            !phoneNumber.startsWith(VALID_START_DIGIT) -> {
                ValidationResult.Error(ValidationError.INVALID_START)
            }
            else -> {
                ValidationResult.Success
            }
        }
    }

    /**
     * Filters input to only allow digits and enforce max length
     */
    fun filterInput(input: String): String {
        return input.filter { it.isDigit() }.take(PHONE_LENGTH)
    }

    /**
     * Formats phone number for display (e.g., 910 510 7256)
     */
    fun formatForDisplay(phoneNumber: String): String {
        return when {
            phoneNumber.length <= 3 -> phoneNumber
            phoneNumber.length <= 6 -> "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3)}"
            else -> "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3, 6)} ${phoneNumber.substring(6)}"
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val error: ValidationError) : ValidationResult()

        fun isValid(): Boolean = this is Success
        fun getErrorMessage(): ValidationError? = (this as? Error)?.error
    }

    enum class ValidationError {
        EMPTY,
        STARTS_WITH_ZERO,
        CONTAINS_NON_DIGITS,
        TOO_SHORT,
        TOO_LONG,
        INVALID_START
    }
}

/**
 * Extension function to get localized error message
 */
fun PhoneValidator.ValidationError.toLocalizedMessage(getString: (Int) -> String): String {
    return when (this) {
        PhoneValidator.ValidationError.EMPTY -> getString(R.string.error_phone_empty)
        PhoneValidator.ValidationError.STARTS_WITH_ZERO -> getString(R.string.error_phone_starts_with_zero)
        PhoneValidator.ValidationError.CONTAINS_NON_DIGITS -> getString(R.string.error_phone_invalid_chars)
        PhoneValidator.ValidationError.TOO_SHORT -> getString(R.string.error_phone_too_short)
        PhoneValidator.ValidationError.TOO_LONG -> getString(R.string.error_phone_too_long)
        PhoneValidator.ValidationError.INVALID_START -> getString(R.string.error_phone_invalid_start)
    }
}