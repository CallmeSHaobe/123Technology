<# Checks that every BetterQuesting quest / quest line has .name and .desc entries in
   assets/123technology/lang/*.lang (keys are derived from the quest UUID). #>
[CmdletBinding()]
param(
    [string[]]$Language
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

$questRoot = Join-Path $repoRoot 'src/main/resources/assets/123technology/quest/DefaultQuests'
$langDir = Join-Path $repoRoot 'src/main/resources/assets/123technology/lang'

$errors = New-Object System.Collections.Generic.List[string]
$warnings = New-Object System.Collections.Generic.List[string]

function ConvertTo-QuestKey([long]$high, [long]$low) {
    # betterquesting.api.utils.UuidConverter#encodeUuidStripPadding
    $highBytes = [BitConverter]::GetBytes($high)
    [Array]::Reverse($highBytes)
    $lowBytes = [BitConverter]::GetBytes($low)
    [Array]::Reverse($lowBytes)
    $encoded = [Convert]::ToBase64String($highBytes + $lowBytes)
    $encoded = $encoded.Replace('+', '-').Replace('/', '_')
    return $encoded.TrimEnd('=')
}

# ---------------------------------------------------------------- expected keys
$expected = @{}   # key -> where the text comes from
if (-not (Test-Path $questRoot)) { throw "Quest data not found: $questRoot" }

Get-ChildItem (Join-Path $questRoot 'Quests') -Recurse -Filter *.json | ForEach-Object {
    $json = Get-Content $_.FullName -Raw -Encoding UTF8 | ConvertFrom-Json
    $key = ConvertTo-QuestKey ([long]$json.'questIDHigh:4') ([long]$json.'questIDLow:4')
    $expected["betterquesting.quest.$key.name"] = $_.Name
    $expected["betterquesting.quest.$key.desc"] = $_.Name
}

Get-ChildItem (Join-Path $questRoot 'QuestLines') -Recurse -Filter QuestLine.json | ForEach-Object {
    $json = Get-Content $_.FullName -Raw -Encoding UTF8 | ConvertFrom-Json
    $key = ConvertTo-QuestKey ([long]$json.'questLineIDHigh:4') ([long]$json.'questLineIDLow:4')
    $expected["betterquesting.questline.$key.name"] = $_.Directory.Name
    $expected["betterquesting.questline.$key.desc"] = $_.Directory.Name
}

$questPrefixes = @('betterquesting.quest.', 'betterquesting.questline.')
function Test-IsQuestKey([string]$key) {
    foreach ($prefix in $questPrefixes) {
        if ($key.StartsWith($prefix)) { return $true }
    }
    return $false
}

# ---------------------------------------------------------------- .lang parsing
function Read-LangFile([string]$path) {
    $entries = @{}
    $lineNumber = 0
    $broken = New-Object System.Collections.Generic.List[string]
    $duplicates = New-Object System.Collections.Generic.List[string]

    foreach ($line in [System.IO.File]::ReadAllLines($path, [System.Text.Encoding]::UTF8)) {
        $lineNumber++
        if ($line.Length -eq 0) { continue }
        if ($line[0] -eq '#') { continue }

        # Locale uses Splitter.on('=').limit(2): only the first '=' separates key and value.
        $separator = $line.IndexOf('=')
        if ($separator -lt 1) {
            $broken.Add("${lineNumber}: $line")
            continue
        }

        $key = $line.Substring(0, $separator)
        if ($entries.ContainsKey($key)) { $duplicates.Add("${lineNumber}: $key") }
        $entries[$key] = $line.Substring($separator + 1)
    }

    return [pscustomobject]@{
        Entries    = $entries
        Broken     = $broken
        Duplicates = $duplicates
    }
}

function Test-FormatEscaping([string]$value) {
    # Fragments where I18n.format would throw ("Format error: ..."), i.e. a '%' that is
    # neither the escape '%%' nor the line break '%n'. BetterQuesting then falls back to
    # the text stored in the quest json, so these are warnings rather than errors.
    $found = New-Object System.Collections.Generic.List[string]
    $i = 0
    while ($i -lt $value.Length) {
        if ($value[$i] -ne '%') { $i++; continue }
        if ($i + 1 -lt $value.Length -and ($value[$i + 1] -eq '%' -or $value[$i + 1] -eq 'n')) {
            $i += 2
            continue
        }
        $start = [Math]::Max(0, $i - 12)
        $found.Add('...' + $value.Substring($start, [Math]::Min(25, $value.Length - $start)) + '...')
        $i++
    }
    return $found
}

if (-not $Language) {
    $Language = @(Get-ChildItem $langDir -Filter *.lang | ForEach-Object { $_.BaseName })
}

$summary = New-Object System.Collections.Generic.List[string]

foreach ($code in $Language) {
    $path = Join-Path $langDir "$code.lang"
    if (-not (Test-Path $path)) {
        $errors.Add("${code}: missing file $path")
        continue
    }

    $lang = Read-LangFile $path
    foreach ($bad in $lang.Broken) {
        $warnings.Add("${code}: line has no key=value pair, ignored by Minecraft: $bad")
    }
    foreach ($dup in $lang.Duplicates) {
        if (Test-IsQuestKey $dup) { $errors.Add("${code}: duplicate quest key: $dup") }
        else { $warnings.Add("${code}: duplicate key (last one wins): $dup") }
    }

    $missing = 0
    foreach ($key in ($expected.Keys | Sort-Object)) {
        if (-not $lang.Entries.ContainsKey($key)) {
            $missing++
            $errors.Add("${code}: missing key $key (from $($expected[$key]))")
            continue
        }

        $value = $lang.Entries[$key]
        if ([string]::IsNullOrWhiteSpace($value)) {
            $errors.Add("${code}: empty value for $key (from $($expected[$key]))")
            continue
        }
        foreach ($fragment in (Test-FormatEscaping $value)) {
            $warnings.Add("${code}: unescaped '%' in $key $fragment -> shows the json text instead")
        }
    }

    foreach ($key in ($lang.Entries.Keys | Where-Object { Test-IsQuestKey $_ })) {
        if (-not $expected.ContainsKey($key)) {
            $warnings.Add("${code}: quest key without matching quest data (deleted quest?): $key")
        }
    }

    $found = @($expected.Keys | Where-Object { $lang.Entries.ContainsKey($_) }).Count
    $summary.Add(('{0,-6} {1,3}/{2,-3} quest keys present, {3} missing' -f $code, $found, $expected.Count, $missing))
}

$summary | ForEach-Object { Write-Output $_ }

if ($warnings.Count) {
    Write-Output ''
    Write-Output "Warnings ($($warnings.Count)):"
    $warnings | ForEach-Object { Write-Output "  - $_" }
}

if ($errors.Count) {
    Write-Output ''
    Write-Output "Errors ($($errors.Count)):"
    $errors | ForEach-Object { Write-Output "  - $_" }
    exit 1
}

Write-Output ''
Write-Output "Quest book translations are complete and well formed ($($expected.Count) keys checked)."
