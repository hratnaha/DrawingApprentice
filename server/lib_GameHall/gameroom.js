/**
  * @author Chih-Pin Hsiao
  * @email: chipin01@gmail.com
*/
"use strict";

var canvas2D = require('../libImage');

class gameroom {
    constructor(roomInfo, apprentice){
        this.players = [];
        this.sockets = [];
        this.compStrokes = [];
        this.userStrokes = [];
        this.roomInfo = roomInfo;
        this.apprentice = apprentice;
        this.canvasSize = { width: 0, height: 0 };
        this.indexULines = 0;
        this.indexCLines = 0;
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
    }
}
module.exports = gameroom;