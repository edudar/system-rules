package org.junit.contrib.java.lang.system;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TestChecker.ExpectNoFailure;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.io.PrintStream;
import java.util.Locale;

import static java.lang.System.*;
import static java.util.Locale.CANADA;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(TestChecker.class)
public class DisallowWriteToSystemOutTest {
	private static final Locale DUMMY_LOCALE = CANADA;

	@ExpectNoFailure
	public static class test_is_successful_if_it_does_not_write_to_System_out {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
		}
	}

	public static class test_fails_if_it_tries_to_append_a_text_to_System_out {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.append("dummy text");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_tries_to_append_a_character_to_System_out {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.append('x');
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'x' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_tries_to_append_a_sub_sequence_of_a_text_to_System_out {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.append("dummy text", 2, 3);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'm' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_format_with_a_Locale {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.format(
				DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_format_without_a_Locale {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.format("%s, %s", "first dummy", "second dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_boolean {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(true);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 't' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_char {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print('a');
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'a' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_an_array_of_chars {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(new char[] {'d', 'u', 'm', 'm', 'y'});
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_double {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(1d);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_float {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(1f);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_an_int {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(1);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_long {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(1L);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_an_object {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print(new Object());
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'j' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_print_with_a_string {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.print("dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_printf_with_a_localized_formatted_text {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.printf(
				DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_printf_with_a_formatted_text {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.printf("%s, %s", "first dummy", "second dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_println_on_System_out {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println();
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '"
					+ getProperty("line.separator").substring(0, 1)
					+ "' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_boolean {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(true);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 't' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_char {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println('a');
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'a' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_an_array_of_chars {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(new char[] {'d', 'u', 'm', 'm', 'y'});
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_double {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(1d);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_float {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(1f);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_an_int {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(1);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_long {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(1L);
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_an_object {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println(new Object());
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'j' although this is not allowed.");
		}
	}

	public static class test_fails_if_it_calls_System_out_println_with_a_string {
		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
			System.out.println("dummy");
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	public static class after_the_test_System_out_is_same_as_before {
		private static PrintStream originalOut;

		@BeforeClass
		public static void captureSystemOut() {
			originalOut = out;
		}

		@Rule
		public final DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();

		@Test
		public void test() {
		}

		public static void checkAfterwards() {
			try {
				assertThat(out).isSameAs(originalOut);
			} finally {
				setOut(originalOut);
			}
		}
	}
}
