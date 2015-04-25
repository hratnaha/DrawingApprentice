// Retrieve
function redraw(){
	
	var java = require("java");
	java.classpath.push("mongo-java-driver-2.11.3.jar");
	java.classpath.push("test.jar");   
	var apprentice = new Apprentice();

	console.log(apprentice.mongoPrint(true));	
}