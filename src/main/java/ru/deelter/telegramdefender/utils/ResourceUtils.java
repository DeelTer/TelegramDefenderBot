package ru.deelter.telegramdefender.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.telegramdefender.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@UtilityClass
public final class ResourceUtils {

	private static final Class<?> REFERENCE_CLASS = Main.class;

	@NotNull
	public static Path saveResource(@NotNull String resourcePath) {
		return saveResource(resourcePath, false);
	}

	@NotNull
	public static Path saveResource(@NotNull String resourcePath, boolean replace) {
		return saveResource(resourcePath, resourcePath, replace);
	}

	@NotNull
	public static Path saveResource(@NotNull String from, @NotNull String to, boolean replace) {
		String resource = from.startsWith("/") ? from.substring(1) : from;
		Path target = Path.of(to.replace('\\', '/'));

		if (Files.exists(target) && !replace) {
			return target;
		}

		try (InputStream in = getResourceAsStream(resource)) {
			if (in == null) {
				throw new IllegalArgumentException("Ресурс не найден в JAR: " + from);
			}
			Path parent = target.getParent();
			if (parent != null) Files.createDirectories(parent);

			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new UncheckedIOException("Не удалось сохранить файл: " + to, e);
		}
		return target;
	}


	@Nullable
	@SneakyThrows
	public static InputStream getResourceAsStream(@NotNull String path) {
		path = path.replace('\\', '/');
		URL url = REFERENCE_CLASS.getClassLoader().getResource(path);
		if (url == null) {
			return null;
		}
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		return connection.getInputStream();
	}


	@NotNull
	public static List<String> getResourcesInFolder(@NotNull String folder, boolean recursive) {
		final String dir = folder.endsWith("/") ? folder : folder + "/";
		final String cleanDir = dir.startsWith("/") ? dir.substring(1) : dir;

		List<String> result = new ArrayList<>();

		URL url = REFERENCE_CLASS.getClassLoader().getResource(cleanDir);
		if (url == null) return result;

		try {
			URI uri = url.toURI();

			try (FileSystem fs = "jar".equals(uri.getScheme())
					? FileSystems.newFileSystem(uri, Collections.emptyMap())
					: FileSystems.getDefault()) {

				Path root = fs.getPath(cleanDir);
				if (!Files.exists(root)) return result;

				try (Stream<Path> stream = recursive ? Files.walk(root) : Files.list(root)) {
					stream.filter(Files::isRegularFile)
							.map(root::relativize)
							.map(Path::toString)
							.map(p -> p.replace('\\', '/'))
							.map(p -> cleanDir + p)          // теперь cleanDir — final
							.forEach(result::add);
				}
			}
		} catch (Exception ignored) {
			// В редких случаях (например, в некоторых Gradle-окружениях) может бросить — просто возвращаем пустой список
		}
		return result;
	}

	@NotNull
	public static List<Path> getFiles(@NotNull Path directory,
									  @Nullable String regexPattern,
									  boolean recursive) {
		if (!Files.isDirectory(directory)) return List.of();

		Pattern pattern = regexPattern != null && !regexPattern.isBlank()
				? Pattern.compile(regexPattern)
				: null;

		List<Path> result = new ArrayList<>();
		try (Stream<Path> stream = recursive ? Files.walk(directory) : Files.list(directory)) {
			stream.filter(Files::isRegularFile)
					.filter(p -> pattern == null || pattern.matcher(p.getFileName().toString()).matches())
					.forEach(result::add);
		} catch (IOException e) {
			throw new UncheckedIOException("Не удалось прочитать папку: " + directory, e);
		}
		return result;
	}

	@NotNull
	public static List<Path> getFiles(@NotNull File directory,
									  @Nullable String regexPattern,
									  boolean recursive) {
		return getFiles(directory.toPath(), regexPattern, recursive);
	}

	@NotNull
	public static List<Path> getFilesByExtension(@NotNull Path folder,
												 @NotNull String extension,
												 boolean recursive) {
		String ext = extension.startsWith(".") ? extension.substring(1) : extension;
		return getFiles(folder, ".*\\." + Pattern.quote(ext) + "$", recursive);
	}

	@NotNull
	public static List<Path> getConfigFiles(@NotNull Path folder, boolean recursive) {
		return getFiles(folder,
				".*\\.(yml|yaml|json|conf|cfg|toml|properties|hocon)",
				recursive);
	}
}