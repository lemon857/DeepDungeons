# Deep Dungeons

Game will be about dungeons like a set of dynamic-generating rooms with mobs, chests and items  
Every room has some doors, in quantity from one to four  
Doors can be locked, every locked door have one unique key same with door lock color  

# Items desctiption

Now game have two items - key and knife  
Key have unique color which open only one locked door  
Knife is a base weapon  

# Attack description

Attack depends on player direction, it showed his eyes  
Every weapon have damage and cooldown property, it affects attack strength and speed  
Now must not get it info in game, maybe later appears info panel for items

# Generation description

Now generation is very simple  
It have several conditions for generate front door and consider doors in neighbor rooms  

# Key bindings

Key  | Actions
------------- | -------------
W  | Go up
A  | Go left
S  | Go down
D  | Go right
E  | Use (pick up, put down, open locked door)
I  | Get info about item
Space  | Attack
*Debug features*
T  | Spawn new skeleton *(for debug)*
G  | Self-damage *(for debug)*
H  | Self-treat *(for debug)*
B  | Self-hunger *(for debug)*
N  | Self-saturation *(for debug)*
J  | Self-thirsty *(for debug)*
K  | Self-drink *(for debug)*
Arrow up | Go top room *(for debug)*
Arrow left | Go left room *(for debug)*
Arrow down | Go bottom room *(for debug)*
Arrow  right | Go right room *(for debug)*