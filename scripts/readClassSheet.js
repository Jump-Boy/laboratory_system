/* 功能：查看课表控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var home = angular.module("navigators.readClassSheet", []);
		/* 功能：查看课表控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    home.controller("readClassSheetCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector) {
	    	$scope.dtOptions = DTOptionsBuilder.newOptions()
				.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
				.withButtons([/*table导出按钮*/
					{
						extend: 'excel',
						title: '查看课表'
					},
					{
						extend: 'copy',
						title: '查看课表'
					}
				])
				.withOption('language', {/*提示语汉化*/
					"sProcessing": "处理中...",
					"sLengthMenu": "显示 _MENU_ 项结果",
					"sZeroRecords": "没有匹配结果",
					"sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
					"sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
					"sInfoFiltered": "(由 _MAX_ 项结果过滤)",
					"sSearch": "搜索:",
					"sEmptyTable": "表中数据为空",
					"sLoadingRecords": "载入中...",
					"oPaginate": {
						"sFirst": "首页",
						"sPrevious": "上页",
						"sNext": "下页",
						"sLast": "末页"
					}
				});
				
			/*****************************************调用缓存信息******************************************/	
			var userInfo = netStorage.getItem("userInfo");
	    	$scope.personInfo = {
	    		name: userInfo.name,
	    		userType: userInfo.userType,
	    		major: userInfo.major
	    	};
	    	
	    	/*****************************************初始化页面信息******************************************/
	    	init();
	    	function init() {
	    		/*获取数据进行页面初始化*/
	    		netConnector.get("/loadClassScheduleAction.do")
	    		            .then(function(res) {
	    		            	$scope.tableDatas = res.data.classSchedule;
	    		            })
	    		            .catch(function(err) {
	    		            	console.error(err);
	    		            });
	    	}
	    	
	    //ending controller
	    }]);
}());