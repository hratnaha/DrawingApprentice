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

var Apprentice = java.import('jcocosketch.nodebridge.Apprentice');
var apprentice = new Apprentice();

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

server.listen(8080);

io.on('connection', function (so) {
    console.log("new client connected");
    so.emit('newconnection', { hello: 'world' });
    so.on('newStroke', function newStrokeReceived(data) {
        var d = JSON.parse(data);
        
        var stroke = d.data;
        
        var stroketime = java.newLong(stroke.timestamp);
        // categorize if it is grouping or not
        if (d.isGrouping)
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
        apprentice.decision(function (err, result) {
            if (result != null) {
                var newpkpts = [];
                for (var i = 0; i < result.sizeSync(); i++) {
                    var newpt = result.getSync(i);
                    
                    newpkpts.push(CreatePacketPoint(newpt));
                }
                stroke.packetPoints = newpkpts;
                
                // decode to JSON and send the message
                var resultmsg = JSON.stringify(stroke);
                io.emit('respondStroke', resultmsg);
            //console.log("sending: " + resultmsg);
            }
        });
        
        so.broadcast.emit('respondStroke', JSON.stringify(d.data));
    });
    so.on('setMode', onModeChanged);
    so.on('clear', onClear);
    so.on('submit', submitResult);
});

function onClear() {
    apprentice.clearSync();
}

function onModeChanged(mode) {
    var m = JSON.parse(mode);
    apprentice.setModeSync(m);
}



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
