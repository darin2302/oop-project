param([string]$Script = "all")

$ErrorActionPreference = "Stop"
$jar = "target/warehouse-1.0-SNAPSHOT.jar"

if (-not (Test-Path $jar)) {
    Write-Host "Building..."
    mvn -q clean package -DskipTests
}

# Reset work dir each run for reproducibility
$work = "demo/work"
if (Test-Path $work) { Remove-Item -Recurse -Force $work }
New-Item -ItemType Directory -Force -Path $work | Out-Null

# Copy fixtures into work dir so scripts that mutate them don't dirty the fixtures
Copy-Item demo/fixtures/expiring.xml $work/
Copy-Item demo/fixtures/expiring.xml $work/orig.xml

$scripts = if ($Script -eq "all") {
    Get-ChildItem demo/scripts/*.txt | Sort-Object Name
} else {
    @(Get-Item "demo/scripts/$Script")
}

foreach ($s in $scripts) {
    Write-Host "`n============================================================"
    Write-Host "DEMO: $($s.Name)"
    Write-Host "============================================================"
    Get-Content $s.FullName | java -jar $jar
    Write-Host "`n[exit code: $LASTEXITCODE]"
}
