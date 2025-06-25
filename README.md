# Beehiiv MCP Server

> 🚀 **Super Quick Start**: Get Beehiiv newsletter management working in Claude Desktop in under 2 minutes - **no Java required!**

Connect your Beehiiv newsletter to Claude Desktop and other AI assistants. Add subscribers, fetch posts, and manage publications using natural language.

## 🎯 Choose Your Setup Method

| Method | Time | Requirements | Best For |
|--------|------|--------------|----------|
| **[📦 Native Binary](#-native-binary-no-java)** | **2 min** | None! | **Most users** |
| [☕ Java Build](#-java-build-traditional) | 5 min | Java 24+ | Developers |

## What You Can Do

Once set up, you can ask Claude Desktop things like:
- *"Add test@example.com to my newsletter"*
- *"Show me my latest 5 newsletter posts"*
- *"Create a subscriber with custom fields: name John, company Tech Corp"*
- *"List all my publications"*

## 📦 Native Binary (No Java)

**Perfect for most users** - Single file download, no installation required!

### 1. Get Your API Credentials
1. Go to [Beehiiv API Settings](https://app.beehiiv.com/settings/integrations/api)
2. Copy your **API Key** (starts with `bh-`)
3. Copy your **Publication ID** (starts with `pub_`)

### 2. Download Binary

**Option A: One-Command Install** (Linux/macOS)
```bash
curl -fsSL https://raw.githubusercontent.com/danvega/beehiiv-mcp-server/main/scripts/install.sh | bash
```

**Option B: Manual Download**

Go to [Latest Release](https://github.com/danvega/beehiiv-mcp-server/releases/latest) and download:
- **Linux**: `beehiiv-mcp-server-linux`
- **macOS**: `beehiiv-mcp-server-macos`  
- **Windows**: `beehiiv-mcp-server-windows.exe`

Make executable (Linux/macOS only):
```bash
chmod +x beehiiv-mcp-server-*
```

### 3. Configure Claude Desktop

**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`  
**Windows**: `%APPDATA%/Claude/claude_desktop_config.json`

```json
{
  "mcpServers": {
    "beehiiv": {
      "command": "/full/path/to/beehiiv-mcp-server-linux",
      "env": {
        "BEEHIIV_API": "bh-your-api-key-here",
        "BEEHIIV_PUBLICATION_ID": "pub-your-publication-id-here"
      }
    }
  }
}
```

⚠️ **Use the full path** to your downloaded binary file.

### 4. Test It Works
1. Restart Claude Desktop
2. Look for the 🔧 icon in a new conversation  
3. Try: *"Add subscriber test@example.com to my newsletter"*

✅ **Success**: You should see Claude use the `beehiiv_create_subscription` tool!

---

## ☕ Java Build (Traditional)

**For developers who want to build from source**

### Prerequisites
- **Java 24+** ([Download here](https://adoptium.net/))
- **Maven** (included via `./mvnw`)

### Steps
```bash
git clone <this-repo>
cd beehiiv-mcp-server
./mvnw clean package -DskipTests
```

Then configure Claude Desktop with:
```json
{
  "mcpServers": {
    "beehiiv": {
      "command": "java",
      "args": [
        "-jar", 
        "/FULL/PATH/TO/target/beehiiv-mcp-server-0.0.3-SNAPSHOT.jar"
      ],
      "env": {
        "BEEHIIV_API": "bh-your-api-key-here",
        "BEEHIIV_PUBLICATION_ID": "pub-your-publication-id-here"
      }
    }
  }
}
```

## 🔥 Native Image Build (Advanced)

**For developers who want to create optimized native binaries**

Native image compilation creates fast-starting, low-memory executables that don't require Java to run.

### Prerequisites
- **Java 24+** with GraalVM ([Download GraalVM](https://www.graalvm.org/downloads/))
- **Maven** (included via `./mvnw`)

### Build Native Image
```bash
git clone <this-repo>
cd beehiiv-mcp-server
./mvnw clean package -Pnative -DskipTests
```

This creates platform-specific binaries in `target/`:
- **Linux**: `beehiiv-mcp-server-linux` 
- **macOS**: `beehiiv-mcp-server-macos`
- **Windows**: `beehiiv-mcp-server-windows.exe`

### Configure Claude Desktop
Use the native binary directly without Java:

```json
{
  "mcpServers": {
    "beehiiv": {
      "command": "/full/path/to/beehiiv-mcp-server-linux",
      "env": {
        "BEEHIIV_API": "bh-your-api-key-here",
        "BEEHIIV_PUBLICATION_ID": "pub-your-publication-id-here"
      }
    }
  }
}
```

### Benefits
- **Fast startup**: ~50ms vs ~2s for Java
- **Low memory**: ~20MB vs ~100MB for Java  
- **No Java required**: Self-contained executable
- **Better for production**: Optimized runtime performance

## Prerequisites

- **Java 24+** ([Download here](https://adoptium.net/))
- **Maven** (included via `./mvnw`)
- **Beehiiv account** with API access
- **Claude Desktop** ([Download here](https://claude.ai/download))

## Available Tools

### 📧 Subscription Management
- **Add subscribers**: Create new subscriptions with custom fields
- **Find subscribers**: Look up by email or ID
- **Custom fields**: Add structured data to subscribers

### 📝 Content Management  
- **Get posts**: Fetch your published newsletters
- **Single post**: Get detailed content for specific posts
- **Filtering**: Search by tags, date, audience type

### 🏢 Publication Management
- **List publications**: See all your newsletters
- **Publication details**: Get stats and settings
- **Multi-publication**: Work with multiple newsletters

## Example Usage

### Basic Subscriber
```
Add john.doe@company.com to my newsletter
```

### Subscriber with Custom Data
```
Create a subscription for sarah@startup.com with custom fields: 
name "Sarah Johnson", role "CEO", company "TechStart"
```

### Get Recent Posts
```
Show me my last 10 newsletter posts with their titles and publish dates
```

### UTM Tracking
```
Add marketing@bigcorp.com with UTM source "website", 
medium "signup", campaign "q4-growth"
```

## Advanced Configuration

### Multiple Publications
Don't set `BEEHIIV_PUBLICATION_ID` to work with multiple newsletters:

```json
{
  "env": {
    "BEEHIIV_API": "bh-your-api-key-here"
  }
}
```

Then specify the publication in your requests:
```
Add user@example.com to publication pub_specific123
```

### HTTP Mode (Alternative)
For testing or development, you can run as a web server:

```bash
java -jar target/beehiiv-mcp-server-0.0.2-SNAPSHOT.jar
```

Then use: `"httpUrl": "http://localhost:8080/mcp"` in Claude Desktop config.

## Troubleshooting

### "Tool not found" in Claude Desktop
1. Check the JAR path is correct and absolute
2. Verify environment variables are set
3. Restart Claude Desktop completely
4. Check Claude Desktop's logs/console for errors

### "Invalid API key" errors
1. Verify your API key starts with `bh-`
2. Check it's copied completely (no extra spaces)
3. Ensure your Beehiiv account has API access enabled

### "Publication not found"
1. Verify your Publication ID starts with `pub_`
2. Check you have access to this publication
3. Try without `BEEHIIV_PUBLICATION_ID` and specify it per request

### Enable Debug Logging
Set environment variable: `LOGGING_LEVEL_ROOT=DEBUG`

### Test Connection Manually
```bash
# Test the server directly
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc": "2.0", "id": "1", "method": "tools/list"}'
```

## Development

### Run Tests
```bash
./mvnw test
```

### Build from Source
```bash
./mvnw clean package
```

### Project Structure
```
src/main/java/dev/danvega/beehiiv/
├── Application.java              # Main Spring Boot app
├── core/                        # Configuration & utilities
├── post/                        # Newsletter post management
├── publication/                 # Publication management  
└── subscription/                # Subscriber management
```

## API Reference

For detailed Beehiiv API documentation: [developers.beehiiv.com](https://developers.beehiiv.com/api-reference)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## Support

- **Issues**: [GitHub Issues](https://github.com/your-org/beehiiv-mcp-server/issues)
- **Beehiiv API**: [Official Documentation](https://developers.beehiiv.com)
- **MCP Protocol**: [Model Context Protocol](https://modelcontextprotocol.io)