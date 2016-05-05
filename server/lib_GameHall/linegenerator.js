var xml2js = require('xml2js'),
    fs = require("fs");
    dict = require("./objDictionary");


// find bbox from a series of strokes
function getBBox(strokes){
    var top = Number.MAX_VALUE;
    var left = Number.MAX_VALUE;
    var bottom = Number.MIN_VALUE;
    var right = Number.MIN_VALUE;
    
    for(var i=0;i < strokes.length; i++){
        for(var j=0;j<strokes[i].points.length;j++){
            var pt = strokes[i].points[j];
            top = pt.y < top ? pt.y : top;
            bottom = pt.y > bottom ? pt.y : bottom;
            left = pt.x < left ? pt.x : left;
            right = pt.x > right ? pt.y : right;
        }
    }
    
    return {
        top: top, 
        bottom: bottom, 
        left: left,
        right: right
    };
}

function findLeastUsageInQuadtree(quadtree, box, canvasSize){
    var boxWidth = box.right - box.left;
    var boxHeight = box.bottom - box.top;

    console.log(box);    
    /*var widthInterval = (canvasSize.width - boxWidth) / 10;
    var heightInterval = (canvasSize.height - boxHeight - 600) / 10;
    var curXpos = 0;
    var curYpos = 200; // account for the top bar
    var offset = {x: 0, y: 0};
    var minCount = Number.MAX_VALUE;*/
   
    var limLeft = 0;
    var limTop = 200;
    var limRight = canvasSize.width - boxWidth;
    var limBottom = canvasSize.height - boxHeight - 600; // 600 => empirical value;    
 
    var curLeftPos = box.left;
    var curTopPos = box.top;
    var curRightPos = box.right;
    var curBottomPos = box.bottom;

    var curX = box.left - boxWidth;
    var curY = box.top - boxHeight;

    var doneLeft = false;
    var doneTop = false;
    var doneRight = false;
    var doneBottom = false;

    var finishLeft = false;
    var finishTop = false;
    var finishRight = false;
    var finishBottom = false;

    var offset = {x: 0, y: 0};
    var minCount = Number.MAX_VALUE;
    
    var chkCount = 0; 

    while(!finishLeft || !finishRight || !finishBottom || !finishTop){
        if(chkCount > 100) {
            console.log("checking least usage location reaching limit!!");
            break;
        }
        if(!doneLeft){
            curY = curY + boxHeight;
            if(curY >= curBottomPos)
                doneLeft = true;
        }else if(!doneBottom){
            curX = curX + boxWidth;            
            if(curX >= curRightPos)
                doneBottom = true;
        }else if(!doneRight){
            curY = curY - boxHeight;
            if(curY <= curTopPos - boxHeight)
                doneRight = true;
        }else{
            curX = curX - boxWidth;
            if(curX <= curLeftPos - boxWidth){
                console.log("done a cycle");
                doneLeft = false;
                doneRight = false;
                doneBottom = false;
                doneTop = false;
                curLeftPos = curLeftPos - boxWidth;
                curRightPos = curRightPos + boxWidth;
                curTopPos = curTopPos - boxHeight;
                curBottomPos = curBottomPos + boxHeight;
                
                if(curLeftPos <= limLeft){
                    curLeftPos = limLeft;
                    finishLeft = true;
                }
                if(curTopPos <= limTop){
                    curTopPos = limTop;
                    finishTop = true;
                }
                if(curRightPos >= limRight){
                    curRightPos = limRight;
                    finishRight = true;
                }
                if(curBottomPos >= limBottom){
                    curBottomPos = limBottom;
                    finishBottom = true;
                }
            }
        }
        console.log("checking pos: " + curX + ", " + curY);
        var curBox = {
            x: curX,
            y: curY,
            width: boxWidth,
            height: boxHeight
            };
        var hitObjects = quadtree.retrieve(curBox);
        if(hitObjects.length < minCount){
            offset = {x: curX, y: curY};
            minCount = hitObjects.length;
        }
        chkCount++;
    }
    
    offset.x = offset.x < 10 ? 10 : offset.x;
    offset.x = offset.x > canvasSize.width - boxWidth ? canvasSize.width - boxWidth : offset.x;
    offset.y = offset.y < 200 ? 200 : offset.y;
    offset.y = offset.y > canvasSize.height - boxHeight ? canvasSize.height - boxHeight : offset.y;

    console.log(offset);

    /*while(curYpos < canvasSize.height){
        var curBox = {
            x: curXpos,
            y: curYpos,
            width: boxWidth,
            height: boxHeight};     

        var hitObjects = quadtree.retrieve(curBox);
        if(hitObjects.length < minCount){
            offset = {x: curXpos, y: curYpos};
            minCount = hitObjects.length;
        }
        //console.log(hitObjects.length); 
        curXpos = curXpos + widthInterval;
        if(curXpos >= canvasSize.width){
            curXpos = 0;
            curYpos = curYpos + heightInterval;
        }
    }*/
    //console.log(offset);
    return offset;
}

