# BaconTweaks

BaconTweaks is a collection of tweaks and addons I made for a private vanilla-ish multiplayer survival Minecraft server.  
  
**It must be modified and compiled on your own to function correctly.**  

It has a few parts:
## Discord Bridge
Requires a Discord bot.
- Provides a system to link a Minecraft player and a Discord user. Start the process with `/beginlink` in Minecraft and `/endlink` in Discord (or just click to copy the provided command from Minecraft chat)
  - Once complete, any mentions in Minecraft or Discord will be reflected with their equivalent username on the other platform.
- Bridges Minecraft chat with a Discord text channel.
- Bridges two SimpleVoiceChat groups with a Discord voice channel.
  - `/joinvc`: Joins a group where you can hear players (or Discord users) in your group and nearby players.
  - `/joinopenvc`: Joins a group where you can hear players (or Discord users) in your group and nearby players, and nearby players can hear players (or Discord users) in your group.
- Sends Minecraft joins and leaves in a Discord text channel.
- Adds a command (`/stop`) to stop the Minecraft server.<br/>

**NEEDS TO BE CONFIGURED TO WORK**
## Markdown
Parses Markdown sent in the Discord bridge channel or Minecraft chat and applies the Markdown styles in Minecraft.  
Supports the following Markdown types:
- `*Emphasis/Italics*`
- `**Strong Emphasis/Bold**`
- `__Strong Emphasis/Underline__` (treated separately)
- `~~Strikethrough~~`
- `Plain text!`
## Server Resource Pack
Adds the command `/refreshresourcepack` (requires permission level 3) to Minecraft.  
This exists because I have a GitHub Action set up to run a shell script that takes the contents of a private GitHub repo with a resource pack in it and does the following steps:
1. Minify all JSON
2. Zip the resource pack
3. Calculate the SHA1 hash of the resource pack and place it in a file called `sum.txt`
4. Publish the resource pack and `sum.txt` to the internet  
So, when you run `/refreshresourcepack`, the server will grab the latest hash from the web server and return it when clients ask for the latest resource pack info. This way, you can update your public resource pack without needing to restart your server.<br/>

**NEEDS TO BE CONFIGURED TO WORK**
## Party Tricks
When you interact with a wooden hoe, it will smite the location you are looking at.
