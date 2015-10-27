/**
 * @author Chih-Pin Hsiao
 * @email: chipin01@gmail.com
 */
"use strict";
process.title = 'sketch-server';
var oneDay = 86400000;
var ss = require("./Sketch");
var java = require("java");

java.classpath.push("commons-lang3-3.1.jar");
java.classpath.push("commons-io.jar");
java.classpath.push("commons-math3-3.3.jar");
java.classpath.push("apprentice.jar");      // apprentice library
java.classpath.push("core.jar");            // processing
java.classpath.push("flexjson.jar");
java.classpath.push("ABAGAIL.jar");

var Apprentice = java.import('jcocosketch.nodebridge.Apprentice');

// set up express
var express = require('express'),
    passport = require('passport'),
    util = require('util'),
    FacebookStrategy = require('passport-facebook').Strategy,
    session = require('express-session'),
    cookieParser = require('cookie-parser'),
    bodyParser = require('body-parser'),
    config = require('./configuration/config'),
    mysql = require('mysql'),
    app = express();

// Define MySQL parameter in Config.js file.
// Todo: Should switch to Mongo database later
var connection = mysql.createConnection({
    host     : config.host,
    user     : config.username,
    password : config.password,
    database : config.database
});
//Connect to Database only if Config.js parameter is set.
if (config.use_database === 'true') {
    connection.connect();
}
// Passport session setup.
passport.serializeUser(function (user, done) {
    done(null, user);
});
passport.deserializeUser(function (obj, done) {
    done(null, obj);
});
// Use the FacebookStrategy within Passport.
passport.use(new FacebookStrategy({
    clientID: config.facebook_api_key,
    clientSecret: config.facebook_api_secret ,
    callbackURL: config.callback_url
},
  function (accessToken, refreshToken, profile, done) {
    process.nextTick(function () {
        //Check whether the User exists or not using profile.id
        if (config.use_database === 'true') {
            connection.query("SELECT * from user_info where user_id=" + profile.id, function (err, rows, fields) {
                if (err) throw err;
                if (rows.length === 0) {
                    console.log("There is no such user, adding now");
                    connection.query("INSERT into user_info(user_id,user_name) VALUES('" + profile.id + "','" + profile.username + "')");
                }
                else {
                    console.log("User already exists in database");
                }
            });
        }
        return done(null, profile);
    });
}
));
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(cookieParser());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(session({ secret: 'keyboard cat', key: 'sid' }));
app.use(passport.initialize());
app.use(passport.session());
app.use(express.static(__dirname + '/public'));

app.get('/', function (req, res) {
    res.render('index', { user: req.user });
});
app.get('/account', ensureAuthenticated, function (req, res) {
    res.render('account', { user: req.user });
});
app.get('/auth/facebook', passport.authenticate('facebook', { scope: 'email' }));
app.get('/auth/facebook/callback',
  passport.authenticate('facebook', { successRedirect : '/app.html', failureRedirect: '/login' }),
  function (req, res) {
    res.redirect('/');
});
app.get('/logout', function (req, res) {
    req.logout();
    res.redirect('/');
});
function ensureAuthenticated(req, res, next) {
    if (req.isAuthenticated()) { return next(); }
    res.redirect('/login')
}

app.listen(3000);

// set up socket io server
var http = require('http');
var server = http.Server(app);
var io = require('socket.io')(server);
var isGrouping = false;
server.listen(8080);
//server.listen(81); // for adam server

var timeout;

