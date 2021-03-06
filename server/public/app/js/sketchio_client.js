var ioUri = "http://130.207.124.45:80"; //"http://localhost:8080"; //

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
var canvas;
var plotterQueue = [];


function initWebSocket() {
    // Initialize CNC Server connection details
    // (thanks to CORS support, this should be fully portable)
    cncserver.api.server = {
        domain: document.domain,
        port: location.port ? location.port : 80,
        protocol: 'http',
        version: '1'
    };

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
    socket.on('classifyObject', onClassifyObject); 
    socket.on('statsData', onStatsQuery); 

	var logo = document.getElementById("logo");

    var i = 0;
    var botStroke = "";
    var botColor = "#000000";
    var ctx = botCanvas.getContext('2d');
	var ctx2 = sketchPadCanvas.getContext('2d');
    ctx.lineWidth = 0.1;

	
	/*$('#slider').slider().on('slideStop', function (ev) {
        console.log('Current Creativity Value:' + ' ' + ev.value / 100);
        socket.emit("SetCreativty", ev.value);
		
    });*/
	

		
		$('#slider').slider({
			change: function(event, ui) {
				 console.log('Current Creativity Value:' + ' ' + ui.value/ 100);
       		     socket.emit("SetCreativty", ui.value);
			}
		});
	


   /* $('#ex8').slider().on('slideStop', function (ev) {
        console.log('Current Creativity Value:' + ' ' + ev.value / 100);
        socket.emit("SetCreativty", ev.value);
    });*/

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
            if(botStroke.allPoints.length > 0) {
                addtoQueue(botStroke.allPoints, botCanvas.width, botCanvas.height);
                console.log("Bot Stroke Added");
                ctx.moveTo(botStroke.allPoints[0].x, botStroke.allPoints[0].y);
            }
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

    var plotterTimer = setInterval(function () {
        if (plotterQueue.length > 0) {
            stroke = plotterQueue.shift();
            cncserver.api.pen.up(cncserver.cmd.cb);
            stroke.forEach(function(point) {
                 var x = point[0];
                 var y = point[1];
                 cncserver.api.pen.move({
                     x: x,
                     y: y,
                 });
                 cncserver.api.pen.down(cncserver.cmd.cb);
                 console.log("Point Drawn");
            });
            cncserver.api.pen.up(cncserver.cmd.cb);
        }
    }, 500);
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
    var labeledGroups = allData.labeledGroups; 

    var userBlob = new Blob([userLines],
        {type: "text/plain;charset=utf-8"});
    saveAs(userBlob, timestamp + "userLines.txt");
    
    var groupBlob = new Blob([userLines],
        { type: "text/plain;charset=utf-8" });
    saveAs(userBlob, timestamp + "groupLines.txt");

    setTimeout(function() {
        var computerBlob = new Blob([computerLines],
            {type: "text/plain;charset=utf-8"});
        saveAs(computerBlob, timestamp + "computerLines.txt");
    }, 500);
}

function onStatsQuery(allData) {
    var userLines = JSON.parse(allData.userLines);
    var computerLines = JSON.parse(allData.computerLines);
    var data = {
        userLines: userLines, 
        computerLines: computerLines
    }; 
    //var room = JSON.parse(allData.room);
    //var upVote = room.upVoteCount;
    //var downVote = room.downVoteCount;
    //console.log("Upvotes: " + upVote + "DownVotes: " + downVote); 
    InitChart(data);
    InitChart2(userLines);
    InitChart3(userLines); 
    //var userLines = allData.userLines;
    console.log(userLines);
    console.log("First comp line length: " + computerLines[0].totalDistance);
    var userLineCount = userLines.length;
    var compLineCount = computerLines.length;
    var avgUserLineLength = getAverageLength(userLines);
    var userTimeData = getUserDrawingTime(userLines);
    var averageLineTime = userTimeData.averageTime;
    var totalDrawingTime = userTimeData.totalTime;
    
    
    //var compLines = allData.compLines;
    //var compLineCount = compLines.length; 
    $("#myInnerModal").append("<p>Number of user lines: " + userLineCount + "</p><br>");
    $("#myInnerModal").append("<p>Number of computer lines: " + compLineCount + "</p><br>");
    $("#myInnerModal").append("<p>Average length of user lines: " + avgUserLineLength + "</p><br>");
    $("#myInnerModal").append("<p>Average time spent on line: " + averageLineTime + "ms</p><br>");
    $("#myInnerModal").append("<p>Total time spent drawing: " + totalDrawingTime + "ms</p><br>");
    
    //$("#myInnerModal").append("<p>Average length of computer lines: " + avgCompLineLength + "</p><br>");
    
    function getAverageLength(lines) {
        console.log("Line: " + lines);
        var sum = 0;
        for (var i = 0; i < lines.length; i++) {
            console.log("Total dist for line: " + lines[i].totalDistance);
            sum = sum + lines[i].totalDistance;
            console.log("New sum for averaging: " + sum);
        }
        var avg = sum / lines.length;
        console.log(avg);
        return avg;
    }
    
    function getUserDrawingTime(lines) {
        console.log("Trying to get user drawing time"); 
        var totalTime = 0;
        var totalWait = 0; 
        for (var i = 0; i < lines.length; i++) {
            var curLine = lines[i];
            var points = curLine.allPoints;
            var finalPoint = points[points.length - 1];
            var finalTime = finalPoint.timestamp;
            var elapsedTime = finalTime - points[0].timestamp;
            console.log("Elapsed: " + elapsedTime);
            totalTime = totalTime + elapsedTime;
            /*
            if(i >= 1) {
                var previousLineEndPoint = line[i-1].allPoints[line[i-1].allPoints.length - 1];
                var finalTimeStamp = finalPoint.timestamp;
                var curStartPoint = line[i].allPoints[0]; 
                var curStamp = curEndPoint.timestamp; 
                var hangTime = curStamp - finalTimeStamp; 
                totalWait = totalWait + hangTime; 
                console.log("Wait:" + totalWait); 
        }
             * */
        }
        var averageTime = totalTime / lines.length;
        var values = {
            averageTime: averageTime, 
            totalTime: totalTime
        }
        console.log("Total time: " + totalTime); 
        return values;
    }
}


