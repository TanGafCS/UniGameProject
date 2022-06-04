To add a texture to the cache, go inside the textures.txt file. Then pick a unique key (string) and texture path (string for filepath, assuming we are in the /Game210 directory.) Connect the key and path with a colon on its own line.
Example:
Player:/src/Player/player.png

In our program, we can then call Textures.getTexture("Player") to access the created texture at any point. Bare in mind that any changes made to the texture will be made across the whole program.

N.B. If adding a tile, or any repeating texture, make sure to include the word "tile" in the string key! (e.g. floorTile)

Will likely also add a third option for scaling if we need to use the same texture at different scales in our program.

If there are any limitations we need fixed just drop a message.