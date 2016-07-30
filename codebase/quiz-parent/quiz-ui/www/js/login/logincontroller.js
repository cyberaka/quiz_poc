angular.module('quiz.module.login', [])
.controller('LoginController',function($scope,$state){
  $scope.login= function(){
  //  $state.go('topics',{})
  }
  $scope.quiz= function(){
    $state.go('topics',{})
  }
})
