package it.bibs.product_category;

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

/** Validate that the name value isn't taken yet. */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProductCategoryNameUnique.ProductCategoryNameUniqueValidator.class)
public @interface ProductCategoryNameUnique {

  String message() default "{exists.productCategory.name}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class ProductCategoryNameUniqueValidator
      implements ConstraintValidator<ProductCategoryNameUnique, String> {

    private final ProductCategoryService productCategoryService;
    private final HttpServletRequest request;

    public ProductCategoryNameUniqueValidator(
        final ProductCategoryService productCategoryService, final HttpServletRequest request) {
      this.productCategoryService = productCategoryService;
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
              productCategoryService.get(UUID.fromString(currentId)).getName())) {
        // value hasn't changed
        return true;
      }
      return !productCategoryService.nameExists(value);
    }
  }
}
