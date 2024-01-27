default: clean package copy

clean:
	mvn clean
	
package:
	mvn package
	
copy:
	cp ./target/danceinterpreter-*-full.jar ./DanceInterpreter.jar
	
install:
	mvn install