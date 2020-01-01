package company.ryzhkov.sh.util

import org.springframework.beans.BeanWrapperImpl

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PasswordRepeatMatchValidator : ConstraintValidator<PasswordRepeatMatch, Any> {
    private var firstFieldName: String = ""
    private var secondFieldName: String = ""
    private var message: String = ""

    override fun initialize(constraintAnnotation: PasswordRepeatMatch) {
        firstFieldName = constraintAnnotation.first
        secondFieldName = constraintAnnotation.second
        message = constraintAnnotation.message
    }

    override fun isValid(value: Any, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        var valid = true
        try {
            val firstObj = BeanWrapperImpl(value).getPropertyValue(firstFieldName)
            val secondObj = BeanWrapperImpl(value).getPropertyValue(secondFieldName)
            valid = firstObj == null && secondObj == null || firstObj != null && firstObj == secondObj
        } catch (ignore: Exception) {
        }

        if (!valid) {
            constraintValidatorContext
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(firstFieldName)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        }
        return valid
    }
}
