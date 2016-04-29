/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
//"use strict";
process.title = 'sketch-server';
var oneDay = 86400000;
var java = require("java");

// load java modules
java.classpath.push("./jars/commons-lang3-3.1.jar");
java.classpath.push("./jars/commons-io.jar");
java.classpath.push("./jars/commons-math3-3.3.jar");
java.classpath.push("./jars/apprentice.jar");      // apprentice library
java.classpath.push("./jars/core.jar");            // processing
java.classpath.push("./jars/flexjson.jar");
java.classpath.push("./jars/ABAGAIL.jar");

// Start the Drawing Apprentice server
var Apprentice = java.import('jcocosketch.nodebridge.Apprentice');

// initialize required module
var express = require('express'),
    passport = require('passport'),
    util = require('util'),
    session = require('express-session'),
    cookieParser = require('cookie-parser'),
    bodyParser = require('body-parser'),
    strategies = require('./strategies'),
    http = require('http'),
    app = express(),
    canvas2D = require('./libImage'),
    uuid = require('node-uuid'),
    zerorpc,
    curRooms = {},
    roomsInfo = [],
    onlineUsers = {},
    Room = require('./lib_GameHall/gameroom');;

var zerorpc;
try {
    zerorpc = require("zerorpc");
} catch (err) {
    console.error(err);
}

var options = {timeout:600000};

// setting up the local 
var sketchClassfier, lineGenerator;
if (zerorpc) {
    sketchClassfier = new zerorpc.Client(options);
    sketchClassfier.connect("tcp://127.0.0.1:4242");
    
    // lineGenerator = new zerorpc.Client(options);
    // lineGenerator.connect("tcp://127.0.0.1:4243");
}
// Passport session setup.
passport.serializeUser(function (user, done) {
    done(null, user);
});
passport.deserializeUser(function (obj, done) {
    done(null, obj);
});

// Use various strategies.
passport.use(strategies.Facebook);
passport.use(strategies.Google);

//=====================Set Up Express App=====================\\
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(cookieParser());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(session({ secret: 'keyboard cat', key: 'sid' }));
app.use(passport.initialize());
app.use(passport.session());
app.use(express.static(__dirname + '/public'));
app.use('/session_pic', express.static(__dirname + '/session_pic'));

// log-in page for now
app.get('/', function (req, res) {
    res.render('index', { user: req });
});

app.get('/admin_room/create', function (req, res) {
    res.json(roomsInfo);
});

app.post('/admin_room/create', function (req, res) {
    var roomInfo = req.body;
    var newRoomInfo = {};
    newRoomInfo.name = roomInfo.name;
    newRoomInfo.fullpic = '';
    newRoomInfo.id = uuid.v4();
    newRoomInfo.host = "chipin01"; // hard-coded for now;
    newRoomInfo.players = [];
    canvas2D.CreateBlankThumb(newRoomInfo.id);
    newRoomInfo.thumb = '../session_pic/' + newRoomInfo.id + '_thumb.png';
    res.json(newRoomInfo);
    roomsInfo.push(newRoomInfo);
    // initialize apprentice
    var apprentice = new Apprentice();
    apprentice.setCurrentTime((new Date()).getTime());
    // create a room
    var room = new Room(newRoomInfo, apprentice, sketchClassfier, lineGenerator);
    
    curRooms[newRoomInfo.id] = room;
});

