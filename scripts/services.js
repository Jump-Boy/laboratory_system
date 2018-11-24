
/* 功能：自定义全局服务变量
 * 思路：引入主模块，在主模块的基础上设立全局服务变量
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	'use strict';

	var app = angular.module('app');
	
	/* 功能：定义网络通讯服务变量netConnector
	 * 思路：对常用的网络通讯方法进行重新的封装，设定通讯方法的形参变量
	 * 参数：$http----angular的http服务接口
	 * 作者：liao
	 * 修改时间：2018-05-04
	 */
	app.factory('netConnector', ['$http',function($http) {
			var server = "http://127.0.0.1:8090";
			var service = {};
			service.get = function(api, params) {
				return $http({
					method: 'GET',
					url: server + api,
					params: params
				});
			};

			service.post = function(api, data, headers) {
				return $http({
					method: 'POST',
					url: server + api,
					data: data,
					headers: headers
				});
			};

			service.put = function(api, data) {
				return $http({
					method: 'PUT',
					url: server + api,
					data: data
				});
			};
			return service;
		}
	]);
	
	/* 功能：定义浏览器缓存服务接口
	 * 思路：对sessionStorage的接口进行重新的封装，缓存只能缓存字符串，需要转化
	 * 参数：$window----angular的window服务
	 * 作者：liao
	 * 修改时间：2018-05-04
	 */
	app.factory("netStorage", ["$window",function($window) {
		var service = {};
		/*添加缓存*/
		service.setItem = function(itemName, itemValue) {
			return $window.sessionStorage.setItem(itemName, JSON.stringify(itemValue));
		};
		/*调用缓存*/
		service.getItem = function(itemName) {
			return JSON.parse($window.sessionStorage.getItem(itemName));
		};
		/*删除缓存*/
		service.removeItem = function(itemName) {
			return $window.sessionStorage.removeItem(itemName);
		};
		return service;
	}]);
}());