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
java.classpath.push("apprentice.jar");      // test library
java.classpath.push("core.jar");        // processing

var ShiftPt = java.import('jcocosketch.nodebridge.shiftpts');

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

io.on('connection', function (socket) {
  console.log("new client connected");
  socket.emit('newconnection', { hello: 'world' });
  socket.on('newStroke', newStrokeReceived);
  socket.on('submit', submitResult);
});

function newStrokeReceived(data){
  var d = JSON.parse(data);

  var stroke = d.data;
  // shift the packet points in the data
  var stroketime = java.newLong(stroke.timestamp);
  var shift = new ShiftPt(stroketime);
  var pts = stroke.packetPoints;
  for(var i=0;i<pts.length;i++){
    var pt = pts[i];
    var pttime = java.newLong(pt.timestamp);
    shift.addPointSync(pt.x, pt.y, pttime, pt.id);
  }
  var returnSt = [];
  // Todo: reconstruct the message to send out
  shift.shiftTen(function(err, result){
    var newpkpts = [];
    for(var i=0;i<result.sizeSync();i++){
      var newpt = result.getSync(i);

      newpkpts.push(CreatePacketPoint(newpt));
    }
    stroke.packetPoints = newpkpts;

    // decode to JSON and send the message
    var resultmsg = JSON.stringify(stroke);
    io.emit('respondStroke', resultmsg);
    console.log("sending: " + resultmsg);
  });
}

function submitResult(d){
  submit(d, function(error, result){
    console.log(result);
  });
}

function CreatePacketPoint(newpt){
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
