/* 功能：开始选课控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var home = angular.module("navigators.selectSubject", []);
		
		/* 功能：开始选课控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块，SweetAlert----弹框对象
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    home.controller("selectSubjectCtrl", ["$scope", "netStorage", "DTOptionsBuilder", "netConnector", "SweetAlert",  function($scope, netStorage, DTOptionsBuilder, netConnector, SweetAlert) {
	    	$scope.dtOptions = DTOptionsBuilder.newOptions()
				.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
				.withButtons([/*table导出按钮*/
					{
						extend: 'excel',
						title: '课程信息'
					},
					{
						extend: 'copy',
						title: '课程信息'
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
	    		netConnector.get("/startCourseAction.do")
	    					.then(function(res) {
	    						console.log("courses", res);
	    						$scope.tableDatas = res.data.courses;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    	}
	    	
	    	/*****************************************选修按钮******************************************/
			/* 功能:学生选择课程
			 * 思路：点击选修按钮，询问是否选择，确认后选定课程，根据后台结果反馈是否选修成功
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.selectedItem = function(item) {
	    		/*询问是否选修当前课程*/
	    		 SweetAlert.swal({
                        title: "你确定选择吗?",
                        text: "你将选择此课程!",
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        closeOnConfirm: false,
                        closeOnCancel: true
                    },
                    function(isConfirm) {
                    	if(isConfirm) {
                    		var sendData = {
                    			userId: userInfo.id,
                    			courseId: item.courseId
                    		};
                    		netConnector.post("/selectCourseAction.do", sendData)
                    					.then(function(res) {
                    						/*选择成功*/
	                    					var isSelected = res.data.selectResult;
	                    					if(isSelected === "success"){
	                    						item.selectFlag = true;
	                    						init();
	                    						swal("选择成功", "", "success");
	                    					} else{
	                    						swal("选择失败", "如有疑问请联系管理员", "error");
	                    					}
                    					})
                    					.catch(function(err) {
                    						swal("选择失败", "如有疑问请联系管理员", "error");
                    						console.error(err);
                    					});
                    	}
                    });
	    	};
	    	
	    //ending controller
	    }]);
}())