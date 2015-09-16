/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";
process.title = 'sketch-server';
var oneDay = 86400000;
var ss = require("./Sketch");

var java = require("java");

java.classpath.push("commons-lang3-3.1.jar");
java.classpath.push("commons-io.jar");
java.classpath.push("commons-math3-3.3.jar");
java.classpath.push("apprentice.jar");      // apprentice library
java.classpath.push("core.jar");            // processing
java.classpath.push("flexjson.jar");
java.classpath.push("ABAGAIL.jar");

var Apprentice = java.import('jcocosketch.nodebridge.Apprentice');

// set up express
var express = require('express'),
    app = express();
// Use compress middleware to gzip content
app.use(express.compress());
// Serve up content from public directory
app.use(express.static(__dirname + '/public', { maxAge: oneDay }));
app.listen(process.env.PORT || 3000);

var server = require('http').Server(app);
var io = require('socket.io')(server);
var isGrouping = false;
//server.listen(8080);
server.listen(81); // for adam server

var timeout;

io.on('connection', function (so) {
    var apprentice = new Apprentice();

    so.on('canvasSize', function setSize(size) {
        //var d = JSON.parse(size);
        apprentice.setCanvasSize(size.width, size.height);
    });

    console.log("new client connected");
    so.emit('newconnection', { hello: 'world' });
    
    function getData(data) {
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
            }
            
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
            apprentice.addNewStrokeSync(stroketime);
        
        var pts = stroke.packetPoints;
        var returnSt = [];
        
        // adding all the points in the stroke
        for (var i = 0; i < pts.length; i++) {
            var pt = pts[i];
            var pttime = java.newLong(pt.timestamp);
            apprentice.addPointSync(parseInt(pt.x, 10), parseInt(pt.y, 10), pttime, pt.id);
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
                        io.emit('respondStroke', resultmsg);
                        apprentice.setModeSync(0);
                    }
                }
            });
        } else {
            apprentice.addLine();
            
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
                            io.emit('respondStroke', resultmsg);
                        }
                   //console.log("sending: " + resultmsg);
                    }
                });
            }, 2000);
        }
        so.broadcast.emit('respondStroke', JSON.stringify(d.data));
    }

    function vote(isUp) {
        var vote = isUp ? 1 : 0;
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
    so.on('touchup', onNewStrokeReceived);
    so.on('setMode', onModeChanged);
    so.on('clear', onClear);
    so.on('submit', submitResult);
    so.on('vote', vote);
});

function submitResult(d) {
    submit(d, function (error, result) {
        console.log(result);
    });
}

function CreatePacketPoint(newpt) {
    // construct a new packet point
    var pkpt = {
        id : newpt.id,
        x : newpt.x,
        y : newpt.y,
        timestamp : newpt.timestamp,
        pressure : 0          // to be implemented when the pressure is available
    };

    return pkpt;
}

console.log("SocketIO Server Initialized!");
