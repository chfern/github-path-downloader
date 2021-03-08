package org.fc.githubpathdownloader.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.asynchttpclient.*
import org.fc.githubpathdownloader.model.GithubRemoteFileInfo
import org.fc.githubpathdownloader.model.rpc.RpcException
import org.fc.githubpathdownloader.model.rpc.RpcResponse
import java.lang.StringBuilder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

class GithubDownloaderService(private val githubRepoToken: String,
                              private val username: String,
                              private val repository: String,
                              private val path: String?,
                              private val ref: String?,
                              private val executor: Executor,
                              private val fileDownloaderService: FileDownloaderService) {
    val objectMapper: ObjectMapper by lazy {
        ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun execute(onCompleteHandler: OnCompleteHandler) {
        val clientBuilder: DefaultAsyncHttpClientConfig.Builder = Dsl.config()
                .setConnectTimeout(30000)
        val client: AsyncHttpClient = Dsl.asyncHttpClient(clientBuilder)

        val requestUrlBuilder = StringBuilder("https://api.github.com/repos/$username/$repository/contents")
        path?.let { requestUrlBuilder.append("/$it") }
        ref?.let { requestUrlBuilder.append("?ref=$ref") }

        val requestBuilder: RequestBuilder = Dsl.get(requestUrlBuilder.toString())
                .addHeader("Accept", "application/vnd.github.v3.raw")
                .addHeader("Authorization", "token ${githubRepoToken}")

        val getGithubFileInfosSuccessFuture = CompletableFuture<List<GithubRemoteFileInfo>>()
        val getGithubFileInfosErrFuture = CompletableFuture<RpcException>()
        client.executeRequest(requestBuilder.build())
                .toCompletableFuture()
                .thenApply {
                    if (it.statusCode != 200) {
                        return@thenApply RpcResponse.Error(RpcException(it.statusCode, it.responseBody))
                    }

                    val githubRemoteFiles = objectMapper.readValue<List<GithubRemoteFileInfo>>(it.getResponseBody(),
                            object : TypeReference<List<GithubRemoteFileInfo>>() {})
                    return@thenApply RpcResponse.Result(githubRemoteFiles)
                }
                .thenAccept {
                    when (it) {
                        is RpcResponse.Result -> getGithubFileInfosSuccessFuture.complete(it.result as List<GithubRemoteFileInfo>)
                        is RpcResponse.Error -> getGithubFileInfosErrFuture.complete(it.exception)
                    }
                }

        getGithubFileInfosErrFuture.thenApply { onCompleteHandler.onException(it) }

        getGithubFileInfosSuccessFuture
                .thenCompose { it ->
                    val futures = it
                            .filter { it.downloadUrl != null }
                            .map { remoteFileInfo ->
                                CompletableFuture.supplyAsync(Supplier<Unit> {
                                    fileDownloaderService.download(remoteFileInfo.downloadUrl!!, remoteFileInfo.name)
                                }, executor)
                            }
                    CompletableFuture.allOf(*futures.toTypedArray())
                }.thenRun {
                    onCompleteHandler.onComplete()
                }
    }

    interface OnCompleteHandler {
        fun onComplete()
        fun onException(rpcException: RpcException)
    }
}