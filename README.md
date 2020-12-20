# LocustKing The Game #

<img src="demo/20201220_152646.gif" width="75%">

### About ###

Creators: Eva Lau @evamlau, Ethan Lebowitz @EthanLebowitz, Karina Thanawala @kthanawala21

Created as the final project for CS-112 at Amherst College in 2018. 

LocustKing is a cute little JPanel game in which the player (the Locust King) leads a simulated swarm of locusts to pillage a pixelated countryside. As-is this game is fully playable, but Eva and Ethan may add some features just for fun. 

### Instructions ###

To compile: 
javac *.java

To run:
java Main

To terminate:
Close the game window (i.e., x out). 

User input:
Control the movement of the leader boid (the sprite that looks like a little locust wearing a crown) by moving your mouse. The leader will fly towards your cursor. Follower locusts (little black dots) will follow the leader. Move your cursor closer to the leader to slow it down, move it further away to speed it up. Don’t go too fast, followers might not be able to keep up with you! If you outpace the followers, wait a moment or slow down and they will find you again.

Note: the MouseListener will not see your mouse position if your cursor is not over the game window. 

### Gameplay ###

You start off with one follower. You can gain more followers by draining the life from green (alive) tiles, which you do by hovering your followers over them until they’re brown or yellow (dead). The moment a tile becomes dead, there’s a chance that a couple new locusts will join your swarm and follow you. The more life a tile has, the greener it is, and the more slowly it will become dead. Yellower green tiles have less life and will become dead more quickly. Brown or yellow tiles have zero life and are dead. The more followers you have, the more quickly you will drain life from tiles. Don’t linger on dead tiles for too long, as being on dead tiles will cause your followers to die and reduce in number. 

Statistics for your current game are displayed in the top left corner of the game window. Percentage of tiles that remain alive, percentage of tiles that have become dead, and follower count is shown. These stats indicate your progress and health in the game. 

Mountains:
Mountain tiles are the ones with black edges and white centers. They will noticeably slow leader speed, due to rough terrain. Mountain tiles count as dead tiles and lingering on them will reduce the size of your swarm of followers. It’s best to avoid mountain tiles, if possible. 

Losing:
You lose when your follower count reaches zero. Close the game window and try again.

Winning:
You win when you have consumed all of the life on the map and your follower count has reached zero, i.e., when you, the leader, are the only alive thing on the map. Congratulations, you are the Locust King. Close the game window and try again.

Tips: The more followers you have the faster you should go to minimize the time your followers spend on dead tiles. Conversely, when you have very few followers you should probably go more slowly because draining the life completely from tiles takes longer. If you want to win, be systemic. Don’t paint yourself into a corner so that there is a sea of dead tiles separating you from the remaining alive tiles. 