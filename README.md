# Github path downloader

A simple utility to download files a path in a github repo.

Due to this library needing to fire a new http request for each to-be-downloaded file, files in nested folders will not be recursively downloaded.

# Usage

## Getting the jar

You can build the jar yourself by running `./gradlew app:jar`, or you can also download the jar from available github releases

## Executing the jar

This tool requires a github token with repo read access, either by setting an env variable with key `GITHUB_REPO_ACCESS_TOKEN`. or by passing an argument `-t` when executing the jar

```
java -jar app.jar <args>
```

Arguments:
- `-U` / `--username` - Github username
- `-R` / `--repository` - Github repository
- `-p` / `--path` - (optional) Folder path, e.g: myfolder/nestedfolder
- `-r' / `--ref` - (optional) Github ref
- `-t` / `--token` - (optional) Github token with read repo access, omit this when token is already set in env