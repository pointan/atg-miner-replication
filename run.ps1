# Builds the Docker image and runs the evaluation on examples\.
# Additional arguments restrict the run to the given examples, e.g. .\run.ps1 Calc Xml
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$image = 'atg-miner-replication'

docker build -t $image .
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

docker run --rm -v "${PSScriptRoot}\examples:/work/examples" $image examples/ @args
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ''
foreach ($result in Get-ChildItem examples -Recurse -Filter result.txt) {
    Write-Host ('{0,-16} {1}' -f $result.Directory.Name, (Get-Content $result.FullName -TotalCount 1))
}
