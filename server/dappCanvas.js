/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";

var fs = require('fs'),
    Canvas; 
    
try{
    Canvas = require('canvas');
}catch(e){
    console.log("canvas not loaded correctly: " + e);
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
    }
}
