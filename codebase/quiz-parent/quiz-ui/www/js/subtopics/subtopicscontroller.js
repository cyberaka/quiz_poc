var subTopicsModule = angular.module('quiz.module.subtopics', ['quiz.resources.subtopics'])
subTopicsModule.controller('SubTopicController', function($scope, $state, $stateParams, SubTopicService) {
  $scope.subTopics = []
  var topicId = $stateParams.topicId
  SubTopicService.getSubTopics(topicId).then(function(results) {
    if (results != null) {
      $scope.subTopics = angular.copy(results)
    }
  }, function() {

  })
})
