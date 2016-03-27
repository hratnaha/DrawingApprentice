var xml2js = require('xml2js'),
    fs = require("fs");

module.exports = {
    GetSketchesInCategory : function(category, callback){
        var dirpath = 'lib_GameHall/pre_sketch/' + category + '/';
        var parser = new xml2js.Parser();
        
        fs.readdir(dirpath, function(err, files){
            if(err){
                throw new Error('directory is not found!!');
            }
            var fileIndex = Math.floor(Math.random() * files.length);
            var filename = files[fileIndex];
            
            fs.readFile(dirpath + filename, 'utf8', function(err, data) {
                // if(err){
                //     throw new Error('SVG file not found or invalid');
                // }
                console.log(data);
                var strokes = JSON.parse(data);

                for(var i=0;i < strokes.length; i++){
                    strokes[i]['data'] = strokes[i].data;
                    strokes[i]['lineWidth'] = 2;
                    strokes[i]['color'] = {r: 0, g: 0, b: 0, a: 0};
                    strokes[i]['allPoints'] = strokes[i].points;
                }                
                
                if(callback != null && typeof callback === "function"){
                    callback.call(this, strokes, err);
                }
            });
        });
        
    }
}