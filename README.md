3ddm
====

A Desktop Mate application, opens 3d models and have them dance on your screen with the music.
No models, musics or motions are provided with this application.

Operating System / Minimum specs
----------------
- Windows XP,Vista,7,8
- Linux
- JVM 1.7
- 2Gb memory
- 2Ghz cpu
- OpenGL 4+

(futur plans for Mac and Android)

Supported formats
-----------------

Models
- PMD
- PMX
- XNA (no animation)

Motion
- VMD

Audio
- WAV
- FLAC (bugs, not all files work)
- MP4 (bugs, not all files work)

How to build
------------
- Install a JDK at least 1.6 (http://www.oracle.com/technetwork/java/javase/downloads)
- Install Apache Maven (http://maven.apache.org)
- Install Mercurial (http://mercurial.selenic.com)
- Install Git (http://git-scm.com)

*build the 3d engine*
- open a shell and move to the folder where you want the project
- hg clone https://bitbucket.org/Eclesia/unlicense
- move in folder : cd unlicense-lib
- compile : mvn clean install -DskipTests

*build 3ddm*
- open a shell and move to the folder where you want the project
- git clone https://github.com/silice-hf/3ddm.git
- move in folder : cd 3ddm
- compile : mvn clean install -DskipTests

*run the application*
Use your IDE(eclipse,netbeans,intellij) to run the Game class in the project.