function pickCategoryInDict(dict, oriCategory){
	var diclength = dict.length;
	console.log("finding: " + oriCategory);
        for(var i=0;i<diclength;i++){
		//console.log(dict[i]);
                if(dict[i] == oriCategory){
               		var selIndex = Math.floor((Math.random() * diclength) + 1);
                        var selCate = dict[selIndex];
                        console.log("selected class = " + selCate);
                        return selCate;
                }
        }
	return "";
}

function decideCategory(oriCategory){
	var selCategories = [];
	
	for ( var subcate in dict ){
		if (dict.hasOwnProperty(subcate)) {
			var subDict = dict[subcate];
			if(Array.isArray(subDict)){
				var pickedCate = pickCategoryInDict(subDict, oriCategory);
				if(pickedCate != "")
					return pickedCate;
			}else{
				for ( var subsubcate in subDict ){
					var subsubDict = subDict[subsubcate];
					if(Array.isArray(subsubDict)){
						var pickedCate = pickCategoryInDict(subsubDict, oriCategory);
						if(pickedCate != "")
							return pickedCate;
					}else{
						for(var subsubsubcate in subsubDict){
							var subsubsubDict = subsubDict[subsubsubcate];
							if(Array.isArray(subsubsubDict)){
                                                		var pickedCate = pickCategoryInDict(subsubsubDict, oriCategory);
                                                		if(pickedCate != "")
                                                        		return pickedCate;
                                        		}
						}
					}
				}
			}
    		}	
	}
	return oriCategory;
}

module.exports = {
    GetSketchesInCategory : function(category, quadtree, canvasSize, objBox, callback){
        var newcate = category;
	console.log(this.mode);
	if(this.mode == 2) newcate = decideCategory(category);

	    var node = quadtree.findLeastUsageOnLevel(5);
        var tolX = canvasSize.width / 8;
        var tolY = canvasSize.height / 8;
        var offsetX = node.bounds.x + tolX > canvasSize.width ? node.bounds.x - tolX : node.bounds.x;
        var offsetY = node.bounds.y + tolY > canvasSize.height ? node.bounds.y - tolY : node.bounds.y;
        //offsetX = offsetX < tolX ? offsetX + tolX : offsetX;
        offsetY = offsetY < tolY ? offsetY + tolY : offsetY;
        var offset = {x: offsetX, y: offsetY};
        
	    var dirpath = 'lib_GameHall/pre_sketch2/' + newcate + '/';
        var parser = new xml2js.Parser();
        
        fs.readdir(dirpath, function(err, files){
            if(err){
                //throw new Error('directory is not found!!');
                return "";
            }
            var fileIndex = Math.floor(Math.random() * files.length);
            var filename = files[fileIndex];
            
            fs.readFile(dirpath + filename, 'utf8', function(err, data) {
                // if(err){
                //     throw new Error('SVG file not found or invalid');
                // }
                var strokes = JSON.parse(data).strokes;
                var bbox = getBBox(strokes);
                 
                var offset = findLeastUsageInQuadtree(quadtree, objBox, canvasSize, objBox);
                offset = {
                    x: offset.x - bbox.left,
                    y: offset.y - bbox.top
                };

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

                if(callback != null && typeof callback === "function"){
                    var decision = {
                        classification: category,
                        selection:      newcate 
                    };
			        callback.call(this, strokes, decision, err);
                }
            });
        });
    },
    mode : 1
}
