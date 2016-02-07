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
    console.error(err);
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
    //Iinitialize the drawing context that has white background
    // and transparent filling in of the drawing lines
    Initialize : function(width, height){
        if(gm){
            var ctx = gm(width, height, "#ffffff")
                .fill("transparent");
            return ctx;
        }
    },
    InitializeFromFile : function(file){
      if(gm){
          var ctx = gm(file)
            .fill("transparent");
            return ctx;
      }  
    },
    CreateBlankThumb: function(picName){
        if(gm){
            var ctx = gm(80, 60, "#ffffff");
            ctx.write(__dirname + '/session_pic/' + picName + '_thumb.png', function (err) {
                    if(err)
                        console.log(err);
            });
        }
    },
    DrawLine : function(ctx, line, translate){
        translate = translate ? translate: {x : 0, y : 0};
        if(line.allPoints && line.allPoints.length > 0){
            var lineColor;
            if(line.color){
                lineColor = rgbToHex(line.color.r, line.color.g, line.color.b);
            }else
                lineColor = rgbToHex(line.colorR, line.colorG, line.colorB);
            //console.log(hexColor);
            ctx.stroke(lineColor, line.lineWidth);
            var pts = [];
            for(var ptID in line.allPoints){
                var pt = line.allPoints[ptID];
                pts.push(pt.x - translate.x, pt.y - translate.y);
            }
            //console.log("num of points: " + pts.length);
            //if(pts.length < 500)
            ctx.drawPolyline(pts);
        }
    },
    ConvertDrawingToPng: function(canvasSize, picName, userLines, computerLines){
        if(gm){
            var ctx = this.Initialize(canvasSize.width, canvasSize.height);
         
            var alllines = [];
            alllines = alllines.concat(userLines);
            alllines = alllines.concat(computerLines);
            
            alllines.sort(function(a, b){
                if(a.startTime > b.startTime) return 1;
                if(a.startTime < b.startTime) return -1;
                return 0;
            });
            
            for(var lineID in alllines)
                this.DrawLine(ctx, alllines[lineID]);
            
            this.SaveToFile(ctx, picName);
        }
    },
    SaveToFile : function(ctx, picName, isThumb, callback){
        if(gm){
            var filename = isThumb ? picName + "_thumb" : picName;
            filename = __dirname + '/session_pic/' + filename + '.png'; 
            ctx.write(filename, function (err) {
                if(err) console.error(err);
                if(callback != null && typeof callback === "function"){
                    callback.call(this, filename, err);
                }
            });
        }        
    }
}
