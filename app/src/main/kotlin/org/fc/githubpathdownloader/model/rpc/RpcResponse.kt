package org.fc.githubpathdownloader.model.rpc

sealed class RpcResponse<out T : Any> {
    class Result<out T : Any>(val result: T) : RpcResponse<T>()
    class Error(val exception: RpcException) : RpcResponse<RpcException>()
}