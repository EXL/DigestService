/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.stereotype.Service;

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.github.generator.CommitTgHtmlGenerator;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GithubService {
	private final Logger log = LoggerFactory.getLogger(GithubService.class);

	@Value("classpath:github/github-repos.txt")
	private Resource githubRepos;

	@Value("${github.max-commits-history}")
	private int maxCommitsHistory;

	private final List<String> targetReposList = new ArrayList<>();
	private final Map<String, Set<String>> repoSeenCommits = new HashMap<>();

	private final RestClient restClient;
	private final FilterHelper filter;
	private final CommitTgHtmlGenerator htmlGenerator;

	public GithubService(FilterHelper filter,
	                     CommitTgHtmlGenerator htmlGenerator,
	                     @Value("${github.oauth.token:}") String githubToken,
	                     @Value("${rest.timeout-sec:10}") int timeoutSec) {
		this.filter = filter;
		this.htmlGenerator = htmlGenerator;

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(timeoutSec));
		requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSec));

		RestClient.Builder builder = RestClient.builder()
				.baseUrl("https://api.github.com")
				.requestFactory(requestFactory)
				.defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
				.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
				.defaultHeader(HttpHeaders.USER_AGENT, "DigestService");
		if (githubToken != null && !githubToken.isBlank()) {
			builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
		}
		this.restClient = builder.build();
	}

	@PostConstruct
	public void setUp() {
		loadRepositories();
	}

	private void loadRepositories() {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(githubRepos.getInputStream(), StandardCharsets.UTF_8)
			)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = filter.strip(line);
				if (!StringUtils.isEmpty(line) && !line.startsWith("#")) {
					// Converts "https://github.com/EXL/DigestService" -> "EXL/DigestService"
					String repoPath =
						line
							.replace("https://github.com/", "")
							.replaceAll("/$", "");
					targetReposList.add(repoPath);
					repoSeenCommits.put(repoPath, createRingBuffer());
				}
			}
		} catch (Exception e) {
			log.error("Failed to read github-repos.txt file", e);
		}
	}

	private Set<String> createRingBuffer() {
		return Collections.newSetFromMap(new LinkedHashMap<String, Boolean>(maxCommitsHistory, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
				return size() > maxCommitsHistory;
			}
		});
	}

	public List<String> getNewGithubCommitsPosts() {
		List<String> newGitHubCommitsPosts = new ArrayList<>();
		if (targetReposList.isEmpty()) {
			log.error("=> GitHub repositories list file is empty. Cannot get GitHub commits.");
			return new ArrayList<>();
		}

		try {
			for (String repoName : targetReposList) {
				List<String> newGitHubCommits = processGithubRepository(repoName);
				for (String post : newGitHubCommits) {
					newGitHubCommitsPosts.add(post);
				}
			}

			return newGitHubCommitsPosts;
		} catch (Exception e) {
			log.error("=> Error fetching updates from GitHub API", e);
		}
		return new ArrayList<>();
	}

	private List<String> processGithubRepository(String repoName) {
		List<String> newGitHubCommits = new ArrayList<>();
		try {
			Set<String> seenCommits = repoSeenCommits.get(repoName);

			List<GithubCommit> commits = getRecentCommits(repoName);
			List<GithubCommit> newCommits = new ArrayList<>();

			for (GithubCommit commit : commits) {
				String sha = commit.sha();
				if (seenCommits.contains(sha)) {
					break; // Stopped at already processed history.
				}
				newCommits.add(commit);
			}

			// Reverse so oldest new commit posts first.
			Collections.reverse(newCommits);

			for (GithubCommit commit : newCommits) {
				String sha = commit.sha();
				seenCommits.add(sha);

				log.info(String.format("==> New commit: %s, %s", repoName, sha));

				String html = htmlGenerator.generateGithubCommitHtmlReport(repoName, commit);
				newGitHubCommits.add(html);
			}

			return newGitHubCommits;
		} catch (RestClientException e) {
			log.error("Failed to process GitHub repository '{}': {}", repoName, e.getMessage());
		} catch (RuntimeException e) {
			log.error("Failed to process GitHub repository: {}", repoName, e);
		}
		return new ArrayList<>();
	}

	private List<GithubCommit> getRecentCommits(String ownerRepo) {
		String[] parts = ownerRepo.split("/", -1);
		if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
			throw new IllegalArgumentException("GitHub repository must have the 'owner/name' format: " + ownerRepo);
		}

		List<GithubCommit> commits = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/repos/{owner}/{repository}/commits")
						.queryParam("per_page", 10)
						.build(parts[0], parts[1]))
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return commits != null ? commits : List.of();
	}
}
