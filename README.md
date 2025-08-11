# Deep Dungeons

Game will be about dungeons like a set of dynamic-generating rooms with mobs, chests and items  
Every room has some doors, in quantity from one to four  
Doors can be locked, every locked door have one unique key same with door lock color  

Now room can be generated with some Skeletons, if you need you can kill they, but they can take revenge

## Player description  

If you very hungry you lose some health points every third second  
If you have strength modifier more than one you lose some food points every 4.5th second  
If you have attack speed modifier more than one you lose some thirst points every 5th second  
Luck is ... variable, which is indirectly can change game probabilities  

## Items description

Items have a four tiers: `common`, `uncommon`, `startling`, `incredible`  
Now game have five items - key, knife, bone, rope, leather, candy, meat, bottle of water, cup of tea  
key have unique color which open only one locked door  
knife is a base weapon  
bone, rope and leather - common items for craft  
candy, meat - food  
bottle of water, cup of tea - drinks  

## Mobs description

Mobs like items have a four tiers: `humble`, `general`, `wicked`, `aggressive`  
Now game have one mob - skeleton

Skeleton can attack with hand and knife, attack depends on his direction, good luck

## Attack description

Attack depends on player direction, it showed his eyes  
Every weapon have damage and cooldown property, it affects attack strength and speed  
Now must not get it info in game, maybe later appears info panel for items

If your invenory is empty you can use hand, mobs without weapons also use hand  

## Generation description

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
Y  | Eat and drink
Space  | Attack
Esc | Pause menu
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