var xml2js = require('xml2js'),
    fs = require("fs");
    //dict = require("objDictionary");

function decideCategory(oriCategory){
	var selCategories = [];
	
	for ( var subcate in dict ){
		if (dict.hasOwnProperty(subcate)) {
			var subDict = dict[subcate];     	   		
			
			if(Array.isArray(subDict)){
			
			}else{
				for ( var subsubcate in subDict ){
					
				}
			}
    		}	
	}

}

module.exports = {
    GetSketchesInCategory : function(category, offset, callback){
        


	var dirpath = 'lib_GameHall/pre_sketch2/' + category + '/';
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
                //console.log(data);
                var strokes = JSON.parse(data).strokes;
		//console.log("prepare to send out lines!");
                for(var i=0;i < strokes.length; i++){
		    strokes[i]['offset'] = offset;
                    strokes[i]['data'] = strokes[i].data;
                    strokes[i]['lineWidth'] = 2;
                    strokes[i]['color'] = {r: 0, g: 0, b: 0, a: 0};
		    var allpoints = [];
		    for(var j=0;j<strokes[i].points.length;j++){
		    	var pt = strokes[i].points[j];
			pt.x = pt.x + offset.x;
			pt.y = pt.y + offset.y;
			allpoints.push(pt);
		    }
                    strokes[i]['allPoints'] = allpoints;
                }                
                //console.log(data);
                if(callback != null && typeof callback === "function"){
                    	//console.log("call callback");
			callback.call(this, strokes, err);
                }
            });
        });
        
    },
    mode : 1
}
