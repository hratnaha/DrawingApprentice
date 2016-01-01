var hallapp = angular.module('appHall', ['ngResource']);

hallapp.controller('hallController', ['$scope', '$resource', function ($scope, $resource) {
  var Room = $resource('/api/rooms');
  
  $scope.rooms = []
  Room.query(function (results) {
    $scope.rooms = results;
  });
   
  $scope.createRoom = function () {
    var room = new Room();
    room.name = $scope.roomName;
    room.test = "test id";
    // room.id ..... get the necessary information from the form and post it to the server
    // for creating a new room
    
    room.$save(function (result) {
      console.log(result);
      $scope.rooms.push(result);
      $scope.roomName = '';        
      //window.location.href = "/app";
    });
  }
  
  $scope.joinRoom = function(sessionID) {
      
  }
  
}]);