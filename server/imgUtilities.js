/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";

var fs = require('fs'),
    gm;
    
try{
    gm = require('gm').subClass({imageMagick: true}); 
}catch(err){
    console.log(err);
}

function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    r = r * 255;
    g = g * 255;
    b = b * 255;
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

module.exports = {
    ConvertDrawingToPng: function(canvasSize, sessionID, userLines, computerLines){
        if(gm){
            var ctx = gm(canvasSize.width, canvasSize.height, "#ffffff")
                .fill("transparent");
                
            function drawLine(line){
                if(line.allPoints && line.allPoints.length > 0){
                    var hexColor = rgbToHex(line.colorR, line.colorG, line.colorB);
                    console.log(hexColor);
                    ctx.stroke(hexColor, line.thickness);
                    var pts = [];
                    for(var ptID in line.allPoints){
                        var pt = line.allPoints[ptID];
                        pts.push(pt.x, pt.y); 
                    }
                    console.log("num of points: " + pts.length);
                    //if(pts.length < 500)
                        ctx.drawPolyline(pts);
                }
            }
            var alllines = [];
            alllines = alllines.concat(userLines);
            alllines = alllines.concat(computerLines);
            
            alllines.sort(function(a, b){
                if(a.startTime > b.startTime) return 1;
                if(a.startTime < b.startTime) return -1;
                return 0;
            });
            
            for(var lineID in alllines)
                drawLine(alllines[lineID]);
            
            console.log("prepare to save the original image!!");
            ctx.write(__dirname + '/session_pic/' + sessionID + '.png', function (err) {
                //console.log(computerLines);
                if(err)
                    console.log(err);
            });
            
            console.log("prepare to save the thumbnail image!!");
            ctx.resize(80, 120);
            ctx.write(__dirname + '/session_pic/' + sessionID + '_thumb.png', function (err) {
                if(err)
                    console.log(err);
            });
        }
    }
}
