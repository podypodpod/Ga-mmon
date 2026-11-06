package uk.co.davidlomas.gammon;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * build.gradle names the classes its tasks start as text, so renaming or moving one still compiles and only fails when
 * someone runs the task
 */
public class GradleTasksTest {

	private static final Pattern MAIN_CLASS = Pattern.compile("mainClass = '([^']+)'");

	@Test
	public void everyTaskStartsAClassThatHasAMainMethod() throws Exception {
		final List<String> names = mainClasses();

		assertEquals(3, names.size(), "expected ga, menu and benchmark, but found " + names);
		for (final String name : names) {
			assertNotNull(Class.forName(name).getMethod("main", String[].class));
		}
	}

	private static List<String> mainClasses() throws IOException {
		final List<String> names = new ArrayList<>();
		final Matcher matcher = MAIN_CLASS.matcher(Files.readString(Path.of("build.gradle")));
		while (matcher.find()) {
			names.add(matcher.group(1));
		}
		return names;
	}
}
