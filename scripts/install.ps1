# Beehiiv MCP Server - Installation Script for Windows PowerShell
# This script downloads and sets up the native binary

param(
    [string]$InstallDir = "$env:LOCALAPPDATA\Programs\BeehiivMCP"
)

$ErrorActionPreference = "Stop"

# Configuration
$RepoUrl = "https://github.com/danvega/beehiiv-mcp-server"
$BinaryName = "beehiiv-mcp-server.exe"

Write-Host "🚀 Installing Beehiiv MCP Server for Windows..." -ForegroundColor Green

# Create install directory
if (!(Test-Path $InstallDir)) {
    New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
    Write-Host "📁 Created directory: $InstallDir" -ForegroundColor Yellow
}

try {
    # Get latest release information
    Write-Host "📡 Fetching latest release information..." -ForegroundColor Cyan
    $LatestUrl = "https://api.github.com/repos/danvega/beehiiv-mcp-server/releases/latest"
    $ReleaseInfo = Invoke-RestMethod -Uri $LatestUrl

    # Extract download URL
    $BinaryFilename = "beehiiv-mcp-server-windows.exe"
    $Asset = $ReleaseInfo.assets | Where-Object { $_.name -eq $BinaryFilename }
    
    if (!$Asset) {
        throw "Could not find Windows binary in latest release"
    }

    $DownloadUrl = $Asset.browser_download_url
    $InstallPath = Join-Path $InstallDir $BinaryName

    Write-Host "⬇️  Downloading $BinaryFilename..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $DownloadUrl -OutFile $InstallPath

    Write-Host "✅ Installation complete!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📍 Binary installed at: $InstallPath" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "🔧 Next steps:" -ForegroundColor Cyan
    Write-Host "1. Configure Claude Desktop by adding this to your config file:" -ForegroundColor White
    Write-Host "   Location: $env:APPDATA\Claude\claude_desktop_config.json" -ForegroundColor Gray
    Write-Host ""
    Write-Host "   {" -ForegroundColor Gray
    Write-Host "     `"mcpServers`": {" -ForegroundColor Gray
    Write-Host "       `"beehiiv`": {" -ForegroundColor Gray
    Write-Host "         `"command`": `"$InstallPath`"," -ForegroundColor Gray
    Write-Host "         `"env`": {" -ForegroundColor Gray
    Write-Host "           `"BEEHIIV_API`": `"your-api-key`"," -ForegroundColor Gray
    Write-Host "           `"BEEHIIV_PUBLICATION_ID`": `"your-publication-id`"" -ForegroundColor Gray
    Write-Host "         }" -ForegroundColor Gray
    Write-Host "       }" -ForegroundColor Gray
    Write-Host "     }" -ForegroundColor Gray
    Write-Host "   }" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Restart Claude Desktop and start managing your newsletter!" -ForegroundColor White
    Write-Host ""
    Write-Host "📚 For more information, visit: $RepoUrl" -ForegroundColor Cyan

} catch {
    Write-Host "❌ Installation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please download manually from: $RepoUrl/releases" -ForegroundColor Yellow
    exit 1
}