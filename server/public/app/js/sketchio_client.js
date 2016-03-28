var ioUri = "http://130.207.124.45"; //"http://localhost:8080"; //

var output;
var socket;
var botCanvas = {};
var isdrawing = false;
var ison = true;
var randomLines = false; //use random lines? 
var curStroke = [];
var finishStroke = false;
var lineThickness;
var totalScore = 0;
var scoreGiven = 0;
var tipColor = "#000000";

function initWebSocket() {
    
    botCanvas = document.getElementById('botpad');
	sketchPadCanvas = document.getElementById('sketchpad');
	moveLogo = document.getElementById("logo");

    botCanvas.setAttribute('width', container.offsetWidth);
    botCanvas.setAttribute('height', container.offsetHeight);

    output = document.getElementById("output");

    //socket = io.connect(ioUri); // for local version
    socket = io.connect(ioUri, { 'path': '/DrawingApprentice/socket.io' }); // for adam server
    
    socket.on('newconnection', onOpen);
    socket.on('respondStroke', onNewStroke);
    socket.on('allData', onDataReceived);
    socket.on('disconnected', saveDataOnDb);
    socket.on('updateScore', onUpdateScore);
    socket.on('classifyObject', onClassifyObject)

	var logo = document.getElementById("logo");

    var i = 0;
    var botStroke = "";
    var botColor = "#000000";
    var ctx = botCanvas.getContext('2d');
	var ctx2 = sketchPadCanvas.getContext('2d');
    ctx.lineWidth = 0.1;

    $('#ex8').slider().on('slideStop', function (ev) {
        console.log('Current Creativity Value:' + ' ' + ev.value / 100);
        socket.emit("SetCreativty", ev.value);
    });

    var timer = setInterval(function () {

        if (botStroke != "" && i < botStroke.allPoints.length ) {
            ctx.lineTo(botStroke.allPoints[i].x, botStroke.allPoints[i].y);

            ctx.stroke();
			//ctx.strokeStyle = botColor;
            ctx.globalAlpha = opacity2;
            ctx.lineWidth = botStroke.lineWidth; 
			$("#tip").css({fill: botColor});
			$("#tip-highlight").css({
				fill: '#F0F0F0',
				opacity: .5,
				});
			moveLogo.style.left = botStroke.allPoints[i].x - 70;
			moveLogo.style.top = botStroke.allPoints[i].y - 130;
			//moveLogo.style.backgroundColor = "blue";

            i++;
        } else if (curStroke.length > 0) {
            botStroke = curStroke.shift();
            botColor = rgbDoubleToHex(botStroke.color.r, botStroke.color.g, botStroke.color.b);
            ctx.beginPath();
            ctx.moveTo(botStroke.allPoints[0].x, botStroke.allPoints[0].y);
			ctx.strokeStyle = botColor;
            ctx.globalAlpha = opacity2;
            ctx.lineWidth = botStroke.lineWidth;
			$("#tip").css({fill: botColor});
			$("#tip-highlight").css({
				fill: '#F0F0F0',
				opacity: .5,
				});
            i = 0;
			//moveLogo.style.backgroundColor = "red";


        } else if (botStroke != "") {
            bothInputContext.drawImage(botCanvas, 0, 0);
            ctx.clearRect(0, 0, botCanvas.width, botCanvas.height);
            botStroke = "";
			$("#tip").css({fill: botColor});
			$("#tip-highlight").css({
				fill: '#F0F0F0',
				opacity: .5,
				});
            i = 0;
			//moveLogo.style.backgroundColor = "yellow";
			MoveLogoBack();
        }
    }, 20);

}


function MoveLogoBack () {
	if(finishStroke==false){
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
}

function onOpen(data) {
    var hello = {
        width : container.offsetWidth,
        height: container.offsetHeight,
        user: userData,
        sessionId: sessionId
    };
    socket.emit("onOpen", hello);
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

var isGrouping = false;
function changeGrouping(){
    if (isGrouping) {
        isGrouping = false;
        $('#group').removeClass('active')
        socket.emit('setMode', 4);
    }
    else {
        isGrouping = true;
        $('#group').addClass('active')
        socket.emit('setMode', 3);
    }
}

function setGroupLabel(label){
    var groupLabel = label;
    //stringify and emit the label to server

}

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
    console.log("Grouping mode chk=" + chk); 
    if (chk) {
        socket.emit('setMode', 3);
    }
    else {
        socket.emit('setMode', 4);
    }
}

function DownVote() {
    socket.emit('vote', 0, totalScore);
}

function UpVote() {
    console.log("upvoted"); 
	socket.emit('vote', 1, totalScore);
}

function downloadData() {
    console.log('getting data...');
    socket.emit('getData');
}


function downloadCanvas(link) {
    var canvas = document.getElementById('both');
    var context = canvas.getContext('2d'); 
    var w = canvas.width;
    var h = canvas.height;

    var data = context.getImageData(0, 0, w, h);
    var compositeOperation = canvas.globalCompositeOperation;
    context.globalCompositeOperation = "destination-over";
    context.fillStyle = "#FFFFFF";
    context.fillRect(0, 0, w, h);
    var imageData = canvas.toDataURL("image/PNG");
    
    context.clearRect(0, 0, w, h);
    context.putImageData(data, 0, 0);
    context.globalCompositeOperation = compositeOperation; 
    
    link.href = imageData; 
    link.download = 'test.png';
}


//$.unload(saveDataOnDb);

function saveDataOnDb() {
    console.log('userId is... ' + userData.id);
    var userId = userData.id;
    console.log('saving new session with id... ' + sessionId);
    console.log('saving data...');
    socket.emit('saveDataOnDb', userId, sessionId);
}

function getNumSessions() {
  var numSessions = 0;
  for (var key in userData) {
    if (key.substring(0, 7) === "session") {
      numSessions++;
    }
  }
  return numSessions;
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

function setRoomType(type){
    socket.emit('setRoomType', type);
}

function onUpdateScore(newScore){
    var score = JSON.parse(newScore);
    totalScore = score; 
    console.log("Inside update score:" + " " + totalScore);
    // document.getElementById("score").innerHTML = "total score = " + totalScore;
	//console.log("totalScore is:" + " " + totalScore);
}

function onClassifyObject(label){
    var newLabel = JSON.parse(label)
    document.getElementById('label').value = newLabel;
}

