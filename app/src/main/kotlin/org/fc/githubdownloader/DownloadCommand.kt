package org.fc.githubdownloader

import kotlinx.cli.Subcommand
import org.asynchttpclient.*
import org.fc.githubdownloader.service.GithubRepoTokenEnvAccessor

class DownloadCommand(private val tokenAccessor: GithubRepoTokenEnvAccessor) : Subcommand("download", "Downloads from github") {
    override fun execute() {
        val clientBuilder: DefaultAsyncHttpClientConfig.Builder = Dsl.config()
                .setConnectTimeout(500)
        val client: AsyncHttpClient = Dsl.asyncHttpClient(clientBuilder)

        val requestBuilder: RequestBuilder = Dsl.get("https://api.github.com/repos/fernandochristyanto/aws-s3backend-terraform-module/contents/example?ref=release/v1.0.0")
                .addHeader("Accept", "application/vnd.github.v3.raw")
        tokenAccessor.getToken()?.let {
            requestBuilder.addHeader("Authorization", "token $it")
        }

        val future = client.executeRequest(requestBuilder.build())
        future.addListener({
            println("Download complete")
        }, null)
    }
}