BIN=./bin
SRC=./src
DOC=./doc

#General build rule
.SUFFIXES: .java .class

${BIN}/%.class: ${SRC}/%.java
	javac $< -cp ${BIN} -d ${BIN}
	
${BIN}/Main.class: ${BIN}/Threaded.class

${BIN}/Threaded.class: ${BIN}/CloudData.class

${BIN}/CloudData.class: ${BIN}/Vector.class


clean:
	rm -f ${BIN}/*.class
	
run:
	java -cp ./bin Main
	
docs:
	javadoc  -classpath ${BIN} -d ${DOC} ${SRC}/*.java

cleandocs:
	rm -rf ${DOC}/*