io.on('connection', function (so) {
    var apprentice = new Apprentice();
    var systemStartTime = (new Date()).getTime();
    apprentice.setCurrentTime(systemStartTime);

    so.on('canvasSize', function setSize(size) {
        //var d = JSON.parse(size);
        apprentice.setCanvasSize(size.width, size.height);
    });

    console.log("new client connected");
    so.emit('newconnection', { hello: 'world' });

    function getData() {
        var userLines;
        var computerLines;

        apprentice.getUserLines(function (err, item) {
            if (err) {
                console.log(err);

            } else {
                userLines = item;
                afterUserLines();
            }
        });

        function afterUserLines() {
            apprentice.getComputerLines(function (err, item) {
                if (err) {
                    console.log(err);
                } else {
                    computerLines = item;
                    emitData();
                }
            });
        }

        function emitData() {
            var allLines = {
                userLines: userLines,
                computerLines: computerLines
            }

            so.emit('allData', allLines);
        }
    }

    function onSaveDataOnDb(userId, sessionId) {
        console.log(userId);
        console.log(sessionId);

        var userLines;
        var computerLines;
        apprentice.getUserLines(function(err, item) {
            if(err) {
                console.log(err);
            } else {
                userLines = item;
                afterUserLines();
            }
        });

        function afterUserLines() {
            apprentice.getComputerLines(function(err, item) {
                if (err) {
                    console.log(err);
                } else {
                    computerLines = item;
                    saveData();
                }
            });
        }

        function saveData() {
            var options = {
                host: 'localhost',
                port: 3005,
                path: '/user/' + userId + '/session/' + sessionId,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            var req = http.request(options, function(res) {
                var data = "";
                res.on('data', function(chunk) {
                    data += chunk;
                });
                res.on('end', function() {
                    console.log(data);
                });
            });
            var postData = JSON.stringify({
                userLines: userLines,
                computerLines: computerLines
            });
            req.write(postData);
            req.end();
        }
    }

    function onNewStrokeReceived(data) {
        var d = JSON.parse(data);

        var stroke = d.data;

        var stroketime = java.newLong(stroke.timestamp);
        // categorize if it is grouping or not
        if (isGrouping)
            apprentice.startGroupingSync(stroketime);
        else
            apprentice.addNewStrokeSync(stroketime);

        var pts = stroke.packetPoints;
        var returnSt = [];

        // adding all the points in the stroke
        for (var i = 0; i < pts.length; i++) {
            var pt = pts[i];
            var pttime = pt.timestamp;
            //console.log(pt.timestamp);
            apprentice.addPointSync(parseInt(pt.x, 10), parseInt(pt.y, 10), pt.timestamp, pt.id);
        }
        // Todo: reconstruct the message to send out

        if (isGrouping) {
            apprentice.Grouping(function (err, result) {
                if (result != null) {
                    for (var i = 0; i < result.sizeSync(); i++) {
                        var newline = result.getSync(i);

                        var newpkpts = [];
                        for (var j = 0; j < newline.sizeSync(); j++) {
                            var newpt = newline.getSync(j);

                            newpkpts.push(CreatePacketPoint(newpt));
                        }
                        stroke.packetPoints = newpkpts;

                        // decode to JSON and send the message
                        var resultmsg = JSON.stringify(stroke);
                        io.emit('respondStroke', resultmsg);
                        apprentice.setModeSync(0);
                    }
                }
            });
        } else {
            apprentice.addLine();

            if (timeout != "" || timeout != null) {
                clearTimeout(timeout);
            }

            timeout = setTimeout(function () {
                apprentice.getDecision(function (err, results) {
                    if (results != null) {
                        for (var j = 0; j < results.sizeSync(); j++) {
                            var newpkpts = [];
                            var result = results.getSync(j);
                            for (var i = 0; i < result.sizeSync(); i++) {
                                var newpt = result.getSync(i);

                                newpkpts.push(CreatePacketPoint(newpt));
                            }
                            stroke.packetPoints = newpkpts;

                            // decode to JSON and send the message
                            var resultmsg = JSON.stringify(stroke);
                            io.emit('respondStroke', resultmsg);
                        }
                   //console.log("sending: " + resultmsg);
                    }
                });
            }, 2000);
        }
        so.broadcast.emit('respondStroke', JSON.stringify(d.data));
    }

    function vote(isUp) {
        var vote = isUp ? 1 : 0;
        apprentice.voteSync(vote);
    }

    function onClear() {
        apprentice.clearSync();
    }

    function onModeChanged(mode) {
        var m = JSON.parse(mode);
        if (m == 3)
            isGrouping = true;
        else if (m == 4)
            isGrouping = false;
        else
            apprentice.setModeSync(m);
    }

    so.on('SetCreativty', function (level) {
        var d = JSON.parse(level);
        apprentice.setCreativityLevel(d);
    });
    so.on('setAgentOn', function (ison) {
        var isOnBool = JSON.parse(ison);
        apprentice.setAgentOn(!isOnBool);
    });
    so.on('getData', getData);
    so.on('touchdown', function () {
        if (timeout != "" || timeout != null) {
            clearTimeout(timeout);
        }
    });
    so.on('saveDataOnDb', onSaveDataOnDb);
    so.on('touchup', onNewStrokeReceived);
    so.on('setMode', onModeChanged);
    so.on('clear', onClear);
    so.on('submit', submitResult);
    so.on('vote', vote);
});

function submitResult(d) {
    submit(d, function (error, result) {
        console.log(result);
    });
}

function CreatePacketPoint(newpt) {
    // construct a new packet point
    var pkpt = {
        id : newpt.id,
        x : newpt.x,
        y : newpt.y,
        timestamp : newpt.timestamp,
        pressure : 0          // to be implemented when the pressure is available
    };

    return pkpt;
}

console.log("SocketIO Server Initialized!");
