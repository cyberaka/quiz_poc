var topicsModule= angular.module('quiz.module.topics', ['quiz.resources.topics'])
topicsModule.controller('TopicController',function($scope,$state,TopicService){
  $scope.topics=[]
  TopicService.getTopics().then(function(results){
    if(results!=null){
      $scope.topics=angular.copy(results)
    }
  },function(){

  })
  $scope.showSubTopics = function(theTopicId){
      $state.go('subtopics',{topicId:theTopicId})
  }
})
