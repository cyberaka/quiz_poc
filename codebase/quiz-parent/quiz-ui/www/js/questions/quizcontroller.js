var questionsModule = angular.module('quiz.module.questions', ['quiz.resources.questions'])
questionsModule.controller('QuestionController', function($scope, $rootScope, $http, CONFIG, $state, $stateParams, QuestionService) {
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
            text: question.options[i].substring(3),
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
      question.userAnswers = new Array();
      if ($scope.needsUserEntry()) {
        question.userAnswers[0] = $scope.data.userentry;
        if ($scope.data.userentry == question.answers[0]) {
          $scope.data.score = $scope.data.score + 1
        }
      } else if ($scope.hasMultipleAnswers()) {
        var correctAnswerCount = 0
        var attempted = 0
        question.userAnswers = $scope.data.options;
        for (var j = 0; j < $scope.data.options.length; j++) {
          var currentOption = $scope.data.options[j]

          if (currentOption.checked == true) {
            attempted++
            for (var i = 0; i < question.answers.length; i++) {
              var currentAnswer = question.answers[i]
              // if (currentOption.text.startsWith(currentAnswer)) {
              //   correctAnswerCount++
              // }
              if (currentOption.value.startsWith(currentAnswer)) {
                correctAnswerCount++
              }
            }

          }
        }
        if (correctAnswerCount == question.answers.length && correctAnswerCount == attempted) {
          $scope.data.score++
        }
      } else {
        question.userAnswers[0] = $scope.data.singleChoice;
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
  $scope.publishScore = function() {
    console.log(JSON.stringify($scope.data.questions));
    console.log(JSON.stringify($rootScope.currentUser));
    // use $.param jQuery function to serialize data from JSON
    var data = JSON.stringify($scope.data.questions);

    var config = {
//        headers : {
//            'Content-Type': 'application/json;'
//        }
    }

    $http.post(CONFIG.url + 'quiz/' + $rootScope.currentUser.userId + '/', data, config)
    .success(function (data, status, headers, config) {
        //$scope.PostDataResponse = data;
        alert("Published Successfully! Close Window.");
        $state.go('topics',{})
    })
    .error(function (data, status, header, config) {
        $scope.ResponseDetails = "Data: " + data +
            "<hr />status: " + status +
            "<hr />headers: " + header +
            "<hr />config: " + config;
    });
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
