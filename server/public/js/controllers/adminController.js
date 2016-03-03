var hallapp = angular.module('appHall', ['ngResource']);

hallapp.controller('hallController', ['$scope', '$resource', function ($scope, $resource) {
    var CreateRoom = $resource('/room/create');
    var JoinRoom = $resource('/room/join');

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
                window.location.href = "/app";
        });
    }
}]);