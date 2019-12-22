package company.ryzhkov.sh.util

import org.springframework.beans.BeanWrapperImpl
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class AccountPhoneNumberMatchValidator : ConstraintValidator<AccountPhoneNumberMatch, Any> {
    private var fieldName: String = ""
    private var message: String = ""

    override fun initialize(constraintAnnotation: AccountPhoneNumberMatch) {
        fieldName = constraintAnnotation.fieldName
        message = constraintAnnotation.message
    }

    override fun isValid(value: Any, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        var valid = true
        try {
            val obj = BeanWrapperImpl(value).getPropertyValue(fieldName)
            val p = Pattern.compile("\\+[0-9]+-[0-9]{3}-[0-9]{3}-[0-9]{4}")
            valid = obj == null || obj == "" || p.matcher((obj as CharSequence?)!!).matches()
        } catch (ignore: Exception) { }
        if (!valid) {
            constraintValidatorContext
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        }
        return valid
    }
}
