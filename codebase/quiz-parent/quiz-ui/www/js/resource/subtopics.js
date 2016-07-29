angular.module('quiz.resources.subtopics', ['ngResource', 'quiz.constants'])
  .service('SubTopics', function($resource, CONFIG) {
    return $resource(CONFIG.url + 'subtopics/:topicId', {
      topicId: '@topicId'
    }, {
      query: {
        method: 'GET',
        isArray: true
      }
    })
  })
