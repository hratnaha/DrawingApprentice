var fs = require("fs"),
    dirpath = "./pre_sketch/";
fs.readdir(dirpath, function(err, files){
    for (var fileIndex = 0; fileIndex < files.length; fileIndex++) {
        var filename = files[fileIndex];
        console.log(filename);
        var dirfilename = filename.split('_');
        if(dirfilename.length == 2){
            try {
                fs.mkdirSync(dirpath + dirfilename[0]);
            } catch(e) {
                if ( e.code != 'EEXIST' ) throw e;
            }

            fs.renameSync(dirpath + filename, dirpath + dirfilename[0] + '/' + dirfilename[1]);
        }
    }
});