package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.security.Permission;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TestChecker.ExpectNoFailure;
import org.junit.runner.RunWith;

@RunWith(TestChecker.class)
public class ProvideSecurityManagerTest {
	private static final SecurityManager MANAGER = new SecurityManager() {
		@Override
		public void checkPermission(Permission perm) {
			// everything is allowed
		}
	};

	@ExpectNoFailure
	public static class provided_security_manager_is_present_during_test {
		public static class TestClass {
			@Rule
			public final ProvideSecurityManager rule = new ProvideSecurityManager(MANAGER);

			@Test
			public void test() {
				assertThat(getSecurityManager()).isSameAs(MANAGER);
			}
		}
	}

	public static class after_test_security_manager_is_the_same_as_before {
		private static final SecurityManager ORIGINAL_MANAGER = getSecurityManager();

		public static class TestClass {
			@Rule
			public final ProvideSecurityManager rule = new ProvideSecurityManager(MANAGER);

			@Test
			public void test() {
			}
		}

		public static void checkAfterwards() {
			assertThat(getSecurityManager()).isSameAs(ORIGINAL_MANAGER);
		}
	}
}
