package org.ikasan.component.endpoint.amazon.s3.validation;

import com.amazonaws.util.CollectionUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.ikasan.spec.component.endpoint.EndpointException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Validate Beans with JSR 380 Annotations
 *
 * @param <T> the type of the bean to validate
 */
public class BeanValidator<T> {

    /**
     * Validates a bean with JSR 380 Annotations and throws an exception using the passed in consumer if validation fails
     * Uses the Hibernate validator implementation ( the reference implementation ) .
     *
     * @param validatableBean he bean to validate
     * @throws EndpointException thrown if validation fails
     */
    public void validateBean(T validatableBean, Consumer<String> exceptionThrower ) {
        ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
        Validator validator = factory.getValidator();
        List<ConstraintViolation<T>> violations = new ArrayList(validator.validate(validatableBean));
        violations.sort(Comparator.comparing(o -> o.getPropertyPath().toString()));
        if (!CollectionUtils.isNullOrEmpty(violations)) {
            String constraintViolations =
                violations.stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).collect(Collectors.toList()).toString();
            exceptionThrower.accept(constraintViolations);
        }
    }
}
