default: clean package copy

clean:
	mvn clean
	
package:
	mvn package

install:
	mvn install
	
system-install: clean install copy-rpm
	sudo zypper in -y -f ./danceinterpreter.rpm
	
copy:
	cp ./target/danceinterpreter-*-full.jar ./DanceInterpreter.jar
	
copy-rpm:
	cp ./target/danceinterpreter-*.noarch.rpm ./danceinterpreter.rpm