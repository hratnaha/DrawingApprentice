var fs = require('fs');
var files = ["UserData/p7_T2_comp.txt", "UserData/p7_T2_user.txt"];
var objs = [];

//1443715920243userLines.json

function insertTime(line){
    //cycles through the line to add 20 to each of the timestamps

}

function readJsonAndWriteCSV(filename) {
    fs.readFile(filename, 'utf8', function (err, data) {
        if (err) throw err;
        var str = JSON.stringify(data);
        str = str.replace(/\\/g, "");
        str = str.slice(2, -1);
        
        objs.push(JSON.parse(str));
        
        if (objs.length === files.length) {
            var stream = fs.createWriteStream("UserData/p7_T2_combined.csv");
            stream.once('open', function (fd) {
                
                stream.write("strokeID, isCompGenerated, lineID, time, timeStamp, x, y, groupID\n");
                var i = 0;
                
                for (var objID in objs) {
                    var obj = objs[objID];
                    for (var strokeID in obj) {
                        console.log("Length of obj" + obj.length); 
                        var stroke = obj[strokeID];
                        var points = stroke["allPoints"];
                        var isComp = stroke["compGenerated"];
                        for (var ptID in points) {
                            var pt = points[ptID];
                            var timeStamp = pt["timestamp"];
                            var time = pt["time"];
                            if (timeStamp < 1400000000000) {
                                var tmptime = timeStamp;
                                timeStamp = time;
                                time = tmptime;
                            }
                            if (isComp){
                                
                                if (ptID == 0 && strokeID != 0) {
                                    prevStroke = obj[strokeID - 1];
                                    console.log("Obj[strokeID] = " + obj[strokeID - 1]);
                                    prevPoints = prevStroke["allPoints"];
                                    if (prevPoints.length != 0) {
                                        console.log("StrokeID" + strokeID + "PreviousPoints.length " + prevPoints.length);
                                        prevLastTime = prevPoints[prevPoints.length - 1].timestamp;
                                        if (pt.timestamp < prevLastTime) {
                                            pt.timestamp = prevLastTime;
                                        }
                                    }
                                }
                                else {
                                    pt.timestamp = pt.timestamp + (20 * ptID);
                                    timeStamp = pt.timestamp;
                                    time = timeStamp; 
                                    console.log("New timestamp = " + timeStamp);
                                }
                            }
                            var x = pt["x"];
                            var y = pt["y"];
                            var lineID = pt["lineID"];
                            var groupID = pt["groupID"];
                            stream.write(strokeID + ", " + isComp + ", " + lineID + ", " + time + ", " + timeStamp + ", " + x + ", " + y + ", " + groupID + "\n");
                            //console.log("line: " + strokeID + ", point" + i);
                            
                            i = i + 1;
                        }
                    }
                }
                
                stream.end();
            });
        }
        //console.log(obj);
    });
}

for (var fileID in files)
    readJsonAndWriteCSV(files[fileID]);