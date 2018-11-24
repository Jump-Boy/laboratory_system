/* 功能：angular程序的主模块，引导全局依赖模块以及初始化全局配置
 * 思路：创建主模块，将全局变量初始化
 * 参数：document----HTML文档对象；angular----angular关键字；$----jQuery关键字
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function(document, angular, $) {
var myApp = angular.module("app", [
									 "ui.router",
									 'oc.lazyLoad',                  // ocLazyLoad
							         'ui.bootstrap',                  //Ui Bootstrap
							         'pascalprecht.translate',       // Angular Translate
							         'ngIdle',                       // Idle timer
							         'ngSanitize'					 // ngSanitize										
									]);
	//手动将主模块myApp挂在到document中								
	angular.element(document).ready(function() {  
		angular.bootstrap(document, ['app']);
	 });
	 //跨域请求带上cookie
	myApp.config(function ($httpProvider) {
		    $httpProvider.defaults.withCredentials = true;
	});
})(document, angular, $);