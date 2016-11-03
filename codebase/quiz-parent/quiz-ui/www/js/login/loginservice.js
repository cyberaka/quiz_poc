loginModule.service('LoginService', function(Logins, $q) {
  var service = {
    login: function(theUserName, theUserPassword) {
      var payload = {}
      var deferred = $q.defer()
      var get = Logins.get({
        userName: theUserName,
        userPassword: theUserPassword
      }, {}, function() {
        console.log("Resolved >> " + JSON.stringify(get))
        deferred.resolve(get)
      }, function() {
        console.log("Rejecting >> " + JSON.stringify(get))
        deferred.reject()
      })

      return deferred.promise
    }
  }

  return service
})
