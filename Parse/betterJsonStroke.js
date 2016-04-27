var xml2js = require('xml2js'),
    fs = require("fs"),
    path = require('path'),
    express = require('express'),
    bodyParser = require('body-parser'),
    http = require('http'),
    app = express();

var dirpath2 = "pre_sketch2/";
function getDirectories(srcpath) {
  return fs.readdirSync(srcpath).filter(function(file) {
    if(file[0] == '.') return false;
    return fs.statSync(path.join(srcpath, file)).isDirectory();
  });
}

// app.use(bodyParser.json());
app.use(express.static(__dirname + '/public'));
app.listen(3000);

var server = http.createServer(app);
var io = require('socket.io')(server);
server.listen(8080); // for local debug
var i = 0;
var j = 0;

io.on('connection', function (so) {
   
    console.log("new client connected");
    so.emit('connection', { hello: "world" });
    
    var alldirs = getDirectories('pre_sketch/');
    var parser = new xml2js.Parser();
    
    
    so.on('onOpen', function(){
        var category = alldirs[i];
        var dirpath = 'pre_sketch/' + category + '/';
        var files = fs.readdirSync(dirpath);
        
        
        function sendJob(curfiles){
            var filename = curfiles[j];
            if(filename[0] != '.'){
                var filepath = dirpath + filename;
                console.log(filepath);
                var data = fs.readFileSync(filepath);
                var strokes = JSON.parse(data);
                var message = {};
                message['strokes'] = strokes;
                message['category'] = category;
                message['filename'] = filename;

                var strMsg = JSON.stringify(message);
                so.emit('newStroke', strMsg);
            }
        }
        
        so.on('respond', function(data){
            var resultStrokes = JSON.parse(data);
            var jsonfile = require('jsonfile');
            var file = resultStrokes.category + '_' + resultStrokes.filename;
            try {
                fs.mkdirSync(dirpath2 + resultStrokes.category);
            } catch(e) {
                if ( e.code != 'EEXIST' ) throw e;
            }
            var file = dirpath2 + resultStrokes.category + '/' + resultStrokes.filename;
            jsonfile.writeFileSync(file, resultStrokes);
            
            j++;
            if(j>=files.length - 1){
                i++;
                j = 0;
                if(i < alldirs.length){
                    category = alldirs[i];
                    console.log("next category: " + category);
                    dirpath = 'pre_sketch/' + category + '/';
                    files = fs.readdirSync(dirpath);
                    sendJob(files);
                }
            }
            else
                sendJob(files);
        });
        
        sendJob(files);
    });
    
    
});



