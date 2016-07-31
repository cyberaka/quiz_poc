var questionsModule = angular.module('quiz.module.questions', ['quiz.resources.questions'])
questionsModule.controller('QuestionController', function($scope, $state, $stateParams, QuestionService) {
  $scope.data = {
    questions: [],
    index: -1,
    count: 0,
    score: 0,
    level: 2,
    options: [],
    userentry: '',
    singleChoice: ''
  }

  if ($state.current.name == "quizsettings") {
    $scope.data.level = 2
    $scope.data.count = 10
  } else if ($state.current.name == "quiz") {
    var theTopicId = $stateParams.topicId
    var subTopicId = $stateParams.subTopicId
    QuestionService.getQuestions($stateParams.topicId, $stateParams.subTopicId, $stateParams.level, $stateParams.count).then(function(results) {
      if (results != null && results.length > 0) {
        $scope.data.questions = angular.copy(results)
        $scope.data.index = 0
        $scope.data.level = $stateParams.level
        $scope.data.count = $stateParams.count
        $scope.configureOptions()
      } else {
        $scope.data.questions = []
        $scope.data.index = -1
      }
    }, function() {

    })
  }

  $scope.configureOptions = function() {
    $scope.data.userentry = ''
    $scope.data.singleChoice = ''
    $scope.data.options = []
    if ($scope.validCurrentQuestion()) {
      var question = $scope.data.questions[$scope.data.index]
      if (question != null && question.options != null && question.options.length > 0) {
        for (var i = 0; i < question.options.length; i++) {
          $scope.data.options[i] = {
            text: question.options[i],
            value: question.options[i].charAt(0),
            checked: false

          }
        }
      } else {
        $scope.data.userentry = ''
        $scope.data.singleChoice = ''
        $scope.data.options = []
      }
      console.log(JSON.stringify(question.answers))
      // console.log(JSON.stringify($scope.data.options))
      // console.log(JSON.stringify(question.options.length))
      // console.log(JSON.stringify(question.options))
    }
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
  $scope.evaluate = function() {
    var question = $scope.data.questions[$scope.data.index]
    if (question != null) {
      if ($scope.needsUserEntry()) {
        if ($scope.data.userentry == question.answers[0]) {
          $scope.data.score = $scope.data.score + 1
        }
      } else if ($scope.hasMultipleAnswers()) {
        var correctAnswerCount = 0
        var attempted = 0

        for (var j = 0; j < $scope.data.options.length; j++) {
          var currentOption = $scope.data.options[j]
          if (currentOption.checked == true) {
            attempted++
            for (var i = 0; i < question.answers.length; i++) {
              var currentAnswer = question.answers[i]
              if (currentOption.text.startsWith(currentAnswer)) {
                correctAnswerCount++
              }
            }

          }
        }
        if (correctAnswerCount == question.answers.length && correctAnswerCount == attempted) {
          $scope.data.score++
        }
      } else {
        if ($scope.data.singleChoice == question.answers[0]) {
          $scope.data.score = $scope.data.score + 1
        }
      }
    }
  }
  $scope.submit = function() {
    $scope.evaluate()
    $scope.data.index = $scope.data.index + 1
    $scope.configureOptions()
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
