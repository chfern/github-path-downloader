package org.fc.githubpathdownloader.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubRemoteFileInfo(
        @JsonProperty("name") val name: String,
        @JsonProperty("download_url") val downloadUrl: String?
)
