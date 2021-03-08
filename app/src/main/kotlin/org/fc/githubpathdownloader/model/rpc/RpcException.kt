package org.fc.githubpathdownloader.model.rpc

data class RpcException(
        private val statusCode: Int,
        private val errorMessage: String
)