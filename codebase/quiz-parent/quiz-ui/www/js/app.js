// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('quiz', ['ionic','quiz.constants','quiz.module.login','quiz.module.topics',
        'quiz.module.subtopics','quiz.module.questions'])
.config(function($urlRouterProvider,$locationProvider,$stateProvider,$windowProvider){
$urlRouterProvider.otherwise('/login')
  $stateProvider
        .state('login', {
            url: '/login',
            templateUrl: 'js/login/login.html',
            controller: 'LoginController'
        })
        .state('topics', {
            url: '/topics',
            templateUrl: 'js/topics/topic.html',
            controller: 'TopicController'
        })
        .state('subtopics', {
            url: '/subtopics/:topicId',
            templateUrl: 'js/subtopics/subtopics.html',
            controller: 'SubTopicController'
        })
        .state('quizsettings', {
            url: '/quiz/:topicId/:subTopicId',
            templateUrl: 'js/questions/quizsettings.html',
            controller: 'QuestionController'
        })
        .state('quiz', {
            url: '/quiz/:topicId/:subTopicId/:level/:count',
            templateUrl: 'js/questions/quiz.html',
            controller: 'QuestionController'
        })

})
.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    if(window.cordova && window.cordova.plugins.Keyboard) {
      // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
      // for form inputs)
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);

      // Don't remove this line unless you know what you are doing. It stops the viewport
      // from snapping when text inputs are focused. Ionic handles this internally for
      // a much nicer keyboard experience.
      cordova.plugins.Keyboard.disableScroll(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }
  });
})
