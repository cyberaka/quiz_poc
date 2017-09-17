angular.module('quiz.resources.logins', ['ngResource', 'quiz.constants'])
  .service('Logins', function($resource, CONFIG) {
    return $resource(CONFIG.url + 'login', {}, {
      userName: '@userName',
      userPassword: '@userPassword'
    }, {
    })
  })

