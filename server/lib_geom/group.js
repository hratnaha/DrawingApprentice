"use strict";
class group {
    constructor(groupID){
        this.lines = [];
        this.groupID = groupID ? groupID : Math.random();
        this.xmin = Number.MAX_SAFE_INTEGER;
        this.ymin = Number.MAX_SAFE_INTEGER;
        this.xmax = Number.MIN_SAFE_INTEGER;
        this.ymax = Number.MIN_SAFE_INTEGER;
    }
    addLines(newlines) {
        for(var i=0;i<newlines.length;i++){
            this.addLine(newlines[i]);
        }
    }
    addLine(newline){
        this.lines.push(newline);
        if(this.xmin > newline.xmin)
            this.xmin = newline.xmin;
        if(this.xmax < newline.xmax)
            this.xmax = newline.xmax;
        if(this.ymin > newline.ymin)
            this.ymin = newline.ymin;
        if(this.ymax < newline.ymax)
            this.ymax = newline.ymax;
    }
}

module.exports = group;