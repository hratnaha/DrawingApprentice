var fs = require('fs');
var obj;
//1443715920243userLines.json
fs.readFile('1443715920243userLines.json', 'utf8', function (err, data) {
    if (err) throw err;
    var str = JSON.stringify(data);
    str = str.replace(/\\/g, "");
    str = str.slice(2, -1);
    
    obj = JSON.parse(str);
    
    var stream = fs.createWriteStream("1443715920243userLines.csv");
    stream.once('open', function (fd) {
        stream.write("strokeID, isCompGenerated, lineID, time, x, y, groupID\n");
        var i = 0;
        for (var strokeID in obj) {
            var stroke = obj[strokeID];
            var points = stroke["allPoints"];
            var isComp = stroke["compGenerated"];
            for (var ptID in points) {
                var pt = points[ptID];
                var time = pt["timestamp"];
                var x = pt["x"];
                var y = pt["y"];
                var lineID = pt["lineID"];
                var groupID = pt["groupID"];
                stream.write(strokeID + ", " + isComp + ", " + lineID + ", " + time + ", " + x + ", " + y + ", " + groupID + "\n");
                console.log("line: " + strokeID + ", point" + i);

                i = i + 1;
            }
        }
        stream.end();
    });
    
    //console.log(obj);
});