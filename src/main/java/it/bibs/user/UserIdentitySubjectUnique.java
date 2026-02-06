package it.bibs.user;

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

/** Validate that the identitySubject value isn't taken yet. */
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UserIdentitySubjectUnique.UserIdentitySubjectUniqueValidator.class)
public @interface UserIdentitySubjectUnique {

  String message() default "{exists.user.identitySubject}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class UserIdentitySubjectUniqueValidator
      implements ConstraintValidator<UserIdentitySubjectUnique, String> {

    private final UserService userService;
    private final HttpServletRequest request;

    public UserIdentitySubjectUniqueValidator(
        final UserService userService, final HttpServletRequest request) {
      this.userService = userService;
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
              userService.get(UUID.fromString(currentId)).getIdentitySubject())) {
        // value hasn't changed
        return true;
      }
      return !userService.identitySubjectExists(value);
    }
  }
}
