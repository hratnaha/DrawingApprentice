var hallapp = angular.module('appHall', ['ngResource']);

Array.prototype.remove = function(index){
  this.splice(index,1);
}

hallapp.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

hallapp.controller('hallController', ['$scope', '$resource', function ($scope, $resource) {
    var CreateRoom = $resource('create');
    var JoinRoom = $resource('join');// add DrawingApprentice in the path to comfort our complicated proxy settings
    var DeleteRoom = $resource('delete');
    
    $scope.rooms = []
    CreateRoom.query(function (results) {
        $scope.rooms = results;
    });

    $scope.createRoom = function () {
        var room = new CreateRoom();
        room.name = $scope.roomName;
        room.host = userData;
    
        // for creating a new room
        room.$save(function (result) {
            console.log(result);
            $scope.rooms.push(result);
            $scope.roomName = '';
        });
    }

    $scope.joinRoom = function (roomID) {
        var room = new JoinRoom();
        room.id = roomID;
        room.newPlayer = userData;
        room.$save(function (result) {
            // redirect to the app page
            if(result.isSucceed)
                window.location.href = "../app/";
        });
    }
    $scope.deleteRoom = function(roomID){
        var room = new DeleteRoom();
        room.id = roomID;
        room.requester = userData.id;
	for(var i=0;i<$scope.rooms.length;i++){
                    var curroom = $scope.rooms[i];
                    if(curroom.id == roomID){
                        $scope.rooms.splice(i, 1);
                        break;
                    }
                }


        room.$save(function(result){
            if(result.isSucceed){
                console.log('remove room: ' + roomID);    
            }
        });
    }
}]);
