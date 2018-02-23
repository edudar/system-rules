package org.junit.contrib.java.lang.system;

import org.junit.*;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.*;

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
		private final TestClass testClass;

		TestCheckerRunner(Class<?> testClass) throws InitializationError {
			super(extractInnerTestClass(testClass));
			failureCheck = extractMethod(testClass, "expectFailure", Failure.class);
			concludingCheck = extractMethod(testClass, "checkAfterwards");
			expectNoFailure = testClass.isAnnotationPresent(ExpectNoFailure.class);
			this.testClass = new TestClass(testClass);
			verifyCheckPresent();
		}

		private static Class<?> extractInnerTestClass(Class<?> testClass) throws InitializationError {
			Class<?>[] innerClasses = testClass.getClasses();
			if (innerClasses.length == 0) {
				throw new InitializationError("The class " + testClass
					+ " has no inner class with name TestClass.");
			} else if (innerClasses.length > 1) {
				throw new InitializationError("The class " + testClass
					+ " has " + innerClasses.length + " inner classes, but only"
					+ " one inner class with name TestClass is expected.");
			} else if (!innerClasses[0].getSimpleName().equals("TestClass")) {
				throw new InitializationError("The class " + testClass
					+ " has no inner class with name TestClass.");
			} else {
				return innerClasses[0];
			}
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

		protected Statement classBlock(final RunNotifier notifier) {
			Statement statement = super.classBlock(notifier);
			statement = withBeforeClasses(statement);
			statement = withAfterClasses(statement);
			statement = withClassRules(statement);
			return statement;
		}

		/**
		 * Returns a {@link Statement}: run all non-overridden {@code @BeforeClass} methods on this class
		 * and superclasses before executing {@code statement}; if any throws an
		 * Exception, stop execution and pass the exception on.
		 */
		protected Statement withBeforeClasses(Statement statement) {
			List<FrameworkMethod> befores = testClass
				.getAnnotatedMethods(BeforeClass.class);
			return befores.isEmpty() ? statement :
				new RunBefores(statement, befores, null);
		}

		/**
		 * Returns a {@link Statement}: run all non-overridden {@code @AfterClass} methods on this class
		 * and superclasses before executing {@code statement}; all AfterClass methods are
		 * always executed: exceptions thrown by previous steps are combined, if
		 * necessary, with exceptions from AfterClass methods into a
		 * {@link MultipleFailureException}.
		 */
		protected Statement withAfterClasses(Statement statement) {
			List<FrameworkMethod> afters = testClass
				.getAnnotatedMethods(AfterClass.class);
			return afters.isEmpty() ? statement :
				new RunAfters(statement, afters, null);
		}

		/**
		 * Returns a {@link Statement}: apply all
		 * static fields assignable to {@link TestRule}
		 * annotated with {@link ClassRule}.
		 *
		 * @param statement the base statement
		 * @return a RunRules statement if any class-level {@link Rule}s are
		 *         found, or the base statement
		 */
		private Statement withClassRules(Statement statement) {
			List<TestRule> classRules = classRules();
			return classRules.isEmpty() ? statement :
				new RunRules(statement, classRules, getDescription());
		}

		/**
		 * @return the {@code ClassRule}s that can transform the block that runs
		 *         each method in the tested class.
		 */
		protected List<TestRule> classRules() {
			List<TestRule> result = testClass.getAnnotatedMethodValues(null, ClassRule.class, TestRule.class);

			result.addAll(testClass.getAnnotatedFieldValues(null, ClassRule.class, TestRule.class));

			return result;
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
