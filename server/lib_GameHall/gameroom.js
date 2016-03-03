/**
  * @author Chih-Pin Hsiao
  * @email: chipin01@gmail.com
*/
"use strict";

var java = require("java"),
    canvas2D = require('../libImage'),
    Quadtree = require('../lib_geom/quadtree'),
    uuid = require('node-uuid');

function insertLineSegments(quadtree, stroke){
    if(quadtree && stroke.allPoints){
        for(var ptID = 0 ; ptID < stroke.allPoints.length-1; ptID++){
            var pt1 = stroke.allPoints[ptID];
            var pt2 = stroke.allPoints[ptID + 1];
            var left = pt1.x < pt2.x ? pt1.x : pt2.x;
            var top = pt1.y < pt2.y ? pt1.y : pt2.y;
            var bwidth = Math.abs(pt1.x - pt2.x);
            var bheight = Math.abs(pt1.y - pt2.y);
            var bound = {x: left, y: top, width: bwidth, height: bheight, fromline: stroke};
            quadtree.insert(bound);
        }
    }
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
class gameroom {
    constructor(roomInfo, apprentice){
        this.players = [];
        this.sockets = [];
        this.compStrokes = [];
        this.userStrokes = [];
        this.roomInfo = roomInfo ? roomInfo : this.createRoomInfo();
        this.apprentice = apprentice;
        this.canvasSize = { width: 0, height: 0 };
        this.indexULines = 0;
        this.indexCLines = 0;
        this.isGrouping = false;
    }
    createRoomInfo(){
        var roomInfo = {};
        roomInfo.name = "";
        roomInfo.fullpic = '';
        roomInfo.id = uuid.v4();
        roomInfo.host = "chipin01"; // hard-coded for now;
        roomInfo.players = [];
        canvas2D.CreateBlankThumb(roomInfo.id);
        roomInfo.thumb = '/session_pic/' + roomInfo.id + '_thumb.png';
        return roomInfo;
    }
    broadcast(evtname,msg){
        for(var i=0;i<this.sockets.length;i++){
            var tarso = this.sockets[i];
            tarso.emit(evtname, msg);
        }
    }
    resetTimeout(){
        if (this.timeout != "" || this.timeout != null) {
            clearTimeout(this.timeout);
        }
    }
    addStroke(userStroke, so){
        // for closure variable
        var thisobj = this;
        
        this.userStrokes.push(userStroke);
        insertLineSegments(this.quadtree, userStroke);
        
        var stroketime = java.newLong(userStroke.timestamp);
        // categorize if it is grouping or not
        if (this.isGrouping)
            this.apprentice.startGroupingSync(stroketime);
        else
            this.apprentice.createStrokeSync(stroketime);

        var pts = userStroke.allPoints;

        // adding all the points in the stroke
        for (var i = 0; i < pts.length; i++) {
            var pt = pts[i];
            this.apprentice.addPointSync(parseInt(pt.x, 10), parseInt(pt.y, 10), pt.timestamp, pt.id);
        }
        // Todo: reconstruct the message to send out

        if (this.isGrouping) {
            this.apprentice.Grouping(function (err, result) {
                if (result != null) {
                    for (var i = 0; i < result.sizeSync(); i++) {
                        var newline = result.getSync(i);

                        var newpkpts = [];
                        for (var j = 0; j < newline.sizeSync(); j++) {
                            var newpt = newline.getSync(j);

                            newpkpts.push(CreatePacketPoint(newpt));
                        }
                        userStroke.allPoints = newpkpts;

                        // decode to JSON and send the message
                        var resultmsg = JSON.stringify(userStroke);
                        so.emit('respondStroke', resultmsg);
                        this.apprentice.setModeSync(0);
                    }
                }
            });
        } else {
            var r = java.newFloat(parseFloat(userStroke.color.r));
            var g = java.newFloat(parseFloat(userStroke.color.g));
            var b = java.newFloat(parseFloat(userStroke.color.b));
            var a = java.newFloat(parseFloat(userStroke.color.a));
            var thickness = java.newFloat(parseFloat(userStroke.lineWidth));

            this.apprentice.endStroke(r, g, b, a, thickness);

            if (this.timeout != "" || this.timeout != null) {
                clearTimeout(this.timeout);
            }

            this.timeout = setTimeout(function () {
                thisobj.apprentice.getDecision(function (err, results) {
                    if (results != null) {
                        for (var j = 0; j < results.sizeSync(); j++) {
                            var newpkpts = [];
                            var result = results.getSync(j);
                            for (var i = 0; i < result.sizeSync(); i++) {
                                var newpt = result.getSync(i);

                                newpkpts.push(CreatePacketPoint(newpt));
                            }
                            var compStroke = JSON.parse(JSON.stringify(userStroke));
                            compStroke.allPoints = newpkpts;
                            compStroke.time = (new Date()).getTime();
                            
                            // decode to JSON and send the message
                            var resultmsg = JSON.stringify(compStroke);
                            // and send it to all the players
                            if(thisobj.sockets.length > 0){
                                for(var i=0;i<thisobj.sockets.length;i++){
                                    var tarso = thisobj.sockets[i];
                                    tarso.emit('respondStroke', resultmsg);
                                }
                            }else
                                so.emit('respondStroke', resultmsg);
                            
                            thisobj.compStrokes.push(compStroke);
                            insertLineSegments(thisobj.quadtree, compStroke);
                            
                            thisobj.updateServerPic();
                        }
                    }
                });
            }, 2000);
        }
        for(var i=0;i<this.sockets.length;i++){
            var tarso = this.sockets[i];
            if(tarso != so)
                tarso.emit('respondStroke', JSON.stringify(userStroke));
        }
        
        
    }
    updateServerPic(){
        // update the server pic every 10 user lines
        if(this.userStrokes.length > 0 && this.userStrokes.length % 10 == 0){
            // draw both user lines and computer lines
            var drawingContext = canvas2D.Initialize(this.canvasSize.width, this.canvasSize.height);
            while (this.userStrokes.length > this.indexULines && this.compStrokes.length > this.indexCLines) {
                if(this.userStrokes[this.indexULines].timestamp < this.compStrokes[this.indexCLines].timestamp){
                    canvas2D.DrawLine(drawingContext, this.userStrokes[this.indexULines]);
                    this.indexULines++;
                }else{
                    canvas2D.DrawLine(drawingContext, this.compStrokes[this.indexCLines]);
                    this.indexCLines++;
                }
            }
            this.indexCLines = 0;
            this.indexULines = 0;
            var id = this.roomInfo.id; 
            canvas2D.SaveToFile(drawingContext, id, false, function(filename, err){
                if(!err){
                    var thumbContext = canvas2D.InitializeFromFile(filename).resize(80,60);
                    canvas2D.SaveToFile(thumbContext, id, true);
                }
            });
        }
    }
    setCanvasSize(width, height){
        // expand the canvas size if necessary
        this.canvasSize.width = Math.max(width, this.canvasSize.width);
        this.canvasSize.height = Math.max(height, this.canvasSize.height);
        this.apprentice.setCanvasSize(this.canvasSize.width, this.canvasSize.height);
        var bound = {x: 0, y: 0, width: this.canvasSize.width, height: this.canvasSize.height};
        this.quadtree = new Quadtree(bound, 10, 5);
    }
    onModeChanged(m) {
        
        if (m == 3)
            this.isGrouping = true;
        else if (m == 4)
            this.isGrouping = false;
        else
            this.apprentice.setModeSync(m);
    }
}
module.exports = gameroom;