/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";

var fs = require('fs'),
    gm = null,
    Canvas = null,
    Image;
    
try{
    gm = require('gm').subClass({imageMagick: true}); 
}catch(err){
    console.error(err);
}

try{
    Canvas = require('canvas');
    Image = Canvas.Image;
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
    isUsingGM : false,    
    //Iinitialize the drawing context that has white background
    // and transparent filling in of the drawing lines
    Initialize : function(width, height, useGM){
        this.isUsingGM = useGM;
        if(gm && useGM){
            var ctx = gm(width, height, "#ffffff")
                .fill("transparent");
            return ctx;
        }else if(Canvas){
            var canvas = new Canvas(width, height)
              , ctx = canvas.getContext('2d');
            ctx.fillStyle = "#ffffff";
            ctx.fillRect(0, 0, width, height);
            return canvas;
        }
        return null;
    },
    InitializeFromFile : function(file){
        if(gm && this.isUsingGM){
            var ctx = gm(file)
            .fill("transparent");
            return ctx;
        }else if(Canvas){
            try{
                var buff = fs.readFileSync(file);
                var img = new Image;
                img.src = buff;
                var canvas = new Canvas(img.width, img.height)
                  , ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, img.width, img.height);
                
                return canvas;//ctx;
            }catch(err){
                console.error(err);   
            }
        }
        return null;
    },
    CreateBlankThumb: function(picName){
        var imgpath = __dirname + '/session_pic/' + picName + '_thumb.png';
        if (Canvas) {
            var canvas = new Canvas(80, 60)
                , ctx = canvas.getContext('2d')
                , out = fs.createWriteStream(imgpath)
                , stream = canvas.pngStream();
            ctx.fillStyle = "#ffffff";
            ctx.fillRect(0,0,80,60);
            
            stream.on('data', function (chunk) {
                out.write(chunk);
            });
            
            stream.on('end', function () {
                console.log('saved png');
            });
        }
        else if (gm) {
            var ctx = gm(80, 60, "#ffffff");
            ctx.write(imgpath, function (err) {
                if (err)
                    console.log(err);
            });
        }
    },
    DrawLine : function(ctx, line, translate){
        translate = translate ? translate: {x : 0, y : 0};

        if(line.allPoints && line.allPoints.length > 0 && ctx != null){
            if(Canvas){
                var ctx2d = ctx.getContext('2d');
                if(line.color)
                    ctx2d.strokeStyle = 'rgba(' + (line.color.r * 255) +',' + (line.color.g * 255) + ', ' + (line.color.b * 255) + ', 1)';
                else
                    ctx2d.strokeStyle = 'rgba(' + (line.colorR * 255) +',' + (line.colorG * 255) + ', ' + (line.colorB * 255) + ', 1)';
                ctx2d.lineWidth = line.lineWidth;
                
                function drawLine(line){
                    if(line.allPoints && line.allPoints.length > 0){
                        ctx2d.beginPath();
                        ctx2d.moveTo(line.allPoints[0].x - translate.x, line.allPoints[0].y - translate.y);

                        for(var ptID in line.allPoints){
                            if(ptID > 0){
                                var pt = line.allPoints[ptID];
                                ctx2d.lineTo(pt.x - translate.x, pt.y - translate.y); 
                            }
                        }
                        ctx2d.stroke();
                    }
                }
                
                drawLine(line);
                console.log('draw line on canvas');
            }else if(gm){
                var lineColor;
                if(line.color){
                    lineColor = rgbToHex(line.color.r, line.color.g, line.color.b);
                }else
                    lineColor = rgbToHex(line.colorR, line.colorG, line.colorB);
                //console.log(hexColor);
                if (ctx) {
                    ctx.stroke(lineColor, line.lineWidth);
                    var pts = [];
                    for (var ptID in line.allPoints) {
                        var pt = line.allPoints[ptID];
                        pts.push(pt.x - translate.x, pt.y - translate.y);
                    }
                    //console.log("num of points: " + pts.length);
                    //if(pts.length < 500)
                    ctx.drawPolyline(pts);
                }
            }
        }
    },
    ConvertDrawingToPng: function(canvasSize, picName, userLines, computerLines){
        if(gm && ctx != null){
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
        var filename = isThumb ? picName + "_thumb" : picName;
        filename = __dirname + '/session_pic/' + filename + '.png';
        if(ctx != null){
            if (Canvas && !this.isUsingGM) {
                var out = fs.createWriteStream(filename)
                    , stream = ctx.pngStream();
                
                stream.on('data', function (chunk) {
                    out.write(chunk);
                });
                
                stream.on('end', function () {
                    if(callback != null && typeof callback === "function"){
                        callback.call(this, filename);
                    }
                });
            }else if(gm){
		console.log("using gm to save image")
                ctx.write(filename, function (err) {
                    if(err) console.error(err);
                    if(callback != null && typeof callback === "function"){
                        callback.call(this, filename, err);
                    }
                });
            }    
        }    
    }
}
