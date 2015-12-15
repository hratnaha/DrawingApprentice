/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";

var Canvas = require('canvas');

module.exports = {
    SaveStrokesIntoPng: function (canvasSize, sessionID, userLines, computerLines){
        var Image = Canvas.Image
        , canvas = new Canvas(canvasSize.width, canvasSize.height)
        , ctx = canvas.getContext('2d');
        
        ctx.strokeStyle = 'rgba(0,0,0,1)';
        
        for(var lineID in userLines){
            var line = userLines[lineID];
            ctx.beginPath();
            console.log(line);
            ctx.moveTo(line.allPoints[0].x, line.allPoints[0].y);
            
            for(var ptID in line.allPoints){
                var pt = line.allPoints[ptID];
                if(ptID > 0){
                   ctx.lineTo(pt.x, pt.y); 
                }
            }
            ctx.stroke();
        }
        // console.log('<img src="' + canvas.toDataURL() + '" />');
        var fs = require('fs')
        , out = fs.createWriteStream(__dirname + '/text.png')
        , stream = canvas.pngStream();
        
        stream.on('data', function(chunk){
        out.write(chunk);
        });
        
        stream.on('end', function(){
        console.log('saved png');
        });
    }
}
