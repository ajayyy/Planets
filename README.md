# Planets

This is multiplayer planet-based game. There are planets, circles with a gravitational pull, that pull all players towards them. When a player collides with the planet, they bounce off. You can press W and A to move left and right relative to the normal of the planet position you are currently in. You can also click on-screen to launch a projectile. Launched projectile will fly in the direction you point and also get pulled in by gravity, but they will not bounce off the planet. When you launch a projectile, you get pushed in the other direction (equal and opposite force). The is another way to move. When a projectile collides with a player, they bounce off of each other, allowing one player to launch a projectile at another player to push them around.


# Technology Stack

This is written in Java with the library Lib-GDX. Lib-GDX is an open-source library that wraps LWJGL function and allows you to export you Java game into many more platforms such as even HTML5/WebGL. LWJGL is a library that is adds Java functions for OpenGL calls.

The networking is handled with a library called Java-WebSockets (https://github.com/TooTallNate/Java-WebSocket). Websockets are used since they are the only network type supported in JavaScript, which I am planning on porting this to later (with Lib-GDX’s WebGL export).

# License

MIT