app.post('/admin_room/join', function (req, res) {
    // 1. check if the room exists
    var msg = req.body;
    var room = curRooms[msg.id]; 
    var roomInfo = room ? room.roomInfo : null;

	function addIfPlayerExists(id, newPlayer){
		var isExist = false;
		for(var i=0;i<roomInfo.players.length;i++){
			if(roomInfo.players == id){
				isExist = true;
				break;
			}
		}
		if(!isExist){
			roomInfo.players.push(msg.newPlayer.id);
		}
		newPlayer.curRoom = roomInfo.id;
		room.players.push(newPlayer);
	}

    // 2. if yes, then add a player inside of the room.players attributes
    //   so that the room knows there is a new player joining in.
    if (room && roomInfo && onlineUsers[msg.newPlayer.id]) {
        console.log("player: " + msg.newPlayer.id + " is joining the room");
        var newPlayer = onlineUsers[msg.newPlayer.id];
        addIfPlayerExists(msg.newPlayer.id, newPlayer);
        // 3. tell the client to redirect to app page
        var rmsg = {isSucceed: true};
        res.json(rmsg);
    }
});
Array.prototype.remove = function(index){
  this.splice(index,1);
}
app.post('/admin_room/delete', function (req, res){
    var msg = req.body;
    var rmsg = {isSucceed: false};
    console.log(msg.requester);
    if(curRooms[msg.id] && msg.requester == "105775598272793470839"){	        
	delete curRooms[msg.id];
	for(var i=0;i<roomsInfo.length;i++){
	    if(roomsInfo[i].id == msg.id){
	    	roomsInfo.remove(i);
		break;
	    }	
	}
    }
    res.json(rmsg);
});

// ensure authentication
function ensureAuthenticated(req, res, next) {
    if (req.isAuthenticated()) { return next(); }
    res.redirect('/DrawingApprentice/');
}
function authenticationSucceed(req, res){
    console.log("user " + req.user.id + " logged in");
    onlineUsers[req.user.id] = req.user;
    res.redirect('/admin_room/');//res.redirect('/app');
}
// if the user pass thorugh authentication, render the app
app.get('/app', ensureAuthenticated, function (req, res) {
    console.log("client start accessing app resources");
    var user = onlineUsers[req.user.id];
    var roomID = '';
    if(user){
        roomID = user.curRoom;
    }
    // need to tell the client to load the existing jpg
    res.render('app', { user: req.user._raw, sessionId: req.sessionID, roomId: roomID});
});
app.get('/admin_room', ensureAuthenticated, function (req, res) {
    res.render('admin_room', { user: req.user._raw, userid: req.user.id });
});

// facebook authentication
app.get('/auth/facebook', passport.authenticate('facebook', { scope: 'email' }));
app.get('/auth/facebook/callback',
    passport.authenticate('facebook', { failureRedirect: '/' }),
    authenticationSucceed
);
// google authentication
app.get('/auth/google', passport.authenticate('google', { scope: 'https://www.googleapis.com/auth/plus.login' }));
app.get('/auth/google/callback',
    passport.authenticate('google', { failureRedirect: '/' }),
    authenticationSucceed
);
// when log-out
app.get('/logout', function (req, res) {
    req.logout();
    res.redirect('/DrawingApprentice/');
});
// Start to listen the app
app.listen(3000);
//===================== Finished Express App Set Up ===================\\

//===================== Set up socket io server =====================\\
var server = http.Server(app);
var io = require('socket.io')(server);
server.listen(8080); // for local debug
//server.listen(81); // for adam server

