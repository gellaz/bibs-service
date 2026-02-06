package it.bibs.product;

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

/** Validate that the productStock value isn't taken yet. */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProductProductStockUnique.ProductProductStockUniqueValidator.class)
public @interface ProductProductStockUnique {

  String message() default "{Exists.product.productStock}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class ProductProductStockUniqueValidator
      implements ConstraintValidator<ProductProductStockUnique, UUID> {

    private final ProductService productService;
    private final HttpServletRequest request;

    public ProductProductStockUniqueValidator(
        final ProductService productService, final HttpServletRequest request) {
      this.productService = productService;
      this.request = request;
    }

    @Override
    public boolean isValid(final UUID value, final ConstraintValidatorContext cvContext) {
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
          && value.equals(productService.get(UUID.fromString(currentId)).getProductStock())) {
        // value hasn't changed
        return true;
      }
      return !productService.productStockExists(value);
    }
  }
}
