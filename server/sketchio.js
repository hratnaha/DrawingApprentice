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
    curRooms = {},
    roomsInfo = [],
    onlineUsers = {};

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

app.get('/room/create', function (req, res) {
    res.json(roomsInfo);
});

app.post('/room/create', function (req, res) {
    var roomInfo = req.body;
    var newRoomInfo = {};
    newRoomInfo.name = roomInfo.name;
    newRoomInfo.fullpic = '';
    newRoomInfo.id = uuid.v4();
    newRoomInfo.host = "chipin01"; // hard-coded for now;
    newRoomInfo.players = [];
    canvas2D.CreateBlankThumb(newRoomInfo.id);
    newRoomInfo.thumb = '/session_pic/' + newRoomInfo.id + '_thumb.png';
    res.json(newRoomInfo);
    roomsInfo.push(newRoomInfo);
    
    var apprentice = new Apprentice();
    apprentice.setCurrentTime((new Date()).getTime());
    
    var newRoom = {};
    newRoom.broadcast = function(evtname,msg){
        for(var i=0;i<this.so.length;i++){
            var tarso = this.so[i];
            tarso.emit(evtname, msg);
        }
    };
    newRoom.players = [];
    newRoom.so = [];
    newRoom.apprentice = apprentice;
    curRooms[newRoomInfo.id] = newRoom;
});

app.post('/room/join', function (req, res) {
    // 1. check if the room exists
    var msg = req.body;
    var room = curRooms[msg.id];
    var roomInfo = getRoom(msg.id);
    // 2. if yes, then add a player inside of the room.players attributes
    //   so that the room knows there is a new player joining in.
    if (room && roomInfo && onlineUsers[msg.newPlayer.id]) {
        var newPlayer = onlineUsers[msg.newPlayer.id];
        roomInfo.players.push(msg.newPlayer.id);
        newPlayer.curRoom = roomInfo.id;
        room.players.push(newPlayer);
        // 3. tell the client to redirect to app page
        var rmsg = {isSucceed: true};
        res.json(rmsg);
    }
});

