topicsModule.service('TopicService', function(Topics, $q) {
  var service = {
    getTopics: function() {
      var payload = {}
      var deferred = $q.defer()
      var get = Topics.query({}, {}, function() {

        console.log(JSON.stringify(get))
        deferred.resolve(get)
      }, function() {
        console.log(JSON.stringify(get))
        deferred.reject()
      })

      return deferred.promise
    }
  }

  return service
})
