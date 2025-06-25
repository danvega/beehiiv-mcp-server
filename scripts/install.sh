#!/bin/bash

# Beehiiv MCP Server - Installation Script for Linux/macOS
# This script downloads and sets up the native binary

set -e

# Configuration
REPO_URL="https://github.com/danvega/beehiiv-mcp-server"
INSTALL_DIR="$HOME/.local/bin"
BINARY_NAME="beehiiv-mcp-server"

# Detect platform
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case "$OS" in
    linux)
        PLATFORM="linux"
        ;;
    darwin)
        PLATFORM="macos"
        ;;
    *)
        echo "❌ Unsupported operating system: $OS"
        echo "Please download the binary manually from: $REPO_URL/releases"
        exit 1
        ;;
esac

echo "🚀 Installing Beehiiv MCP Server for $PLATFORM..."

# Create install directory
mkdir -p "$INSTALL_DIR"

# Get latest release information
echo "📡 Fetching latest release information..."
LATEST_URL="https://api.github.com/repos/danvega/beehiiv-mcp-server/releases/latest"
RELEASE_INFO=$(curl -s "$LATEST_URL")

if [ $? -ne 0 ]; then
    echo "❌ Failed to fetch release information"
    echo "Please check your internet connection and try again"
    exit 1
fi

# Extract download URL
BINARY_FILENAME="beehiiv-mcp-server-$PLATFORM"
DOWNLOAD_URL=$(echo "$RELEASE_INFO" | grep -o "https://github.com/danvega/beehiiv-mcp-server/releases/download/[^\"]*/$BINARY_FILENAME" | head -1)

if [ -z "$DOWNLOAD_URL" ]; then
    echo "❌ Could not find download URL for $PLATFORM"
    echo "Please download manually from: $REPO_URL/releases"
    exit 1
fi

echo "⬇️  Downloading $BINARY_FILENAME..."
curl -L -o "$INSTALL_DIR/$BINARY_NAME" "$DOWNLOAD_URL"

if [ $? -ne 0 ]; then
    echo "❌ Failed to download binary"
    exit 1
fi

# Make executable
chmod +x "$INSTALL_DIR/$BINARY_NAME"

echo "✅ Installation complete!"
echo ""
echo "📍 Binary installed at: $INSTALL_DIR/$BINARY_NAME"
echo ""
echo "🔧 Next steps:"
echo "1. Add $INSTALL_DIR to your PATH if not already added:"
echo "   echo 'export PATH=\"$INSTALL_DIR:\$PATH\"' >> ~/.bashrc"
echo "   source ~/.bashrc"
echo ""
echo "2. Configure Claude Desktop by adding this to your config:"
echo "   {" 
echo "     \"mcpServers\": {"
echo "       \"beehiiv\": {"
echo "         \"command\": \"$INSTALL_DIR/$BINARY_NAME\","
echo "         \"env\": {"
echo "           \"BEEHIIV_API\": \"your-api-key\","
echo "           \"BEEHIIV_PUBLICATION_ID\": \"your-publication-id\""
echo "         }"
echo "       }"
echo "     }"
echo "   }"
echo ""
echo "3. Restart Claude Desktop and start managing your newsletter!"
echo ""
echo "📚 For more information, visit: $REPO_URL"