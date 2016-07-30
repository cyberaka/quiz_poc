subTopicsModule.service('SubTopicService', function(SubTopics, $q) {
  var service = {
    getSubTopics: function(theTopicId) {
      var payload = {}
      var deferred = $q.defer()
      var get = SubTopics.query({
        topicId: theTopicId
      }, {}, function() {
        //console.log(JSON.stringify(get))
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
