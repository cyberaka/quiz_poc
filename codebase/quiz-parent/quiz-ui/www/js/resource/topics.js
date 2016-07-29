angular.module('quiz.resources.topics',['ngResource','quiz.constants'])
		.service('Topics', function($resource,CONFIG) {
			return $resource(CONFIG.url+'topics',{},{
				query:{
					method: 'GET',
					isArray: true
				}
			})
	 })
