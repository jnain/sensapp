# SensApp in a Nutshell

SensApp is a platform to support sensor based application. It is developed by 
SINTEF (IKT division, NSS Department, MOD research group). 

This repository is oriented to developers. End-users or business experts 
should refer to the following webpage: http://sensapp.modelbased.net

## How to create a new SensApp Service?

 * Run the maven tool from the command line

<pre><code>mvn archetype:generate<code></pre>

  * Select the "service" archetype provided by SensApp, and fill in the blanks

<pre><code>[...]
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 186: sensapp
Choose archetype:
1: local -> net.modelbased.sensapp.archetype:net.modelbased.sensapp.archetype.service (A Prototypical SensApp Service, integrated with the others)
2: local -> net.modelbased.sensapp.archetype:net.modelbased.sensapp.archetype.system (A Prototypical SensApp System, integrating Services)
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): : 1
Define value for property 'groupId': : net.modelbased.sensapp.service 
Define value for property 'artifactId': : net.modelbased.sensapp.service.registry
Define value for property 'version':  1.0-SNAPSHOT: : 0.0.1-SNAPSHOT
Define value for property 'package':  net.modelbased.sensapp.service: : net.modelbased.sensapp.service.registry
Confirm properties configuration:
groupId: net.modelbased.sensapp.service
artifactId: net.modelbased.sensapp.service.registry
version: 0.0.1-SNAPSHOT
package: net.modelbased.sensapp.service.registry
 Y: : Y</code></pre>

  * Enjoy!