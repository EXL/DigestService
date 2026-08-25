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
import ru.exlmoto.digest.github.json.GithubCommit;
import ru.exlmoto.digest.entity.GithubReposEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class GithubService {
	private final Logger log = LoggerFactory.getLogger(GithubService.class);

	@Value("classpath:github/github-repos.txt")
	private Resource githubRepos;

	@Value("${github.max-commits-history}")
	private int maxCommitsHistory;

	@Value("${github.max-commits-per-request:3}")
	private int maxCommitsPerRequest;

	private final List<String> targetReposList = new ArrayList<>();
	private final Map<String, Set<String>> repoSeenCommits = new HashMap<>();
	private final Set<String> initializedRepos = new HashSet<>();
	private boolean initialRepositoriesLoaded;

	private final RestClient restClient;
	private final DatabaseService databaseService;
	private final FilterHelper filter;
	private final CommitTgHtmlGenerator htmlGenerator;

	public GithubService(FilterHelper filter,
	                     CommitTgHtmlGenerator htmlGenerator,
	                     DatabaseService databaseService,
	                     @Value("${github.oauth.token:}") String githubToken,
	                     @Value("${rest.timeout-sec:10}") int timeoutSec) {
		this.filter = filter;
		this.htmlGenerator = htmlGenerator;
		this.databaseService = databaseService;

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(timeoutSec));
		requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSec));

		RestClient.Builder builder = RestClient.builder()
				.baseUrl("https://api.github.com")
				.requestFactory(requestFactory)
				.defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
				.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
				.defaultHeader(HttpHeaders.USER_AGENT, "DigestService");
		if (StringUtils.isEmpty(githubToken)) {
			log.warn("GitHub Token is empty. GitHub API requests may be limited.");
		} else {
			log.info("GitHub Token is present. GitHub API requests have large limits.");
			builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
		}
		this.restClient = builder.build();
	}

	@PostConstruct
	public void setUp() {
		initializeRepositories();
	}

	private void initializeRepositories() {
		try {
			GithubReposEntity repos = databaseService.getGithubRepos().orElse(null);
			if (repos == null || repos.getReposList() == null) {
				GithubReposEntity initialRepos = (repos != null) ? repos : new GithubReposEntity(GithubReposEntity.REPOS_ROW);
				initialRepos.setReposList(readRepositoriesFile());
				databaseService.saveGithubRepos(initialRepos);
				log.info("==> Initialized GitHub repositories list in database.");
			}
		} catch (Exception e) {
			log.error("Failed to initialize GitHub repositories list", e);
		}
	}

	private String readRepositoriesFile() {
		StringBuilder repositories = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(githubRepos.getInputStream(), StandardCharsets.UTF_8)
			)) {
			String line;
			while ((line = reader.readLine()) != null) {
				repositories.append(line).append('\n');
			}
		} catch (Exception e) {
			log.error("Failed to read github-repos.txt file", e);
		}
		return repositories.toString().stripTrailing();
	}

	private void reloadRepositories() {
		Optional<GithubReposEntity> repos = databaseService.getGithubRepos();
		if (repos.isEmpty()) {
			return;
		}

		List<String> previousRepos = new ArrayList<>(targetReposList);
		List<String> configuredRepos = parseRepositories(repos.get().getReposList());
		targetReposList.clear();
		targetReposList.addAll(configuredRepos);
		repoSeenCommits.keySet().retainAll(configuredRepos);
		initializedRepos.retainAll(configuredRepos);

		for (String repoName : configuredRepos) {
			repoSeenCommits.computeIfAbsent(repoName, ignored -> createRingBuffer());
			if (initialRepositoriesLoaded && !previousRepos.contains(repoName)) {
				initializedRepos.add(repoName);
			}
		}
		initialRepositoriesLoaded = true;
	}

	private List<String> parseRepositories(String repositories) {
		if (StringUtils.isEmpty(repositories)) {
			return List.of();
		}

		List<String> result = new ArrayList<>();
		for (String line : repositories.split("\\R")) {
			line = filter.strip(line);
			if (!StringUtils.isEmpty(line) && !line.startsWith("#")) {
				String repoPath = line
					.replace("https://github.com/", "")
					.replaceAll("/$", "");
				result.add(repoPath);
			}
		}
		return result;
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
		reloadRepositories();
		List<String> newGitHubCommitsPosts = new ArrayList<>();
		if (targetReposList.isEmpty()) {
			log.info("=> GitHub repositories list is empty. GitHub commits crawler disabled.");
			return new ArrayList<>();
		}

		try {
			for (String repoName : targetReposList) {
				List<String> newGitHubCommits = processGithubRepository(
					repoName,
					!initializedRepos.contains(repoName));
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

	private List<String> processGithubRepository(String repoName, boolean initializeOnly) {
		List<String> newGitHubCommits = new ArrayList<>();
		try {
			Set<String> seenCommits = repoSeenCommits.get(repoName);

			List<GithubCommit> commits = getRecentCommits(repoName);
			if (initializeOnly) {
				if (!commits.isEmpty()) {
					String sha = commits.get(0).sha();
					seenCommits.add(sha);
					log.info(String.format(
						"==> Saved latest SHA %s for %s",
						CommitTgHtmlGenerator.getShortSha(sha), repoName
					));
				}
				initializedRepos.add(repoName);
				return newGitHubCommits;
			}

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

				log.info(
					String.format("==> New commit: %s, %s", repoName, CommitTgHtmlGenerator.getShortSha(sha))
				);

				String html = htmlGenerator.generateGithubCommitHtmlReport(repoName, commit);
				newGitHubCommits.add(html);
			}

			if (newCommits.isEmpty()) {
				log.info(String.format("==> No new commits in: %s", repoName));
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
						.queryParam("per_page", maxCommitsPerRequest)
						.build(parts[0], parts[1]))
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return commits != null ? commits : List.of();
	}
}
