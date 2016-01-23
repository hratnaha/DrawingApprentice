/**
  * @author Chih-Pin Hsiao
  * @email: chipin01@gmail.com
*/
"use strict";

var canvas2D = require('../libImage'),
    Quadtree = require('../lib_geom/quadtree'),
    uuid = require('node-uuid');

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
    addStroke(userline, computerline){
        this.userStrokes.push(userline);
        this.compStrokes.push(computerline);
        
        // add the user line and the computer line into the quadtree
        
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
}
module.exports = gameroom;