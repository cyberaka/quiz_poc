var subTopicsModule = angular.module('quiz.module.subtopics', ['quiz.resources.subtopics'])
subTopicsModule.controller('SubTopicController', function($scope, $state, $stateParams, SubTopicService) {

  $scope.data = {
    subTopics: []
  }
  var topicId = $stateParams.topicId
  SubTopicService.getSubTopics(topicId).then(function(results) {
    if (results != null) {
      $scope.data.subTopics = angular.copy(results)
    }
  }, function() {

  })
  $scope.showQuizSettings = function(theTopicId, theSubTopicId) {
    $state.go('quizsettings', {
      topicId: theTopicId,
      subTopicId: theSubTopicId
    })
  }
})
