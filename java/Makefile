CLASSPATH=/usr/share/java/json-simple.jar:/usr/share/java/joda-time-2.9.2.jar:.

all: Example.class
	java -cp "$(CLASSPATH)" Example

Example.class: Example.java $(wildcard com/ntechlab/*.java)
	javac -cp "$(CLASSPATH)" -Xlint:unchecked $<

clean:
	find -name '*.class' -delete
