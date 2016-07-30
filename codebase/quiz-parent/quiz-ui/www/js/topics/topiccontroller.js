var topicsModule = angular.module('quiz.module.topics', ['quiz.resources.topics'])
topicsModule.controller('TopicController', function($scope, $state, TopicService) {
  $scope.data = {
    topics: []
  }

  TopicService.getTopics().then(function(results) {
    if (results != null) {
      $scope.data.topics = angular.copy(results)
    }
  }, function() {

  })
  $scope.showSubTopics = function(theTopicId) {
    $state.go('subtopics', {
      topicId: theTopicId
    })
  }
})
