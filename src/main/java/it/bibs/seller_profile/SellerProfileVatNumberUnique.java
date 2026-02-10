package it.bibs.seller_profile;

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
@Constraint(validatedBy = SellerProfileVatNumberUnique.SellerProfileVatNumberUniqueValidator.class)
public @interface SellerProfileVatNumberUnique {

  String message() default "{exists.sellerProfile.vatNumber}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class SellerProfileVatNumberUniqueValidator
      implements ConstraintValidator<SellerProfileVatNumberUnique, String> {

    private final SellerProfileService sellerProfileService;
    private final HttpServletRequest request;

    public SellerProfileVatNumberUniqueValidator(
        final SellerProfileService sellerProfileService, final HttpServletRequest request) {
      this.sellerProfileService = sellerProfileService;
      this.request = request;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
      if (value == null) {
        return true;
      }
      @SuppressWarnings("unchecked")
      final Map<String, String> pathVariables =
          ((Map<String, String>)
              request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
      final String currentId = pathVariables.get("sellerProfileId");
      if (currentId != null
          && value.equalsIgnoreCase(
              sellerProfileService.get(UUID.fromString(currentId)).getVatNumber())) {
        return true;
      }
      return !sellerProfileService.vatNumberExists(value);
    }
  }
}
