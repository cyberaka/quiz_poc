var loginModule = angular.module('quiz.module.login', ['quiz.resources.logins'])
loginModule.controller('LoginController', function($scope, $state, LoginService) {
  $scope.data = {
    username: "cyberaka",
    password: "abcd1234",
    logins: []
  }

  $scope.login= function(username, password){
      LoginService.login(username, password).then(function(results) {
        console.log("Result received >> " + results);
        if (results != null && results.email != null) {
          $scope.data.logins = angular.copy(results);
          console.log("Login >> Result >> Email = " + $scope.data.logins.email);
          $state.go('topics',{});
        } else {
          alert("Invalid Login Credentials.");
          console.log("Result is null.")
        }
      }, function() {
      alert("Invalid Login Credentials.");
        console.log("We have a problem.");
      })
  }
  $scope.quiz= function(){
    $state.go('topics',{})
  }
})
