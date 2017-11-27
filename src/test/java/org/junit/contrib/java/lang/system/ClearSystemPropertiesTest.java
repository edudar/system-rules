package org.junit.contrib.java.lang.system;

import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.*;
import org.junit.contrib.java.lang.system.TestChecker.ExpectNoFailure;
import org.junit.runner.RunWith;

@RunWith(TestChecker.class)
public class ClearSystemPropertiesTest {
	@ExpectNoFailure
	public static class properties_are_cleared_at_start_of_test {
		@ClassRule
		public static final RestoreSystemProperties RESTORE
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperties() {
			setProperty("first property", "dummy value");
			setProperty("second property", "another dummy value");
		}

		@Rule
		public final ClearSystemProperties clearSystemProperties
			= new ClearSystemProperties("first property", "second property");

		@Test
		public void test() {
			assertThat(getProperty("first property")).isNull();
			assertThat(getProperty("second property")).isNull();
		}
	}

	@ExpectNoFailure
	public static class property_is_cleared_after_added_to_rule_within_test {
		@ClassRule
		public static final RestoreSystemProperties RESTORE
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperty() {
			setProperty("property", "dummy value");
		}

		@Rule
		public final ClearSystemProperties clearSystemProperties
			= new ClearSystemProperties();

		@Test
		public void test() {
			clearSystemProperties.clearProperty("property");
			assertThat(getProperty("property")).isNull();
		}
	}

	public static class after_test_properties_have_the_same_values_as_before {
		@ClassRule
		public static final RestoreSystemProperties RESTORE
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperty() {
			setProperty("first property", "dummy value");
			setProperty("second property", "another dummy value");
			setProperty("third property", "another dummy value");
		}

		@Rule
		public final ClearSystemProperties clearSystemProperties
			= new ClearSystemProperties("first property", "second property");

		@Test
		public void test() {
			clearSystemProperties.clearProperty("third property");
		}

		public static void checkAfterwards() {
			assertThat(getProperties())
				.containsEntry("first property", "dummy value")
				.containsEntry("second property", "another dummy value")
				.containsEntry("third property", "another dummy value");
		}
	}

	@ExpectNoFailure
	public static class property_that_is_not_present_does_not_cause_failure {
		@ClassRule
		public static final RestoreSystemProperties RESTORE
			= new RestoreSystemProperties();

		@BeforeClass
		public static void ensurePropertyIsNotPresent() {
			clearProperty("property");
		}

		@Rule
		public final ClearSystemProperties clearSystemProperties
			= new ClearSystemProperties("property");

		@Test
		public void test() {
		}

		//everything is fine if test is successful
	}
}
