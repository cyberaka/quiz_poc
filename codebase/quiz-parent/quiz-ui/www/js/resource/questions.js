angular.module('quiz.resources.questions', ['ngResource', 'quiz.constants'])
  .service('Questions', function($resource, CONFIG) {
    return $resource(CONFIG.url + 'quiz/:topicId/:subTopicId/:level/:count', {
      topicId: '@topicId',
      subTopicId: '@subTopicId',
      level: '@level',
      count: '@count'
    }, {
      query: {
        method: 'GET',
        isArray: true
      }
    })
  })
