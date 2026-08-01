[CmdletBinding()]
param(
    [string[]]$Scope = @(),
    [ValidateRange(40, 1000)]
    [int]$MaxDiffLines = 180
)

$ErrorActionPreference = 'Stop'

function Invoke-Git {
    param([string[]]$Arguments)

    $output = @(& git @Arguments)
    if ($LASTEXITCODE -ne 0) {
        throw "git $($Arguments -join ' ') failed with exit code $LASTEXITCODE"
    }
    return $output
}

$repository = (Invoke-Git @('rev-parse', '--show-toplevel') | Select-Object -First 1).Trim()
Set-Location -LiteralPath $repository

$separator = @('--')
if ($Scope.Count -gt 0) {
    $separator += $Scope
}

$tracked = @(Invoke-Git (@('diff', '--name-only') + $separator))
$staged = @(Invoke-Git (@('diff', '--cached', '--name-only') + $separator))
$untracked = @(Invoke-Git (@('ls-files', '--others', '--exclude-standard') + $separator))
$paths = @($tracked + $staged + $untracked | Where-Object { $_ } | Sort-Object -Unique)

if ($Scope.Count -gt 0) {
    $allStaged = Invoke-Git @('diff', '--cached', '--name-only')
    $outsideScope = @($allStaged | Where-Object { $_ -and $_ -notin $paths })
    if ($outsideScope.Count -gt 0) {
        Write-Output 'BLOCKED: staged paths exist outside the requested scope'
        $outsideScope | ForEach-Object { Write-Output "  $_" }
        exit 3
    }
}

if ($paths.Count -eq 0) {
    Write-Output 'CLEAN: no changes to commit'
    exit 0
}

$sensitivePathPattern = '(?i)(^|/)(\.env($|\.)|id_rsa|credentials\.json|auth\.json|data\.sqlite$)|\.(pem|key|p12|pfx)$'
$sensitiveContentPattern = '(?im)-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----|(?:sk|ghp|github_pat|xox[baprs])_[A-Za-z0-9_-]{16,}|AKIA[0-9A-Z]{16}'
$conflictPattern = '(?m)^(<<<<<<< |>>>>>>> )'
$blocked = [System.Collections.Generic.List[string]]::new()

foreach ($path in $paths) {
    $normalized = $path.Replace('\', '/')
    if ($normalized -match $sensitivePathPattern) {
        $blocked.Add("$path (sensitive filename)")
        continue
    }

    $absolutePath = Join-Path $repository $path
    if (-not (Test-Path -LiteralPath $absolutePath -PathType Leaf)) {
        continue
    }

    $file = Get-Item -LiteralPath $absolutePath
    if ($file.Length -gt 2MB) {
        continue
    }

    $content = Get-Content -LiteralPath $absolutePath -Raw -ErrorAction SilentlyContinue
    if ($null -eq $content) {
        continue
    }
    if ($content -match $sensitiveContentPattern) {
        $blocked.Add("$path (possible credential)")
    }
    if ($content -match $conflictPattern) {
        $blocked.Add("$path (merge conflict marker)")
    }
}

if ($blocked.Count -gt 0) {
    Write-Output 'BLOCKED: inspect these paths before staging'
    $blocked | Sort-Object -Unique | ForEach-Object { Write-Output "  $_" }
    exit 4
}

Invoke-Git (@('add', '--') + $paths) | Out-Null

$checkOutput = @(& git diff --cached --check)
if ($LASTEXITCODE -ne 0) {
    $checkOutput
    exit $LASTEXITCODE
}

$cachedNames = Invoke-Git @('diff', '--cached', '--name-status')
if ($cachedNames.Count -eq 0) {
    Write-Output 'CLEAN: staged diff is empty'
    exit 0
}

Write-Output 'PREPARED'
Write-Output 'FILES'
$cachedNames
Write-Output 'STAT'
Invoke-Git @('diff', '--cached', '--stat')
Write-Output 'DIFF'
$diff = Invoke-Git @('diff', '--cached', '--no-ext-diff', '--no-color', '--unified=0')
$diff | Select-Object -First $MaxDiffLines
if ($diff.Count -gt $MaxDiffLines) {
    Write-Output "[DIFF TRUNCATED: $($diff.Count - $MaxDiffLines) additional lines]"
}
