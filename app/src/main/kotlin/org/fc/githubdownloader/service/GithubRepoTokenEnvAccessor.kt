package org.fc.githubdownloader.service

class GithubRepoTokenEnvAccessor {
    fun getToken(): String? = System.getenv("GITHUB_REPO_ACCESS_TOKEN")
}