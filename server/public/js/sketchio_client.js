var ioUri = "http://localhost:8080"; //replace with the Websocket URL
var output;
var socket;
var botCanvas = {};
var isdrawing = false;
var ison = true;
var curStroke = [];
var finishStroke = false;

var lineThickness;

function initWebSocket() {
    botCanvas = document.getElementById('botpad');
	sketchPadCanvas = document.getElementById('sketchpad');
	moveLogo = document.getElementById("logo");

    botCanvas.setAttribute('width', container.offsetWidth);
    botCanvas.setAttribute('height', container.offsetHeight);

    output = document.getElementById("output");
    socket = io.connect(ioUri);

    socket.on('newconnection', onOpen);
    socket.on('respondStroke', onNewStroke);
    socket.on('allData', onDataReceived);

	var logo = document.getElementById("logo");


    var i = 0;
    var botStroke = "";
    var ctx = botCanvas.getContext('2d');
	var ctx2 = sketchPadCanvas.getContext('2d');
    ctx.lineWidth = 0.1;

    $('#ex8').slider().on('slideStop', function (ev) {
        console.log('Current Creativity Value:' + ' ' + ev.value / 100);
        socket.emit("SetCreativty", ev.value);
    });

    var timer = setInterval(function () {

        if (botStroke != "" && i < botStroke.packetPoints.length ) {
            ctx.lineTo(botStroke.packetPoints[i].x, botStroke.packetPoints[i].y);
			console.log(botStroke.packetPoints[i].x);

            ctx.stroke();
			ctx.strokeStyle = x;
			ctx.globalAlpha = opacity2;
			ctx.lineWidth = y;

      console.log(botStroke.packetPoints[i].x);
			moveLogo.style.left = botStroke.packetPoints[i].x - 70;
			moveLogo.style.top = botStroke.packetPoints[i].y - 130;
			//moveLogo.style.backgroundColor = "blue";

            i++;
        } else if (curStroke.length > 0) {
            botStroke = curStroke.shift();
            ctx.beginPath();
            ctx.moveTo(botStroke.packetPoints[0].x, botStroke.packetPoints[0].y);
			ctx.strokeStyle = x;
			ctx.globalAlpha = opacity2;
			ctx.lineWidth = y;
            i = 0;
			//moveLogo.style.left = botStroke.packetPoints[i].x - 70;
			//moveLogo.style.top = botStroke.packetPoints[i].y - 130;
			//moveLogo.style.backgroundColor = "red";


        } else if (botStroke != "") {
            bothInputContext.drawImage(botCanvas, 0, 0);
            ctx.clearRect(0, 0, botCanvas.width, botCanvas.height);
            botStroke = "";
            i = 0;
			//moveLogo.style.backgroundColor = "yellow";
			MoveLogoBack();
        }
    }, 20);

}


function MoveLogoBack () {
	if(finishStroke==false){
	//console.log("move");
	//moveLogo.style.left = '4em';
	//moveLogo.style.top = '5em';
			$('#logo').animate({
					left: '90%',
					top: '-1em'},
				"swing");

	console.log('logo left is ' + moveLogo.style.left);
	}
}


function onNewStroke(data) {
	moveLogo.style.left = "90%";
			moveLogo.style.top = "3%";
    console.log(data);
    // decode the data into the new stroke
    var botStroke = JSON.parse(data);
    curStroke.push(botStroke);
	logo.style.position = "absolute";
	logo.style.left = data.x;
	logo.style.top = data.y;

	//var botPts = botStroke.packetPoints;

 //   if (botPts.length > 1) {

 //       ctx.beginPath();
 //       ctx.moveTo(botPts[0].x, botPts[0].y);
	//	var i = 0;

	//}
}
function onOpen(data) {
    var size = {
        width : container.offsetWidth,
        height: container.offsetHeight
    };
    socket.emit("canvasSize", size);
}

function onDataReceived(allData) {
    console.log('data received');
    console.log(allData);

    var timestamp = String(Date.now());

    var userLines = allData.userLines;
    var computerLines = allData.computerLines;

    var userBlob = new Blob([userLines],
        {type: "text/plain;charset=utf-8"});
    saveAs(userBlob, timestamp + "userLines.txt");

    setTimeout(function() {
        var computerBlob = new Blob([computerLines],
            {type: "text/plain;charset=utf-8"});
        saveAs(computerBlob, timestamp + "computerLines.txt");
    }, 500);
}

function onTouchUp(message) {
    socket.emit('touchup', message);
}

function onTouchDown() {
    socket.emit('touchdown');
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
	if(chk)
	   	socket.emit('setMode', 3);
	else
		socket.emit('setMode', 4);
}

function DownVote() {
    socket.emit('vote', 0);
}

function UpVote() {
    socket.emit('vote', 1);
}

function downloadData() {
    console.log('getting data...');
    socket.emit('getData');
}
function saveDataOnDb() {
    console.log('prompting user id.');
    var userId = prompt('Please enter user id:', '1');
    console.log('prompting session id.');
    var sessionId = prompt('Please enter session id:', '1');
    console.log('saving data...');
    socket.emit('saveDataOnDb', userId, sessionId);
}

function TurnOnOffAgent() {
    ison = !ison;
    if (ison) {
        console.log('turn agent on');
        socket.emit('setAgentOn', true);
		console.log(ison);
    } else {
        console.log('turn agent off');
        socket.emit('setAgentOn', false);
		console.log(ison);
    }
}
