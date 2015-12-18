# server
	This is the main folder of the Node.js server that bridges the clients and the apprentice AI agent.
	- [Node.js Version 4.2.2 (32 bit)](https://nodejs.org/download/release/v4.2.2/)
	- Please refer to [package.json](https://github.gatech.edu/chsiao9/DrawingApprentice/blob/master/server/package.json) for Node.js dependencies.
	- For Node.js Java bridge, if your development environment is Windows, please install JDK 7 32 bit.
	- For GM dependencies, please install [GraphicsMagick](http://www.graphicsmagick.org/) and [ImageMagick](http://www.imagemagick.org/script/index.php) before installing nodejs module. 
		
# configuration
	It contains the settings to numerous servers we are using in DApp.
	Currently it has settings for facebook, google authentication services, and the mongo database server.

# jars
	All the used java libraries (as .jar). The compiled apprentice file needs to go into here.

# public
	The client side codes, including html used for front pages and corresponding javascript codes.
	The current release version of the website is on [DrawingApprentice](http://adam.cc.gatech.edu/DrawingApprentice/) website.

# session_pic
	The saved png and thumbnails for each session
	
# views
	the html template for the drawing app.