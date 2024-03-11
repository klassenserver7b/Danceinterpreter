default: clean package copy

clean:
	mvn clean
	
package:
	mvn package

install:
	mvn install
	
system-install: clean install
	sudo zypper --no-refresh in -y --allow-unsigned-rpm target/danceinterpreter-*.noarch.rpm
	
copy:
	cp ./target/danceinterpreter-*-full.jar ./DanceInterpreter.jar
