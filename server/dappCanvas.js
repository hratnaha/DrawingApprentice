/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";

var fs = require('fs'),
    gm = require('gm'),
    Canvas; 
    
try{
    Canvas = require('canvas');
}catch(e){
    console.log("canvas not loaded correctly: " + e);
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
    SaveStrokesIntoPng: function (canvasSize, sessionID, userLines, computerLines){
        if(Canvas){
            var Image = Canvas.Image,
                canvas = new Canvas(canvasSize.width, canvasSize.height),
                ctx = canvas.getContext('2d');
            
            function drawLine(line){
                if(line.allPoints && line.allPoints.length > 0){
                    ctx.beginPath();
                    ctx.moveTo(line.allPoints[0].x, line.allPoints[0].y);
                    
                    for(var ptID in line.allPoints){
                        if(ptID > 0){
                            var pt = line.allPoints[ptID];
                            ctx.lineTo(pt.x, pt.y); 
                        }
                    }
                    ctx.stroke();
                }
            }
            
            ctx.strokeStyle = 'rgba(0,0,0,1)';
            for(var lineID in userLines)
                drawLine(userLines[lineID]);        
            for(var lineID in computerLines)
                drawLine(computerLines[lineID]);
    
            var out = fs.createWriteStream(__dirname + '/session_pic/' + sessionID + '.png'),
                stream = canvas.pngStream();
            
            stream.on('data', function(chunk){
                out.write(chunk);
            });
            
            stream.on('end', function(){
                console.log('saved png for session: ' + sessionID);
            });
        }
    },
    ConvertDrawingToPng: function(canvasSize, sessionID, userLines, computerLines){
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
                //console.log(pts);
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
                
        ctx.write(__dirname + '/session_pic/' + sessionID + '.png', function (err) {
            console.log(err);
        });
        
        ctx.resize(80, 120);
        ctx.write(__dirname + '/session_pic/' + sessionID + '_thumb.png', function (err) {
            console.log(err);
        });
    }
}
