var fs = require('fs');
var files = ["UserData/p7_T2_comp.txt", "UserData/p7_T2_user.txt"];
var objs = [];

//1443715920243userLines.json

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
                            var x = pt["x"];
                            var y = pt["y"];
                            var lineID = pt["lineID"];
                            var groupID = pt["groupID"];
                            stream.write(strokeID + ", " + isComp + ", " + lineID + ", " + time + ", " + timeStamp + ", " + x + ", " + y + ", " + groupID + "\n");
                            console.log("line: " + strokeID + ", point" + i);
                            
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

