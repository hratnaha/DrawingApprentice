// var fs = require('fs');
// var Canvas = require('canvas')
//   , Image = Canvas.Image
//   , canvas = new Canvas(200, 200, 'svg')
//   , ctx = canvas.getContext('2d');
  
// ctx.font = '30px Impact';
// ctx.rotate(.1);
// ctx.fillText("Awesome!", 50, 100);

// var te = ctx.measureText('Awesome!');
// ctx.strokeStyle = 'rgba(0,0,0,0.5)';
// ctx.beginPath();
// ctx.lineTo(50, 102);
// ctx.lineTo(50 + te.width, 102);
// ctx.stroke();


// ctx.strokeStyle = '#000';
// ctx.lineWidth = 1;
// ctx.fillStyle = '#000'

// //getPointAtLength

// fs.writeFile('out.svg', canvas.toBuffer());

//////////////////////////////////////////////////////

// var FS = require('fs'),
//     PATH = require('path'),
//     SVGO = require('svgo'),
//     filepath = 'svg/airplane/1.svg',
//     svgo = new SVGO(/*{ custom config object }*/);

// FS.readFile(filepath, 'utf8', function(err, data) {

//     if (err) {
//         throw err;
//     }

//     svgo.optimize(data, function(result) {

//         console.log(result);

//         // {
//         //     // optimized SVG data string
//         //     data: '<svg width="10" height="20">test</svg>'
//         //     // additional info such as width/height
//         //     info: {
//         //         width: '10',
//         //         height: '20'
//         //     }
//         // }

//     });

// });

////////////////////////////////////////////////////////
var xml2js = require('xml2js'),
    fs = require("fs"),
    category = 'airplane',
    svgpath = require('svgpath'),
    path = require('path');

function getDirectories(srcpath) {
  return fs.readdirSync(srcpath).filter(function(file) {
    if(file[0] == '.') return false;
    return fs.statSync(path.join(srcpath, file)).isDirectory();
  });
}

var alldirs = getDirectories('svg/');
var parser = new xml2js.Parser();
var jsonIndex = 0;
for (var i = 0; i < alldirs.length; i++) {
    var category = alldirs[i];
    var dirpath = 'svg/' + category + '/';
    var files = fs.readdirSync(dirpath);
    
    for (var fileIndex = 0; fileIndex < files.length; fileIndex++) {
        var filename = files[fileIndex];
        if(filename[0] != '.'){
            var data = fs.readFileSync(dirpath + filename);
            parser.parseString(data, function (err, result) {
                console.log(result);
                try{
                    var paths = result.svg.g[0].g[0].path;
                    var strokes = [];
                    
                    for(var i=0;i<paths.length;i++){
                        var rawpath = paths[i].$.d;
                        
                        var pts = [];
                        var line = svgpath(rawpath);
                        
                        line.iterate(function(seg, index, ptx, pty){
                            console.log(seg);
                            if(index > 0)
                                pts.push({x: ptx, y: pty});
                        });
                        
                        var stroke = {data: rawpath, points: pts};
                        strokes.push(stroke);
                    }
                    var jsonfile = require('jsonfile');
                    var file = category + '_' + jsonIndex + '.json';
                    jsonfile.writeFileSync(file, strokes);
                    
                    jsonIndex++;
                    
                }catch(ex){
                    console.error(ex);
                }
                
            });
        }
    }
}