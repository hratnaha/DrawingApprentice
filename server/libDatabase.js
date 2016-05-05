"use strict";
var canvas2D = require('./libImage'),
    http = require('http'),
    mongoConfig = require('./configuration/mongoServerConfig');

var userProfile = {},
    sessionID = '',
    apprentice = {},
    canvasSize = {}; 

module.exports = {
    initializeParameters : function(profile, id, ai, size){
        userProfile = profile;
        sessionID = id;
        apprentice = ai;
        canvasSize = size;
    },
    onSaveDataOnDb: function () {
        if (userProfile && userProfile != "") {
            try{
            var userId = userProfile.id;

            console.log(userId);
            console.log(sessionID);

            var userLines;
            var computerLines;
            var groupLines = apprentice.labeledGroups;  

            apprentice.getUserLines(function (err, item) {
                if (err) {
                    console.log(err);
                } else {
                    try {
                        userLines = JSON.parse(item);
                    } catch (e) {
                        console.log(e);
                    }
                    afterUserLines();
                }
            });

            function afterUserLines() {
                apprentice.getComputerLines(function (err, item) {
                    if (err) {
                        console.log(err);
                    } else {
                        try {
                            computerLines = JSON.parse(item);
                        } catch (e) {
                            console.log(e)
                        }
                        saveData();
                    }
                });
            }

            function saveData() {
                //canvas2D.ConvertDrawingToPng(canvasSize, sessionID, userLines, computerLines);

                var options = {
                    host: mongoConfig.host,
                    port: mongoConfig.port,
                    path: mongoConfig.base_path + userId + '/session/' + sessionID,
                    auth: mongoConfig.user + ":" + mongoConfig.pass,
                    method: 'POST',
                    headers: mongoConfig.headers
                };

                var req = http.request(options, function (res) {
                    var data = "";
                    res.on('data', function (chunk) {
                        data += chunk;
                    });
                    res.on('end', function () {
                        console.log(data);
                    });
                });
                var postData = JSON.stringify({
                    name: userProfile['name'],
                    age_range: userProfile['age_range'],
                    gender: userProfile['gender'],
                    email: userProfile['email'],
                    userLines: userLines,
                    computerLines: computerLines, 
                    labeledGroups: groupLines 
                });
                req.write(postData);
                req.end();
            }
            }catch(exception){
                console.log(exception);
            }
        }
    }
}
