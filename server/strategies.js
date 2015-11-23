"use strict";
var http = require('http'),
    FacebookStrategy = require('passport-facebook').Strategy,
    facebookConfig = require('./configuration/facebookConfig'),
    GoogleStrategy = require('passport-google-oauth').OAuth2Strategy,
    googleConfig = require('./configuration/googleConfig'),
    mongoConfig = require('./configuration/mongoServerConfig');

function CheckMongoDatabase (accessToken, refreshToken, profile, done) {
    process.nextTick(function () {
        //Check whether the User exists or not using profile.id
        (function checkIfUserExists(userId, cb) {
            // Query database server if userId exists. Call callback with its data or error.
            var options = {
                host: mongoConfig.host,
                port: mongoConfig.port,
                path: mongoConfig.base_path + userId,
                headers: mongoConfig.headers,
                method: 'GET'
            };
            var request = http.request(options, function (response) {
                var data = "";
                response.on('data', function (chunk) {
                    data += chunk;
                });
                response.on('end', function () {
                    // This is the data we received from the database
                    cb(data);
                    console.log(data);
                });
            });
            request.end();
        })(profile.id, doneCheckingForUser);
        
        function doneCheckingForUser(data) {
            // data should be the user's info as it is saved in the database,
            // plus any previous session info.
            // If no user existed, it has been added with this id.
            
            // TODO: use user_data to display sessions and allow the user to
            // select one or create new.
            return done(null, profile);
        }
    });
}

module.exports =
{
    Facebook: function () {
        return new FacebookStrategy(facebookConfig, CheckMongoDatabase);
    }(),
    Google: function () {
        return new GoogleStrategy(googleConfig, CheckMongoDatabase);
    }()
};