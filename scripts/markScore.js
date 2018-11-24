
/* 功能：随堂打分控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var home = angular.module("navigators.markScore", []);
		
		/* 功能：随堂打分控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    home.controller("markScoreCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector){
	    	/*table插件调用*/
	    	$scope.dtOptions = DTOptionsBuilder.newOptions()
				.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
				.withButtons([/*table导出按钮*/
					{
						extend: 'excel',
						title: '随堂打分'
					},
					{
						extend: 'copy',
						title: '随堂打分'
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
	    	
	    	/*初始化调用*/
	    	init();
	    	/*****************************************初始化页面信息******************************************/
	    	function init() {
	    		/*请求后台数据初始化*/
	    		netConnector.get("/loadUseForEveryMarkAction.do")
	    					.then(function(res) {
	    						$scope.tableDatas = res.data.everyMarkTable;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    	}
	    	
	    	/*****************************************打分按钮******************************************/
	    	/* 功能：点击打分按钮
			 * 思路：点击打分按钮，输入分数，再次点击后将数据发送到后台保存
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			/*输入规则：-1-100的数*/
	    	var reg = /(^[1-9]{1}$)|(^[1-9]{1}\d{1}$)|(^0{1}$)|(^(-1){1}$)|(^[1]0{2}$)/;
	    	$scope.markScore = function(item) {
	    		/*由关闭到打开，markSign默认值为false*/
	    		if(!item.markSign) {
	    			item.markSign = !(item.markSign);
	    			var formerScore = item.usualScore;
	    			
	    		} else {/*此时为打开状态*/
	    			if(reg.test(item.usualScore)) {
	    				item.markSign = !(item.markSign);
	    				var sendData = {
	    					usualScoreId: item.usualScoreId,
		    				usualScore: item.usualScore
		    			};
		    			netConnector.post("/markEveryAction.do", sendData)
		    						.then(function(res) {
		    							var isMark = res.data.markResult;
		    							if(isMark === "success") {
		    								
		    							}
		    						})
		    						.catch(function(err) {
		    							console.error(err);
		    						});
	    			} else {
	    				swal("只能输入-1~100的数字", "", "warning");
	    			}
	    		}
	    	};
	    	
	    //ending controller
	    }]);
}())