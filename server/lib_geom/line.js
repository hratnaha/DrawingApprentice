"use strict";
var Point = require("./point");

class line{
    constructor(){
        this.lineID = Math.random();        
        this.allPoints = [];
        this.parameter = [];
        this.startTime = 0;
        this.endTime = 0;
        this.groupID = -1;
        this.color = {};
        this.isCompGenerated = false;
        this.isSelected = false;
        this.thickness = 1.0;
        
        this.xmin = Number.MAX_SAFE_INTEGER;
        this.ymin = Number.MAX_SAFE_INTEGER;
        this.xmax = Number.MIN_SAFE_INTEGER;
        this.ymax = Number.MIN_SAFE_INTEGER;
    }
    // add new point in this line
    addPoint(x, y, time){
        if(this.allPoints.length == 0)
            this.startTime = time;
        this.endTime = time;
        
        var stpt = new Point(x, y, this.lineID, time);
        this.allPoints.push(stpt);
                
        if(x < this.xmin)
            this.xmin = x;
        if(x > this.xmax)
            this.xmax = x;
        if(y < this.ymin)
            this.ymin = y;
        if(y > this.ymax)
            this.ymax = y;
    }
    // return a new line from offsetting the current line
    offsetLine(offX, offY){
        var offLine = new line();
        for(var i = 0; i< this.allPoints.length; i++){
            offLine.addPoint(this.allPoints[i].x + offX, this.allPoints[i].y + offY);
        }
        return offLine;
    }
    // test if the point is inside of the bbox from this line
    containsInBBox(x, y){
        if(x >= this.xmin && x <= this.xmax && y >= this.ymin && y <= this.ymax)
			return true;
		return false;
    }
}

module.exports = line;
