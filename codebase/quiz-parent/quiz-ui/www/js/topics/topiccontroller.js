var topicsModule= angular.module('quiz.module.topics', ['quiz.resources.topics'])
topicsModule.controller('TopicController',function($scope,TopicService){
  TopicService.getTopics()
  // $scope.login= function(){
  //   $state.go('topics',{})
  // }
})
