var ioUri = "http://localhost:8080"; //replace with the Websocket URL
var output;
var socket;

function initWebSocket()
{
	output = document.getElementById("output");
	socket = io.connect(ioUri);

	socket.on('newconnection', onOpen);
	socket.on('respondStroke', onNewStroke);
}
function onNewStroke(data){
	console.log(data);
	// 1. decode the data into the new stroke

	// 2. add the new stroke into the queue for drawing

	// 3. anmiate the stroke in the queue


}
function onOpen(data){
	console.log(data);
}
function doSend(message)
{
	//writeToScreen("SENT: " + message);
	socket.emit('newStroke', message);
}
function writeToScreen(message)
{
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";
	pre.innerHTML = message;
	output.appendChild(pre);
}