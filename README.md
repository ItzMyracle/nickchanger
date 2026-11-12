# Nick Changer (Fabric 1.21.11, client-side)

Shows a fake name for YOU in the tab list, the side scoreboard, name tags and chat.
Only you see it. Other players still see your real name, and the server is not told anything.

## Commands
- `/nick <name>`        set the fake name (colour codes with &, e.g. `&6&lKing`)
- `/nick off` / `on`    switch it off / on
- `/nick reset`         remove the nick
- `/nick tab|scoreboard|nametag|chat [true/false]`   switch single parts
- `/nick`               show the current settings

Settings are saved in `config/nickchanger.json`.

## Build
Upload the project to GitHub. The workflow in `.github/workflows/build.yml` builds the jar
(Actions -> latest run -> Artifacts -> nickchanger-jar).
