package org.junit.contrib.java.lang.system;

import static java.lang.String.format;
import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.*;
import org.junit.contrib.java.lang.system.TestChecker.ExpectNoFailure;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(TestChecker.class)
public class SystemOutRuleTest {

	public static class after_the_test_system_out_is_same_as_before {
		private static PrintStream originalStream;

		@BeforeClass
		public static void captureOriginalStream() {
			originalStream = System.out;
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			System.out.print("dummy text");
		}

		public static void checkAfterwards() {
			assertThat(originalStream).isSameAs(System.out);
		}
	}

	public static class text_is_still_written_to_system_out_if_no_log_mode_is_specified {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			System.out.print("dummy text");
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEqualTo("dummy text");
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class no_text_is_written_to_system_out_if_muted_globally {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule().mute();

		@Test
		public void test() {
			System.out.print("dummy text");
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class no_text_is_written_to_system_out_after_muted_locally {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			System.out.print("text before muting");
			systemOutRule.mute();
			System.out.print("text after muting");
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString())
				.isEqualTo("text before muting");
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class no_text_is_written_to_system_out_for_successful_test_if_muted_globally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.muteForSuccessfulTests();

		@Test
		public void test() {
			System.out.print("dummy text");
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class text_is_written_to_system_out_for_failing_test_if_muted_globally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.muteForSuccessfulTests();

		@Test
		public void test() {
			System.out.print("dummy text");
			fail();
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEqualTo("dummy text");
		}

		public static void expectFailure(Failure failure) {
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class no_text_is_written_to_system_out_for_successful_test_if_muted_locally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			systemOutRule.muteForSuccessfulTests();
			System.out.print("dummy text");
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class text_is_written_to_system_out_for_failing_test_if_muted_locally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			systemOutRule.muteForSuccessfulTests();
			System.out.print("dummy text");
			fail();
		}

		public static void checkAfterwards() {
			assertThat(captureOutputStream.toString()).isEqualTo("dummy text");
		}

		public static void expectFailure(Failure failure) {
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	@ExpectNoFailure
	public static class no_text_is_logged_by_default {
		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			System.out.print("dummy text");
			assertThat(systemOutRule.getLog()).isEmpty();
		}
	}

	@ExpectNoFailure
	public static class text_is_logged_if_log_has_been_enabled_globally {
		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.enableLog();

		@Test
		public void test() {
			System.out.print("dummy text");
			assertThat(systemOutRule.getLog()).isEqualTo("dummy text");
		}
	}

	@ExpectNoFailure
	public static class text_is_logged_after_log_has_been_enabled_locally {
		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule();

		@Test
		public void test() {
			System.out.print("text before enabling log");
			systemOutRule.enableLog();
			System.out.print("text after enabling log");
			assertThat(systemOutRule.getLog())
				.isEqualTo("text after enabling log");
		}
	}

	@ExpectNoFailure
	public static class log_contains_only_text_that_has_been_written_after_log_was_cleared {
		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.enableLog();

		@Test
		public void test() {
			System.out.print("text before clearing");
			systemOutRule.clearLog();
			System.out.print("text after clearing");
			assertThat(systemOutRule.getLog()).isEqualTo("text after clearing");
		}
	}

	@ExpectNoFailure
	public static class text_is_logged_if_rule_is_enabled_and_muted {
		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.enableLog()
			.mute();

		@Test
		public void test() {
			System.out.print("dummy text");
			assertThat(systemOutRule.getLog()).isEqualTo("dummy text");
		}
	}

	@ExpectNoFailure
	public static class log_is_provided_with_new_line_characters_only_if_requested {
		@ClassRule
		public static final RestoreSystemProperties RESTORE_SYSTEM_PROPERTIES
			= new RestoreSystemProperties();

		@BeforeClass
		public static void useWindowsLineSeparator() {
			setProperty("line.separator", "\r\n");
		}

		@Rule
		public final SystemOutRule systemOutRule = new SystemOutRule()
			.enableLog();

		@Test
		public void test() {
			System.out.print(format("dummy%ntext%n"));
			assertThat(systemOutRule.getLogWithNormalizedLineSeparator())
				.isEqualTo("dummy\ntext\n");
		}
	}
}
