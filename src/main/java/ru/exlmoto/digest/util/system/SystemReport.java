package ru.exlmoto.digest.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

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

	private final ApplicationContext context;
	private final FilterHelper filter;
	private final LocaleHelper locale;
	private final RestHelper rest;

	@Value("${general.date-format}")
	private String dateFormat;

	@Value("${general.url-host-ip}")
	private String urlHostIp;

	public SystemReport(ApplicationContext context, FilterHelper filter, LocaleHelper locale, RestHelper rest) {
		this.context = context;
		this.filter = filter;
		this.locale = locale;
		this.rest = rest;
	}

	public String getSystemReportHtml() {
		return getSystemReportAux("<br/>").replaceAll("\n", "<br/>");
	}

	public String getSystemReportMarkdown() {
		return getSystemReportAux("\n");
	}

	private String getSystemReportAux(String delimiter) {
		StringJoiner joiner = new StringJoiner(delimiter);
		joiner.add(getJavaVersion());
		joiner.add(getJavaVendor());
		joiner.add("");
		joiner.add(getDigestServiceVersion());
		joiner.add(getDigestServiceRevision());
		joiner.add(getDigestServiceBuildDateTime());
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
		joiner.add(getHostIp());
		return joiner.toString();
	}

	private String getJavaVersion() {
		return String.format("Java Version: %s %s-bit",
			System.getProperty("java.version"), System.getProperty("sun.arch.data.model"));
	}

	private String getJavaVendor() {
		return String.format("Vendor: %s", System.getProperty("java.vendor"));
	}

	private String getDigestServiceVersion() {
		return String.format("Digest Service Version: %s", context.getBean(BuildProperties.class).getVersion());
	}

	private String getDigestServiceRevision() {
		return String.format("Revision: %s", context.getBean(BuildProperties.class).get("revision"));
	}

	private String getDigestServiceBuildDateTime() {
		return String.format("Build Date Time: %s",
			filter.getDateFromTimeStamp(dateFormat, context.getBean(BuildProperties.class).getTime().getEpochSecond()));
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

		return String.format("Memory Free: %d%% %s/%s MiB", percent, bytesToMegabytes(used), bytesToMegabytes(max));
	}

	private String getMemoryAllocated() {
		long total = runtime.totalMemory();
		long max = runtime.maxMemory();
		long percent = total * 100 / max;

		return String.format("Memory Allocated: %d%% %s MiB", percent, bytesToMegabytes(total));
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

	private String getHostIp() {
		Answer<String> res = rest.getRestResponse(urlHostIp);
		return String.format("IP: %s", (res.ok()) ? res.answer() :
			String.format(locale.i18n("bot.error.hostip"), res.error()));
	}

	private String bytesToMegabytes(long bytes) {
		return String.format("%.2f", bytes / 1024.0D / 1024.0D);
	}
}
