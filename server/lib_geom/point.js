"use strict";
class point{
    constructor(x, y, lineID, time){
        this.x = x;
        this.y = y;
        this.time = time ? time : (new Date()).getTime();
        this.lineID = lineID;
        this.node = {};
    }
    // return -1 when the comparing pt is on the right or down side of this point
    // return 0 if both points are on the same position
    // return 1 when the comparing pt is on the left or up side of this point
    compareTo(cpt){
        if (this.x < cpt.x) {
            return -1;
        } else if (this.x > cpt.x) {
            return 1;
        } else {
            if (this.y < cpt.y) {
                return -1;
            } else if (this.y > cpt.y) {
                return 1;
            }
            return 0;
        }
    }
    distTo(nexPt){
        return Math.sqrt((this.x - nexPt.x)*(this.x - nexPt.x) + (this.y - nexPt.y)*(this.y - nexPt.y));
    }
}

module.exports = point;