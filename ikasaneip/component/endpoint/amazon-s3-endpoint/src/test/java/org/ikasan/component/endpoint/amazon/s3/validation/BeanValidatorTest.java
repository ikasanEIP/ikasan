package org.ikasan.component.endpoint.amazon.s3.validation;

import com.amazonaws.util.CollectionUtils;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanValidatorTest {


    @Test
    void testViolations() {
        TestBean testBean = new TestBean();
        BeanValidator<TestBean> validator = new BeanValidator<>();
        try {
            validator.validateBean(testBean, RuntimeException::new);
        } catch (RuntimeException re) {
            assertEquals("firstName", re.getMessage());
        }
    }

    @Test
    void noViolations() {
        TestBean testBean = new TestBean();
        testBean.setFirstName("firstName");
        testBean.setLastName("lastName");
        testBean.setTelephoneNumber("telephoneNumber");
        BeanValidator<TestBean> validator = new BeanValidator<>();
        validator.validateBean(testBean, RuntimeException::new);
    }

    public class TestBean {

        @NotNull
        private String firstName;

        @NotNull
        private String lastName;

        private String telephoneNumber;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getTelephoneNumber() {
            return telephoneNumber;
        }

        public void setTelephoneNumber(String telephoneNumber) {
            this.telephoneNumber = telephoneNumber;
        }
    }

//    if (!CollectionUtils.isNullOrEmpty(violations)){
//        String constraintViolations =
//            violations.stream().map(v->v.getPropertyPath() + " " + v.getMessage()).collect(Collectors.toList()).toString();
//        throw new EndpointException("Instance of " + validatableBean.getClass().getSimpleName()
//            + " has the following constraint violations :- " + constraintViolations);
//    }
}