io.on('connection', function (so) {
    // set up closure varialbes
    var utilDatabase = require('./libDatabase');
    var apprentice;
    var room; //this is the game room, grab the data from here rather than apprentice 
    var userProfile;
    var sessionID;
    var totalScore = 0;
    
    console.log("new client connected");
    
    so.emit('newconnection', { hello: "world" });
    
    function onOpen(hello) {
        if (hello) {
            var thisPlayer;
            if (onlineUsers[hello.user.id]) {
                thisPlayer = onlineUsers[hello.user.id];
                room = curRooms[thisPlayer.curRoom];
                room.sockets.push(so);
            }
            
            if (!room) {
                apprentice = new Apprentice();
                room = new Room(null, apprentice, sketchClassfier);
            }
            else
                apprentice = room.apprentice;
            
            room.setCanvasSize(hello.width, hello.height);
            userProfile = hello.user;
            sessionID = thisPlayer ? thisPlayer.curRoom : uuid.v4();
            
            utilDatabase.initializeParameters(userProfile, sessionID, apprentice, room.canvasSize);
        }
    }
    
    function getData() {
        var userLines;
        var computerLines;
        
        apprentice.getUserLines(function (err, item) {
            if (err) {
                console.log(err);

            } else {
                userLines = item;
                afterUserLines();
            }
        });
        
        function afterUserLines() {
            apprentice.getComputerLines(function (err, item) {
                if (err) {
                    console.log(err);
                } else {
                    computerLines = item;
                    emitData();
                }
            });
        }
        
        function emitData() {
            var allData = {
                userLines: userLines,
                computerLines: computerLines, 
                labeledGroups: apprentice.labeledGroups
            };
            
            so.emit('allData', allData);
        }
    }
    
    function getData_noSave() {
        //console.log("In getData_before");
        
        //instead of calling apprentice, grab data from room; 
        //obj rec data is stored in the room, not apprentice 
        //room.compStrokes
        //!need array of recognizedObjects
        
        var userLines;
        var computerLines;
        
        apprentice.getUserLines(function (err, item) {
            if (err) {
                console.log(err);

            } else {
                userLines = item;
                afterUserLines();
            }
        });
        
        function afterUserLines() {
            apprentice.getComputerLines(function (err, item) {
                if (err) {
                    console.log(err);
                } else {
                    computerLines = item;
                    emitData();
                }
            });
        }
        
        function emitData() {
            var allData = {
                userLines: userLines,
                computerLines: computerLines, 
                labeledGroups: apprentice.labeledGroups 
            };
            
            so.emit('statsData', allData);
        }
}

    function onNewStrokeReceived(data) {
        var d = JSON.parse(data);

        var stroke = d.data;
        room.addStroke(stroke, so);
    }


    function vote(isUp) {
        //var value = JSON.parse(isUP); 
        console.log("In server. Votin up. Room = ");
        if (isUp==1) {
            if (room.upVoteCount == null)
                room.upVoteCount = 1;
            else
                room.upVoteCount = room.upVoteCount + 1; 
        }
        else {
            if (room.downVoteCount == null)
                room.downVoteCount = 1;
            else
                room.downVoteCount = room.downVoteCount + 1; 
        }
        /*
        var vote = isUp ? 1 : 0;
        if (isUp)
            totalScore += 10;
        else
            totalScore -= 10; 
        //totalScore = isUp ? (totalScore + 10) : (totalScore - 10); 
        if(room)
            room.broadcast('updateScore', JSON.stringify(totalScore));
        */
        apprentice.voteSync(isUp);
    }

    function onClear() {
        apprentice.clearSync();
    }

    function onSetMode(mode){
        var m = JSON.parse(mode);
        if(room){
            room.onModeChanged(m);
        }
    }
    
    function onLabel(label){
        console.log("onLabel " + label); 
        var labeledGroup = {
            label : label,
            //get lines in the newGroup waiting area
            lines: room.newGroup.pop()
            //is there only one new entry? problem point//also if two users group at same time, this is an problematic approach
        };
        apprentice.labeledGroups.push(labeledGroup);
         

    }
    
    so.on('onLabel', onLabel); 
    so.on('onOpen', onOpen);
    so.on('SetCreativty', function (level) {
        var d = JSON.parse(level);
	if(room)
	    room.setCreativity(d);
	else
            apprentice.setCreativityLevel(d);
    });
    so.on('setRoomType', function (type) {
        var t = JSON.parse(type);
        if(room){
            room.setRoomType(t);
        }
    });
    so.on('getData', getData);
    so.on('touchdown', function () {
       room.resetTimeout();
    });
    so.on('disconnect', utilDatabase.onSaveDataOnDb);
    so.on('touchup', onNewStrokeReceived);
    so.on('setMode', onSetMode);
    so.on('clear', onClear);
    so.on('submit', submitResult);
    so.on('vote', vote);
    so.on('getData_noSave', getData_noSave);
});
//===================== Finished Socket io server Set Up ====================\\

//===================== Utility Functions ====================\\

function submitResult(d) {
    submit(d, function (error, result) {
        console.log(result);
    });
}

console.log("SocketIO Server Initialized!");
