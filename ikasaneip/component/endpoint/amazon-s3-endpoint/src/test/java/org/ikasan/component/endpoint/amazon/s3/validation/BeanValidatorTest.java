package org.ikasan.component.endpoint.amazon.s3.validation;

import org.junit.Test;

import jakarta.validation.constraints.NotNull;

import static org.junit.Assert.assertEquals;

public class BeanValidatorTest {


    @Test
    public void testViolations() {
        TestBean testBean = new TestBean();
        BeanValidator<TestBean> validator = new BeanValidator<>();
        try {
            validator.validateBean(testBean, RuntimeException::new);
        } catch (RuntimeException re) {
            assertEquals("firstName", re.getMessage());
        }
    }

    @Test
    public void noViolations() {
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
}