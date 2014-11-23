var ioUri = "http://localhost:8080"; //replace with the Websocket URL
var output;
var socket;
var botCanvas = {};
var isdrawing = false;
var curStroke = [];
function initWebSocket() {
    botCanvas = document.getElementById('botpad');
    botCanvas.setAttribute('width', container.offsetWidth * 0.95);
    botCanvas.setAttribute('height', container.offsetHeight * 0.90);
    
    output = document.getElementById("output");
    socket = io.connect(ioUri);
    
    socket.on('newconnection', onOpen);
    socket.on('respondStroke', onNewStroke);
    
    var i = 0;
    var botStroke = "";
    var ctx = botCanvas.getContext('2d');
    ctx.width = 0.1;
    var timer = setInterval(function () {
        
        if (botStroke != "" && i < botStroke.packetPoints.length) {
            ctx.lineTo(botStroke.packetPoints[i].x, botStroke.packetPoints[i].y);
            ctx.stroke();
            i++;
        } else if (curStroke.length > 0) {
            botStroke = curStroke.shift();
            ctx.beginPath();
            ctx.moveTo(botStroke.packetPoints[0].x, botStroke.packetPoints[0].y);
            i = 0;
        } else {
            botStroke = "";
            i = 0;
        }
    }, 20);
}
function onNewStroke(data) {
    console.log(data);
    // decode the data into the new stroke
    var botStroke = JSON.parse(data);
    curStroke.push(botStroke);

	//var botPts = botStroke.packetPoints;
	
 //   if (botPts.length > 1) {
        
 //       ctx.beginPath();
 //       ctx.moveTo(botPts[0].x, botPts[0].y);
	//	var i = 0;
		
	//}
}
function onOpen(data) {
    console.log(data);
}
function doSend(message) {
    //writeToScreen("SENT: " + message);
    socket.emit('newStroke', message);
}
function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
}
// save a image to the client's computer
function saveToImage() {

}
// clear the canvas
function clearCanvas() {
    socket.emit('clear', 'all');
}
// change the mode base on the UI changes
function setMode(mode) {
    var m = 0;
    switch ($(this).val()) {
        case 'local':
            m = 0;
            break;
        case 'region':
            m = 1;
            break;
        case 'global':
            m = 2;
            break;
    }
    
    socket.emit('setMode', m);
}
function groupingMode(modenum) {

}