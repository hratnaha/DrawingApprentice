var ioUri = "http://localhost:8080"; //replace with the Websocket URL
var output;
var socket;
var botCanvas = {};
var isdrawing = false;
var curStroke = [];

function initWebSocket() {
    botCanvas = document.getElementById('botpad');
    botCanvas.setAttribute('width', container.offsetWidth);
    botCanvas.setAttribute('height', container.offsetHeight);
    
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
        } else if (botStroke != "") {
            bothInputContext.drawImage(botCanvas, 0, 0);
            ctx.clearRect(0, 0, botCanvas.width, botCanvas.height);
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
	alert('clear canvas');
	myCanvasContext1.clearRect(0, 0, myCanvas1.width, myCanvas1.height);
	myCanvasContext2.clearRect(0, 0, myCanvas2.width, myCanvas2.height);
	myCanvasContext3.clearRect(0, 0, myCanvas3.width, myCanvas3.height);
	//context.clearRect(0, 0, canvas.width, canvas.height);
    socket.emit('clear', 'all');
}
// change the mode base on the UI changes


function setMode(mode) {
   
    switch ($(this).val()) {
        case 'local':
            m = 0;
            break;
        case 'regional':
            m = 1;
            break;
        case 'global':
            m = 2;
            break;
    } 
    socket.emit('setMode', m);
}



function ChangeMode1(){
	alert("Global");
	socket.emit('setMode',2);

}

function ChangeMode2(){
	alert("Regional");
	socket.emit('setMode',1);
}

function ChangeMode3(){
	alert("Local");
	socket.emit('setMode',0)
}

	
function groupingMode(chk) {
	if(this.checked){
	    socket.emit('setMode', 3);
	}
}
function voteUpOrDown(isup) {
    socket.emit('vote', isup);
}