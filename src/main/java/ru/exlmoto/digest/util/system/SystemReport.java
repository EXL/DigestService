/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		joiner.add(getHostUptime());
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

		return String.format("Memory Free: %d%% %s/%s MiB", percent, bytesToMebibytes(used), bytesToMebibytes(max));
	}

	private String getMemoryAllocated() {
		long total = runtime.totalMemory();
		long max = runtime.maxMemory();
		long percent = total * 100 / max;

		return String.format("Memory Allocated: %d%% %s MiB", percent, bytesToMebibytes(total));
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
		double averageCpuLoad = os.getSystemLoadAverage();
		return String.format("Load Average: %s", (averageCpuLoad < 0.0f) ? "Unknown" : String.valueOf(averageCpuLoad));
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

	private String getHostUptime() {
		String uptime = "Uptime: ";
		String command = "uptime";
		try {
			BufferedReader reader =
				new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String res = filter.strip(builder.toString());
			if (res.contains(", load")) {
				res = res.replace(", load", "\nHost load");
			}
			return uptime + res;
		} catch (IOException ioe) {
			log.error(String.format("Cannot exec '%s' command.", command), ioe);
		}
		return uptime + "Unknown";
	}

	private String bytesToMebibytes(long bytes) {
		return String.format("%.2f", bytes / 1024.0D / 1024.0D);
	}
}
