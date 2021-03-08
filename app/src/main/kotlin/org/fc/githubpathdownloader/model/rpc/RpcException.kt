package org.fc.githubpathdownloader.model.rpc

data class RpcException(
        val statusCode: Int,
        val errorMessage: String
)