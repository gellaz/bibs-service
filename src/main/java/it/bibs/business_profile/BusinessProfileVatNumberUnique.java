package it.bibs.business_profile;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/** Validate that the vatNumber value isn't taken yet. */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
    validatedBy = BusinessProfileVatNumberUnique.BusinessProfileVatNumberUniqueValidator.class)
public @interface BusinessProfileVatNumberUnique {

  String message() default "{exists.businessProfile.vatNumber}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class BusinessProfileVatNumberUniqueValidator
      implements ConstraintValidator<BusinessProfileVatNumberUnique, String> {

    private final BusinessProfileService businessProfileService;
    private final HttpServletRequest request;

    public BusinessProfileVatNumberUniqueValidator(
        final BusinessProfileService businessProfileService, final HttpServletRequest request) {
      this.businessProfileService = businessProfileService;
      this.request = request;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
      if (value == null) {
        // no value present
        return true;
      }
      @SuppressWarnings("unchecked")
      final Map<String, String> pathVariables =
          ((Map<String, String>)
              request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
      final String currentId = pathVariables.get("id");
      if (currentId != null
          && value.equalsIgnoreCase(
              businessProfileService.get(UUID.fromString(currentId)).getVatNumber())) {
        // value hasn't changed
        return true;
      }
      return !businessProfileService.vatNumberExists(value);
    }
  }
}
