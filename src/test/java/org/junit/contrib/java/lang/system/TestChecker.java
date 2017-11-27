package org.junit.contrib.java.lang.system;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestChecker extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface ExpectNoFailure {
	}

	public TestChecker(Class<?> testClass) throws Throwable {
		super(testClass, createRunners(testClass));
	}

	private static List<Runner> createRunners(Class<?> testClass) {
		ArrayList<Runner> runners = new ArrayList<Runner>();
		for (Class<?> each : testClass.getClasses()) {
			runners.add(safeRunnerForClass(each));
		}
		return runners;
	}

	private static Runner safeRunnerForClass(Class<?> testClass) {
		try {
			return new TestCheckerRunner(testClass);
		} catch (Throwable e) {
			return new ErrorReportingRunner(testClass, e);
		}
	}

	private static class TestCheckerRunner extends BlockJUnit4ClassRunner {
		private final Method failureCheck;
		private final Method concludingCheck;
		private final boolean expectNoFailure;

		TestCheckerRunner(Class<?> testClass) throws InitializationError {
			super(testClass);
			failureCheck = extractMethod(testClass, "expectFailure", Failure.class);
			concludingCheck = extractMethod(testClass, "checkAfterwards");
			expectNoFailure = testClass.isAnnotationPresent(ExpectNoFailure.class);
			verifyCheckPresent();
		}

		private void verifyCheckPresent() {
			if (failureCheck == null && concludingCheck == null && !expectNoFailure)
				throw new IllegalStateException(
					"No expectation is defined for the test " +  getName() + ". It needs either a method expectFailure or checkAfterwards or an annotation @ExpectNoFailure.");
		}

		private static Method extractMethod(Class<?> testClass, String name, Class<?>... parameterTypes) {
			try {
				return testClass.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				return null;
			}
		}

		@Override
		protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
			Description description = describeChild(method);
			if (method.getAnnotation(Ignore.class) != null) {
				notifier.fireTestIgnored(description);
			} else {
				runTest(methodBlock(method), description, notifier);
			}
		}

		void runTest(Statement statement, Description description,
			RunNotifier notifier) {
			EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
			eachNotifier.fireTestStarted();
			try {
				statement.evaluate();
				if (failureCheck != null) {
					eachNotifier.addFailure(new AssertionError("Test did not fail."));
				}
			} catch (AssumptionViolatedException e) {
				eachNotifier.addFailedAssumption(e);
				if (failureCheck != null) {
					eachNotifier.addFailure(new AssertionError("Test did not fail."));
				}
			} catch (Throwable e) {
				if (failureCheck == null) {
					eachNotifier.addFailure(e);
				} else {
					try {
						failureCheck.invoke(null, new Failure(description, e));
					} catch (IllegalAccessException e1) {
						fail(eachNotifier, e1, "Failed to check failure.");
					} catch (InvocationTargetException e1) {
						fail(eachNotifier, e1, "Failed to check failure.");
					} catch (Exception e1) {
						eachNotifier.addFailure(e1);
					}
				}
			} finally {
				if (concludingCheck != null) {
					try {
						concludingCheck.invoke(null);
					} catch (IllegalAccessException e1) {
						fail(eachNotifier, e1, "Failed to execute concluding check.");
					} catch (InvocationTargetException e1) {
						fail(eachNotifier, e1, "Failed to execute concluding check.");
					} catch (Exception e1) {
						eachNotifier.addFailure(e1);
					}
				}
				eachNotifier.fireTestFinished();
			}
		}

		private void fail(EachTestNotifier eachNotifier, InvocationTargetException e, String message) {
			if (e.getCause() instanceof AssertionError) {
				eachNotifier.addFailure(e.getCause());
			} else {
				fail(eachNotifier, (Exception) e, message);
			}
		}

		private void fail(EachTestNotifier eachNotifier, Exception e, String message) {
			AssertionError error = new AssertionError(message);
			error.initCause(e);
			eachNotifier.addFailure(error);
		}
	}
}
