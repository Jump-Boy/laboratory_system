/* 功能：角色管理控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators.scoreManage", []);
		
		/* 功能：角色管理控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("scoreManageCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector) {
	    	$scope.dtOptions = DTOptionsBuilder.newOptions()
				.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
				.withButtons([/*table导出按钮*/
					{
						extend: 'excel',
						title: '成绩管理 '
					},
					{
						extend: 'copy',
						title: '成绩管理'
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
	    		netConnector.get("/loadUseForTotalMarkAction.do")
	    					.then(function(res) {
	    						$scope.tableDatas = res.data.totalTable;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    	}
	    	
	    	/*****************************************通过进行年级过滤******************************************/
			/* 功能:查询对应年级的学生
			 * 思路：发送查询条件，获取后台相关数据
			 * 参数：params----查询条件
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.classFilter = function(params) {
	    		var sendCalss = {
	    			groupGrade: params
	    		};
	    		netConnector.post("/loadGroupUseForTotalAction.do", sendCalss)
	    					.then(function(res) {
	    						$scope.tableDatas = res.data.totalTable;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    		
	    	};
	    	
	    	/*****************************************成绩输入******************************************/
			/* 功能:输入学生的最终成绩
			 * 思路：自定义开关标志，当打开时，输入成绩，当输入成绩的数字规则符合条件才会发送到后端保存
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	/*输入规则：0-100的数*/
	    	var reg = /(^[1-9]{1}$)|(^[1-9]{1}\d{1}$)|(^0{1}$)|(^[1]0{2}$)/;
	    	$scope.markScore = function(item) {
	    		/*由关闭到打开，markShow默认值为false*/
	    		if(!item.markShow) {
	    			item.markShow = !(item.markShow);
	    			var formerScore = item.totalScore;
	    			
	    		} else {/*此时为打开状态*/
	    			if(reg.test(item.totalScore)) {
	    				item.markShow = !(item.markShow);
	    				var sendScore = {
	    					totalScoreId: item.totalScoreId,
		    				totalScore: item.totalScore
		    			};
		    			netConnector.post("/markTotalAction.do", sendScore)
		    						.then(function(res) {
		    							console.log("success", res);
		    						})
		    						.catch(function(err) {
		    							console.error(err);
		    						});
	    				
	    			} else {
	    				swal("只能输入0~100的数字", "", "warning");
	    			}
	    		}
	       };
	    
	    //ending controller
	    }]);
}())