questionsModule.service('QuestionService', function(Questions, $q) {
  var service = {
    getQuestions: function(theTopicId, theSubTopicId, theLevel, theCount) {
      var payload = {}
      var deferred = $q.defer()
      var get = Questions.query({
        topicId: theTopicId,
        subTopicId: theSubTopicId,
        level: theLevel,
        count: theCount
      }, {}, function() {
      //  console.log(JSON.stringify(get))
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
