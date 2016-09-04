//alert(location.host + "\n" + location.hostname + "\n" + location.href)
angular.module('quiz.constants',[])
		.constant('CONFIG',{
		url: 'http://' + location.hostname + ':7070/'
		});
