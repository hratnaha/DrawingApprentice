﻿// Facebook Authentication
module.exports = {
	"clientID" 		        : "1668411693393703",                   //facebook_api_key
    "clientSecret"	        : "d059a2cb9fda79d68964e82b96a28212",   //facebook_api_secret
    "callbackURL"			: "/auth/facebook/callback",    
    "profileFields"         : ['emails', 'first_name', 'last_name', 'gender', 'age_range'],
    "use_database"			: "false",
    "host"					: "localhost",
    "username"				: "root",
    "password"				: "",
    "database"				: ""
}
