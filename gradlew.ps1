param([Parameter(ValueFromRemainingArguments = $true)][string[]] $GradleArgs)
$ErrorActionPreference = 'Stop'
$javaExe = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
& $javaExe '-classpath' (Join-Path $PSScriptRoot 'gradle/wrapper/gradle-wrapper.jar') 'org.gradle.wrapper.GradleWrapperMain' @GradleArgs
exit $LASTEXITCODE
