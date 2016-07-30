var questionsModule = angular.module('quiz.module.questions', ['quiz.resources.questions'])
questionsModule.controller('QuestionController', function($scope, $state, $stateParams, QuestionService) {
  $scope.data = {
      questions: [],
      index: -1,
      count: 0,
      score: 0,
      level: 1
    }
    // $scope.questions = []
    // $scope.index = -1
    // $scope.score = 0

  if ($state.current.name == "quizsettings") {
    $scope.data.level = 1
    $scope.data.count = 10
  } else if ($state.current.name == "quiz") {
    var theTopicId = $stateParams.topicId
    var subTopicId = $stateParams.subTopicId
    QuestionService.getQuestions($stateParams.topicId, $stateParams.subTopicId, $stateParams.level, $stateParams.count).then(function(results) {
      if (results != null && results.length > 0) {
        $scope.data.questions = angular.copy(results)
        $scope.data.index = 0
      } else {
        $scope.data.questions = []
        $scope.data.index = -1
      }
    }, function() {

    })
  }
  $scope.noQuestions = function() {
    if ($scope.data.questions == null || $scope.data.questions == undefined || $scope.data.questions.length == 0) {
      return true
    }
    return false
  }

  $scope.validCurrentQuestion = function() {
    if ($scope.data.questions != null && $scope.data.questions.length > 0 && $scope.data.questions.length > $scope.data.index) {
      return true
    }
    return false
  }
  $scope.quizEnded = function() {
    if ($scope.data.questions != null && $scope.data.questions.length > 0 && $scope.data.questions.length == $scope.data.index) {
      return true
    }
    return false
  }
  $scope.hasMultipleAnswers = function() {
    var question = $scope.data.questions[$scope.data.index]
    if (question != null) {
      if (question.answers != null && question.answers.length > 1 && question.options != null && question.options.length > 0) {
        return true
      }

    }
    return false
  }
  $scope.hasSingleChoice = function() {
    var question = $scope.data.questions[$scope.data.index]
    if (question != null) {
      if (question.answers != null && question.answers.length == 1 && question.options != null && question.options.length > 0) {
        return true
      }
    }

    return false
  }
  $scope.needsUserEntry = function() {
    var question = $scope.data.questions[$scope.data.index]
    if (question != null) {
      if (question.answers != null && question.answers.length == 1 && (question.options == null || question.options == undefined || question.options.length == 0)) {
        return true
      }

    }

    return false
  }
  $scope.submit = function() {
    $scope.data.index = $scope.data.index + 1
  }
  $scope.startQuiz = function() {
    $state.go('quiz', {
      topicId: $stateParams.topicId,
      subTopicId: $stateParams.subTopicId,
      level: $scope.data.level,
      count: $scope.data.count
    })
  }

})