function onTouchUp(message) {
    socket.emit('touchup', message);
    
    sketchPadCanvas = document.getElementById('sketchpad');

    userStroke = userStrokes.data.allPoints;
    addtoQueue(userStroke, sketchPadCanvas.width, sketchPadCanvas.height);
    console.log("User Stroke Added");
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
    cncserver.api.pen.park(cncserver.cmd.cb);
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

function getData(){
    socket.emit('getData'); 
}

function getData_noSave() {
    //console.log("in getData no save"); 
    socket.emit('getData_noSave'); 
}

function setGroupLabel(label){
    socket.emit('onLabel', label); 
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

function sleep(milliseconds) {
  var start = new Date().getTime();
  for (var i = 0; i < 1e7; i++) {
    if ((new Date().getTime() - start) > milliseconds){
      break;
    }
  }
}


upvoteConfirm = $("#upvoteconfirmation");
downvoteConfirm = $("#downvoteconfirmation");


function DownVote() {
    displayMessage("Sorry you didn't like it. I'll do that less.");

    //socket.emit('vote', 0, totalScore);
	//downvoteConfirm.show("fast").delay( 2000 );
    socket.emit('vote', 0);//add score count to the room, get that with user data

	//downvoteConfirm.style.display = "block";
	//upvoteConfirm.style.display = "none";
	//downvoteConfirm.hide("fast");
}


function UpVote() {
    console.log("upvoted2");
    displayMessage("Glad you liked it! I'll do that more often.");


	//upvoteConfirm.show("fast").delay( 2000 );
	//upvoteConfirm.css("display","block");
	socket.emit('vote', 1);//add score count to the room, get that with user data
	//downvoteConfirm.style.display = "none";
	
	//upvoteConfirm.css("display","none");
	//upvoteConfirm.hide("fast");
	console.log(upvoteConfirm);
	
}

function downloadData() {
    console.log('getting data...');
    socket.emit('getData');
	
}


function downloadCanvas(link) {
    console.log("Working on downloading canvas");
    var canvas = document.getElementById('both');
    var filename = 'test.png'; 
    link.href = document.getElementById('both').toDataURL();
    link.download = filename;
    /*
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
     * */
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
    //var objToDraW = label.selection;
    //var objRecognized = label.classification;
    //displaySpeech(objRecognized, objToDraw); 
    console.log("recognized as: ");
    console.log(label);
    //var newLabel = JSON.parse(label)
    //document.getElementById('label').value = newLabel;
}


function ChooseCreativity(value){
		 	console.log("Setting creativity" + value);
		    switch( value ) {
				   case 1: 
						socket.emit("SetCreativty", 1);
					    break;
				   case 2: 
					    socket.emit("SetCreativty", 50);
					    break;
				   case 3:
				   		socket.emit("SetCreativty", 70);
				   		break;
                case 4:
                    socket.emit("SetCreativty", 95);
                         break;
                case 5:
                    socket.emit("SetCreativty", 100);
                    break;
				   		
				}
}
	    
function addtoQueue(stroke, canvasWidth, canvasHeight) {
    var strokeList = [];
    stroke.forEach(function(point) {

        var percentX = (point.x / canvasWidth) * 50;
        var percentY = (point.y / canvasHeight) * 50;

        //console.log(point.x, point.y, canvasHeight, canvasWidth, percentX, percentY);

        strokeList.push([percentX, percentY]);
    });
    plotterQueue.push(strokeList);
    //console.log("Stroke Added To Buffer");
}