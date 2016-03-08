var xml2js = require('xml2js'),
    fs = require("fs");

module.exports = {
    GetSketchesInCategory : function(category){
        var dirpath = 'svg/' + category + '/';
        var parser = new xml2js.Parser();
        
        fs.readdir(dirpath, function(err, files){
            if(err){
                throw new Error('directory is not found!!');
            }
            var fileIndex = Math.floor(Math.random() * files.length);
            var filename = files[fileIndex];
            
            fs.readFile(dirpath + filename, function(err, data) {
            
                if(err){
                    throw new Error('SVG file not found or invalid');
                }
                
                parser.parseString(data, function (err, result) {
                    console.log(result);
                    try{
                        var paths = result.svg.g[0].g[0].path;
                        //return json; 
                        var strokes = [];
                        
                        for(var i=0;i<paths.length;i++){
                            var rawpath = paths[i].$.d;
                            
                            
                        }
                    }catch(ex){
                        console.log(ex);
                    }
                    
                });
            });
        });
        
    }
}