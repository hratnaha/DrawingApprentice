/**
  * @author Chih-Pin Hsiao
  * @email: chipin01@gmail.com
*/
"use strict";

var java = require("java"),
    canvas2D = require('../libImage'),
    Quadtree = require('../lib_geom/quadtree'),
    uuid = require('node-uuid'),
    fs = require("fs"),
    generator = require('./linegenerator');

var waitForTurn = 4000;
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
function onReaching4thLevel(strokes){
    console.log(strokes.length);
    
    
}
function pushTurnStroke(stroke){
    if(stroke && stroke.allPoints && stroke.allPoints.length > 0){
        if(!this.bound){
            this.bound = {
                left:   stroke.allPoints[0].x,
                right:  stroke.allPoints[0].x,
                top:    stroke.allPoints[0].y,
                bottom: stroke.allPoints[0].y
            };
        }
        for(var i=0;i<stroke.allPoints.length;i++){
            if(stroke.allPoints[i].x < this.bound.left)
                this.bound.left = stroke.allPoints[i].x;
            if(stroke.allPoints[i].x > this.bound.right)
                this.bound.right = stroke.allPoints[i].x;
            if(stroke.allPoints[i].y > this.bound.bottom)
                this.bound.bottom = stroke.allPoints[i].y;
            if(stroke.allPoints[i].y < this.bound.top)
                this.bound.top = stroke.allPoints[i].y;
        }
        //console.log(this.bound);
    }
    this.push(stroke);
}
function clear(){
    while (this.length) {
        this.pop();
    }
    this.bound = null;
}
var enum_RoomType = {
    solo        : 1,
    human       : 2,
    apprentice  : 3,
    recorded    : 4
};
class gameroom {
    constructor(roomInfo, apprentice, sketchClassfier, lineGenerator){
        this.players = [];
        this.sockets = [];
        this.newGroup = [];
        this.compStrokes = [];
        this.userStrokes = [];
        this.userTurnStrokes = [];
        // overide the push function so that we can update the bounding box of the turn strokes
        this.userTurnStrokes.pushTurnStroke = pushTurnStroke;
        this.userTurnStrokes.clear = clear;
        this.roomInfo = roomInfo ? roomInfo : this.createRoomInfo();
        this.apprentice = apprentice;
        this.apprentice.labeledGroups = []; 
        this.canvasSize = { width: 0, height: 0 };
        this.indexULines = 0;   // for counting the number of user strokes the user has drawn
        this.indexCLines = 0;   // for counting the number of computer strokes the room has drawn
        this.indexRLines = 0;   // for counting the number of recorded strokes used in the program
        this.indexPLines = 0;   // for counting the number strokes processed by apprentice
        this.isGrouping = false;
        this.prevDrawn = 1;
        this.sketchClassfier = sketchClassfier;
        this.lineGenerator = lineGenerator;
        this.numTurnStrokes = 0;
        this.setRoomType(enum_RoomType.apprentice);
        this.prevDrawn = 1;
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
    setRoomType(type){
        this.roomtype = type;
        if(this.roomtype != enum_RoomType.apprentice){
            this.apprentice.setAgentOn(false);
            if(this.roomtype == enum_RoomType.recorded){
                var filepath = this.recordedPath ? 
                    this.recordedPath : __dirname + '/prev_session/1_userLines.json';
                var thisobj = this;
                fs.readFile(filepath, 'utf8', (err, data)=>{
                   if (err) throw err;
                   console.log(err);
                   if(!err){
                        var str = JSON.stringify(data);
                        str = str.replace(/\\/g, "");
                        str = str.slice(2, -1);
                        
                        thisobj.recordedData = JSON.parse(str);
                   }
                });
            }
        }else{
            this.apprentice.setAgentOn(true);
        }
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

    addStroke(userStroke, so) {
    
    //this.players.forEach()
    //match the userID from userStroke.userID to the player IDs
        //
        // for closure variable for the "this" object in the call back
        var thisobj = this;
        this.numTurnStrokes++;
        
        this.userTurnStrokes.pushTurnStroke(userStroke);
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
            var newTurn = []; 
            this.apprentice.Grouping(function (err, result) {
            console.log("Inside the grouping, result is: " + result);
                if (result != null) {
                    for (var i = 0; i < result.sizeSync(); i++) {
                        var newline = result.getSync(i);

                        var newpkpts = [];
                        for (var j = 0; j < newline.sizeSync(); j++) {
                            var newpt = newline.getSync(j);
                            newpkpts.push(CreatePacketPoint(newpt));
                        }
                        newTurn.push(newpkpts); 
                        //userStroke.allPoints = newpkpts;
                        // decode to JSON and send the message
                        //var resultmsg = JSON.stringify(userStroke);
                        //so.emit('respondStroke', resultmsg);
                        //this.apprentice.setModeSync(0);
                    }
                    thisobj.newGroup.push(newTurn);
                    console.log("Length of newGroup = " + thisobj.newGroup.length); 
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
                // For testing the results from the sketch classfier!!
                // create the pic for recognizing using canvas2D
                var tmpWidth = thisobj.userTurnStrokes.bound.right - thisobj.userTurnStrokes.bound.left;
                var tmpHeight = thisobj.userTurnStrokes.bound.bottom - thisobj.userTurnStrokes.bound.top;
                var boundSize = tmpWidth > tmpHeight ? tmpWidth + 200 : tmpHeight + 200;
                var deltaWidth = boundSize - tmpWidth;
                var deltaHeight = boundSize - tmpHeight;
                var turnContext = canvas2D.Initialize(boundSize, boundSize);
                for(var i = 0; i < thisobj.userTurnStrokes.length; i++){
                    canvas2D.DrawLine(turnContext, thisobj.userTurnStrokes[i], {
                        x: thisobj.userTurnStrokes.bound.left - deltaWidth / 2,
                        y: thisobj.userTurnStrokes.bound.top - deltaHeight / 2
                    });
                }
		
		        if(thisobj.creativity > 90){
		            canvas2D.SaveToFile(turnContext, thisobj.roomInfo.id + "-tmp", false, function(filename, err1){
                        if(!err1){
                            // recognize the image using sketchClass
                            if(thisobj.sketchClassfier){
                                thisobj.sketchClassfier.invoke("recognize_Image", filename, function(err2, result) {
                                    // report back to the client
                                    if(!err2){
                                        generator.GetSketchesInCategory(result, thisobj.quadtree, thisobj.canvasSize, function(strokes, err3){
                                            console.log(err3);
					                        if(!err3){
					                            //console.log(strokes);
                                                try{
					                                for(var i=0;i < strokes.length; i++){
                                                        var stroke = strokes[i];
						                                insertLineSegments(thisobj.quadtree, stroke);
                                                        var resultmsg = JSON.stringify(stroke);
                                                        if(thisobj.sockets.length > 0){
						                                    //console.log("prepare to send lines back through sockets: " + resultmsg);
                                                            for(var j=0;j<thisobj.sockets.length;j++){
                                                                var tarso = thisobj.sockets[j];
							                                    tarso.emit('respondStroke', resultmsg);
							                                    console.log("finish sending stroke");
                                                            }
                                                        }else{
						                                    console.log("prepare to send lines through so");
                                                            so.emit('respondStroke', resultmsg);
						                                }
                                                    }
					                            }catch(err4){
					    	                        console.log(err4);
					                            }
                                            }
                                        });
                                        so.emit('classifyObject', result);
                                    }
                                });
                            }
                        }
                    });
		            thisobj.precreativityLevel = 100; // hack for fix the bug
		
                    // reset the user turn strokes
                    thisobj.userTurnStrokes.clear();
                }else{
                    switch(thisobj.roomtype){
                        case enum_RoomType.recorded:
                            if(thisobj.recordedData){
                                for(var i = thisobj.numTurnStrokes; i>0;i--){
                                    var curStroke = thisobj.recordedData[thisobj.indexRLines];
                                    curStroke.color = thisobj.userStrokes[thisobj.indexRLines].color;
                                    curStroke.lineWidth = thisobj.userStrokes[thisobj.indexRLines].lineWidth;
                                    var resultmsg = JSON.stringify(curStroke);
                                    so.emit('respondStroke', resultmsg);
                                    thisobj.indexRLines++;
                                }
                                // update the server pic
                                // thisobj.updateServerPic();
                            }
                        break;
                        case enum_RoomType.apprentice:
                            thisobj.apprentice.getDecision(function (err, results) {
                                if (results != null) {
                                    var resultSize = results.sizeSync();
				                    if(thisobj.precreativityLevel && thisobj.precreativityLevel > 90 )
                                        thisobj.precreativityLevel = 0;
                                    else{
                                        for (var j = 0; j < resultSize; j++) {
                                            // deal with the case when the subject switch between 
                                            // random mode and apprentice mode
                                            var curStIndex = thisobj.indexRLines >= thisobj.userStrokes.length ?
                                                thisobj.userStrokes.length - 1 : thisobj.indexRLines;
                                            // get the stroke attributes from the corresponding user stroke  
                                            var curStroke = thisobj.userStrokes[curStIndex];
                                            var newpkpts = [];
                                            var result = results.getSync(j);
                                            for (var i = 0; i < result.sizeSync(); i++) {
                                                var newpt = result.getSync(i);
                                                newpkpts.push(CreatePacketPoint(newpt));
                                            }
                                            var compStroke = JSON.parse(JSON.stringify(curStroke));
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
                                            thisobj.indexRLines++;
                                        }
				                    }
                                }
                            });
                        break;
                    }
		        }
                thisobj.numTurnStrokes = 0; // clean to 0;
            }, waitForTurn);
        }
        
        for(var i=0;i<this.sockets.length;i++){
            var tarso = this.sockets[i];
            if(tarso != so)
                tarso.emit('respondStroke', JSON.stringify(userStroke));
        }
    }
    updateServerPic(){
        // update the server pic /*every 10 user lines*/
        if(this.userStrokes.length > 0 && this.userStrokes.length / 10 > this.prevDrawn){
            this.prevDrawn = Math.ceil(this.userStrokes.length / 10);
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
                // draw the thumbnail
                if(!err){
		            console.log("saving thumbnail filename: " + filename);
                    canvas2D.isUsingGM = true;
                    var thumbContext = canvas2D.InitializeFromFile(filename).resize(80,60);
                    canvas2D.SaveToFile(thumbContext, id, true);
                    canvas2D.isUsingGM = false;
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
        this.quadtree = new Quadtree(bound, 50, 5, 0, this, onReaching4thLevel);
    }
    onModeChanged(m) {
        if (m == 3)
            this.isGrouping = true;
        else if (m == 4)
            this.isGrouping = false;
        else
            this.apprentice.setModeSync(m);
    }
    setCreativity(level){
	console.log("set creativity level:" + level);
    	this.apprentice.setCreativityLevel(level);
	this.creativity = level;
	/*if(level == 100)
		generator.mode = 2;
	else
		generator.mode = 1;*/
    }
}
module.exports = gameroom;