// ensure authentication
function ensureAuthenticated(req, res, next) {
    if (req.isAuthenticated()) { return next(); }
    res.redirect('/');
}
function authenticationSucceed(req, res){
    onlineUsers[req.user.id] = req.user;
    res.redirect('/admin_room');//res.redirect('/app');
}
// if the user pass thorugh authentication, render the app
app.get('/app', ensureAuthenticated, function (req, res) {
    var user = onlineUsers[req.user.id];
    var roomID = '';
    if(user){
        roomID = user.curRoom;
    }
    // need to tell the client to load the existing jpg
    res.render('app', { user: req.user._raw, sessionId: req.sessionID, roomId: roomID});
});
app.get('/admin_room', ensureAuthenticated, function (req, res) {
    res.render('admin_room', { user: req.user._raw });
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
    passport.authenticate('google', { failureRedirect: '/login' }),
    authenticationSucceed
);
// when log-out
app.get('/logout', function (req, res) {
    req.logout();
    res.redirect('/');
});
// Start to listen the app
app.listen(3000);
//===================== Finished Express App Set Up ===================\\

//===================== Set up socket io server =====================\\
var server = http.Server(app);
var io = require('socket.io')(server);
var isGrouping = false;
server.listen(8080);
//server.listen(81); // for adam server

io.on('connection', function (so) {
    // set up closure varialbes
    var utilDatabase = require('./libDatabase');
    var apprentice;
    var room;
    var timeout;
    var userProfile;
    var sessionID;
    var canvasSize = { width: 0, height: 0 };
    var totalScore = 0;
    
    console.log("new client connected");

    so.emit('newconnection', { hello: "world" });

    function onOpen(hello) {
        if (hello) {
            if(onlineUsers[hello.user.id]){
                var thisPlayer = onlineUsers[hello.user.id];
                room = curRooms[thisPlayer.curRoom];
                room.so.push(so);
            }
            apprentice = room ? room.apprentice : new Apprentice();
            canvasSize.width = hello.width;
            canvasSize.height = hello.height;
            apprentice.setCanvasSize(hello.width, hello.height);
            userProfile = hello.user;
            sessionID = thisPlayer.curRoom;
            
            utilDatabase.initializeParameters(userProfile, sessionID, apprentice, canvasSize);
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
            var allLines = {
                userLines: userLines,
                computerLines: computerLines
            };

            so.emit('allData', allLines);
        }
    }

    function onNewStrokeReceived(data) {
        var d = JSON.parse(data);

        var stroke = d.data;

        var stroketime = java.newLong(stroke.timestamp);
        // categorize if it is grouping or not
        if (isGrouping)
            apprentice.startGroupingSync(stroketime);
        else
            apprentice.createStrokeSync(stroketime);

        var pts = stroke.packetPoints;

        // adding all the points in the stroke
        for (var i = 0; i < pts.length; i++) {
            var pt = pts[i];
            apprentice.addPointSync(parseInt(pt.x, 10), parseInt(pt.y, 10), pt.timestamp, pt.id);
        }
        // Todo: reconstruct the message to send out

        if (isGrouping) {
            apprentice.Grouping(function (err, result) {
                if (result != null) {
                    for (var i = 0; i < result.sizeSync(); i++) {
                        var newline = result.getSync(i);

                        var newpkpts = [];
                        for (var j = 0; j < newline.sizeSync(); j++) {
                            var newpt = newline.getSync(j);

                            newpkpts.push(CreatePacketPoint(newpt));
                        }
                        stroke.packetPoints = newpkpts;

                        // decode to JSON and send the message
                        var resultmsg = JSON.stringify(stroke);
                        so.emit('respondStroke', resultmsg);
                        apprentice.setModeSync(0);
                    }
                }
            });
        } else {
            var r = java.newFloat(parseFloat(stroke.color.r));
            var g = java.newFloat(parseFloat(stroke.color.g));
            var b = java.newFloat(parseFloat(stroke.color.b));
            var a = java.newFloat(parseFloat(stroke.color.a));
            var thickness = java.newFloat(parseFloat(stroke.lineWidth));

            apprentice.endStroke(r, g, b, a, thickness);

            if (timeout != "" || timeout != null) {
                clearTimeout(timeout);
            }

            timeout = setTimeout(function () {
                apprentice.getDecision(function (err, results) {
                    if (results != null) {
                        for (var j = 0; j < results.sizeSync(); j++) {
                            var newpkpts = [];
                            var result = results.getSync(j);
                            for (var i = 0; i < result.sizeSync(); i++) {
                                var newpt = result.getSync(i);

                                newpkpts.push(CreatePacketPoint(newpt));
                            }
                            stroke.packetPoints = newpkpts;

                            // decode to JSON and send the message
                            var resultmsg = JSON.stringify(stroke);
                            if(room)
                                room.broadcast('respondStroke', resultmsg);
                        }
                        //console.log("sending: " + resultmsg);
                    }
                });
            }, 2000);
        }
        for(var i=0;i<room.so.length;i++){
            var tarso = room.so[i];
            if(tarso != so)
                tarso.emit('respondStroke', JSON.stringify(d.data));
        }
    }

    function vote(isUp, score) {
        var vote = isUp ? 1 : 0;
        if (isUp)
            totalScore += 10;
        else
            totalScore -= 10; 
        //totalScore = isUp ? (totalScore + 10) : (totalScore - 10); 
        if(room)
            room.broadcast('updateScore', JSON.stringify(totalScore));

        apprentice.voteSync(vote);
    }

    function onClear() {
        apprentice.clearSync();
    }

    function onModeChanged(mode) {
        var m = JSON.parse(mode);
        if (m == 3)
            isGrouping = true;
        else if (m == 4)
            isGrouping = false;
        else
            apprentice.setModeSync(m);
    }

    function classifyObject(objectLabel) {
        var label = JSON.stringify(objectLabel)
        so.emit('classifyObject', label)
    }

    so.on('onOpen', onOpen);
    so.on('SetCreativty', function (level) {
        var d = JSON.parse(level);
        apprentice.setCreativityLevel(d);
    });
    so.on('setAgentOn', function (ison) {
        var isOnBool = JSON.parse(ison);
        apprentice.setAgentOn(!isOnBool);
    });
    so.on('getData', getData);
    so.on('touchdown', function () {
        if (timeout != "" || timeout != null) {
            clearTimeout(timeout);
        }
    });
    so.on('disconnect', utilDatabase.onSaveDataOnDb);
    so.on('touchup', onNewStrokeReceived);
    so.on('setMode', onModeChanged);
    so.on('clear', onClear);
    so.on('submit', submitResult);
    so.on('vote', vote);
});
//===================== Finished Socket io server Set Up ====================\\

//===================== Utility Functions ====================\\

function getRoom(roomID) {
    for (var i = 0; i < roomsInfo.length; i++) {
        var existingRoom = roomsInfo[i];
        if (existingRoom.id == roomID)
            return existingRoom;
    }
}

function submitResult(d) {
    submit(d, function (error, result) {
        console.log(result);
    });
}

function CreatePacketPoint(newpt) {
    // construct a new packet point
    var pkpt = {
        id: newpt.id,
        x: newpt.x,
        y: newpt.y,
        timestamp: newpt.timestamp,
        pressure: 0          // to be implemented when the pressure is available
    };

    return pkpt;
}

console.log("SocketIO Server Initialized!");
