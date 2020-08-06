package me.groot_23.ming.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceExtractor {
	
	/**
	 * This function copies all resources of THIS jar located at the resPath folder to
	 * the dest folder. It's used to extract resources
	 * @param resPath Path to the resource
	 * @param dest Path to the destination
	 * @param replace If set to true, already existing files with the same name will be replaced
	 */
	public static void extractResources(String resPath, Path dest, boolean replace) {
		extractResources(resPath, dest, replace, ResourceExtractor.class);
	}
	
	/**
	 * This function copies all resources of a jar specified by clazz located at the resPath folder to
	 * the dest folder. It's used to extract resources
	 * @param resPath Path to the resource
	 * @param dest Path to the destination
	 * @param replace If set to true, already existing files with the same name will be replaced
	 * @param clazz A class inside the jar which the resources belong to
	 */
	public static void extractResources(String resPath, Path dest, boolean replace, Class<?> clazz) {
		File jarFile;
		try {
			jarFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(jarFile.isFile()) {
				try(JarFile jar = new JarFile(jarFile)) {
					Enumeration<JarEntry> entries = jar.entries();
					while(entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if(name.startsWith(resPath)) {
							name = name.substring(resPath.length());
							if(!resPath.endsWith("/")) {
								name = name.substring(name.indexOf('/') + 1);
							}
							if(entry.isDirectory()) {
								Path dir = dest.resolve(name);
								if(!Files.exists(dir)) {
									Files.createDirectory(dir);
								}
							} else {
								Path file = dest.resolve(name);
								if(!file.toFile().exists() || replace) {
									File parent = file.toFile().getParentFile();
									if(!parent.exists()) {
										parent.mkdirs();
									}
									try(InputStream in = jar.getInputStream(entry)) {
										Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
									}
								}
							}
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				}

			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}
}
