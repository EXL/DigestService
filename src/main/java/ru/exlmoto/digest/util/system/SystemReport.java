package ru.exlmoto.digest.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.filter.FilterHelper;

import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.StringJoiner;

@Component
public class SystemReport {
	private final Logger log = LoggerFactory.getLogger(SystemReport.class);
	private final OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
	private final Runtime runtime = Runtime.getRuntime();

	private final FilterHelper filter;

	public SystemReport(FilterHelper filter) {
		this.filter = filter;
	}

	public String getSystemReportHtml() {
		return getSystemReportAux("<br/>");
	}

	public String getSystemReportMarkdown() {
		return getSystemReportAux("\n");
	}

	private String getSystemReportAux(String delimiter) {
		StringJoiner joiner = new StringJoiner(delimiter);
		joiner.add(getJavaVersion());
		joiner.add(getJavaVendor());
		joiner.add("");
		joiner.add(getOsName());
		joiner.add(getOsVersion());
		joiner.add(getOsArch());
		joiner.add("");
		joiner.add(getMemoryFree());
		joiner.add(getMemoryAllocated());
		joiner.add("");
		joiner.add(getCpuName());
		joiner.add(getCpuCount());
		joiner.add(getCpuLoadAverage());
		joiner.add("");
		joiner.add(getHostName());
		return joiner.toString();
	}

	private String getJavaVersion() {
		return String.format("Java Version: %s %s-bit",
			System.getProperty("java.version"), System.getProperty("sun.arch.data.model"));
	}

	private String getJavaVendor() {
		return String.format("Java Vendor: %s", System.getProperty("java.vendor"));
	}

	// Source: https://stackoverflow.com/a/57084402
	private String getCpuName() {
		String name = "CPU: ";
		try {
			name += filter.strip(Files.lines(Paths.get("/proc/cpuinfo"))
				.filter(line -> line.startsWith("model name"))
				.map(line -> line.replaceAll(".*: ", ""))
				.findFirst().orElse("Unknown"));
		} catch (IOException ioe) {
			log.error("Cannot open /proc/cpuinfo on this OS.", ioe);
			name += "Unknown";
		}
		return name;
	}

	private String getCpuCount() {
		return String.format("Cores: %d", os.getAvailableProcessors());
	}

	private String getCpuLoadAverage() {
		return String.format("Load Average: %f", os.getSystemLoadAverage());
	}

	private String getOsName() {
		return String.format("OS: %s", System.getProperty("os.name"));
	}

	private String getOsVersion() {
		return String.format("Version: %s", System.getProperty("os.version"));
	}

	private String getOsArch() {
		return String.format("Arch: %s", System.getProperty("os.arch"));
	}

	private String getMemoryFree() {
		long free = runtime.freeMemory();
		long total = runtime.totalMemory();
		long max = runtime.maxMemory();
		long totalFree = free + (max - total);
		long used = max - totalFree;

		long percent = totalFree * 100 / max;

		return String.format("Memory Free: %d%% %d/%d MiB", percent, used / 1024 / 1024, max / 1024 / 1024);
	}

	private String getMemoryAllocated() {
		long total = runtime.totalMemory();
		long max = runtime.maxMemory();
		long percent = total * 100 / max;

		return String.format("Memory Allocated: %d%% %d MiB", percent, total / 1024 / 1024);
	}

	private String getHostName() {
		String host = "Host: ";
		String hostname = System.getenv("HOSTNAME");
		if (hostname != null) {
			host += hostname;
		} else {
			hostname = System.getenv("COMPUTERNAME");
			if (hostname != null) {
				host += hostname;
			} else {
				try {
					host += Files.lines(Paths.get("/etc/hostname"))
						.findFirst().orElseThrow(() -> new IOException("Error: /etc/hostname is empty."));
				} catch (IOException ioe) {
					log.error("Cannot open /etc/hostname on this OS.", ioe);
					try {
						host += InetAddress.getLocalHost().getHostName();
					} catch (UnknownHostException uhe) {
						log.error("Cannot get system host name.", uhe);
						host += "Unknown";
					}
				}
			}
		}
		return host;
	}
}
