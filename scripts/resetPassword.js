/* 功能：修改密码控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var home = angular.module("navigators.resetPassword", []);
		/* 功能：修改密码控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    home.controller("resetPasswordCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector) {
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
				
				/*****************************************切换到编辑页面******************************************/
			    /* 功能：切换到编辑页面
				 * 思路：点击修改密码，切换到编辑页面
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
				$scope.submitted = false;
				$scope.toggleSign = false;
				$scope.toggle = function() {
					/*表单验证标志*/
					$scope.submitted = false;
					$scope.confirmSign = false;
					$scope.passwordError = false;
					$scope.modifyData = {
		    			studentID: userInfo.id
		    		};
					$scope.toggleSign = !($scope.toggleSign);
				};
				
	    		/*****************************************确定修改密码******************************************/
			    /* 功能：保存修改后的密码
				 * 思路：页面表单信息验证成功后，询问是否修改密码，确认发送到后台保存，依据前后端约定规则显示是否修改成功
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
	    		/*密码规则：密码由六位以上的数字或者字母组成*/
	    		var reg = /[0-9a-zA-Z]{5,}/g
	    		$scope.modifyPassword = function() {
	    			/*自定义表单验证标志*/
	    			$scope.submitted = true;
	    			$scope.passwordError = false;
	    			
	    			if($scope.modifyForm.$valid) {
	    				/*密码规则判断标志*/
	    				$scope.passwordStyleError = true;
	    				/*符合密码规则执行*/
	    				if(reg.test($scope.modifyData.newPassword)) {
	    					$scope.passwordStyleError = false;
		    				var sendModifyData = {
		    					password: $scope.modifyData.oldPassword,
		    					newPassword: $scope.modifyData.newPassword
		    				};
		    				/*确认新密码都正确才发起修改请求*/
		    				if($scope.modifyData.newPassword === $scope.modifyData.newPasswordConfirm) {
		    					$scope.confirmSign = false;
		    					netConnector.post("/modifyPwdAction.do", sendModifyData)
		    								.then(function(res) {
		    									var modifySign = res.data.modifyResult;
		    									if(modifySign) {
		    										swal("修改成功！", "", "success");
		    										$scope.toggleSign = false;
		    									} else{
		    										$scope.passwordError = true;
		    										swal("修改失败！", "", "error");
		    									}
		    								})
		    								.catch(function(err){
		    									console.error(err);
		    									swal("修改失败！", "", "error");
		    								});
		    					
		    				} else {
		    					$scope.confirmSign = true;
		    				}
		    				
		    			}	
	    			}
	    		};
	    		
		    	/*****************************************重置按钮******************************************/
			    /* 功能：重置按钮
				 * 思路：将判断标志复原，信息复原
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
	    		$scope.reset = function() {
	    			$scope.submitted = false;
	    			$scope.confirmSign = false;
	    			$scope.passwordError = false;
	    			$scope.passwordStyleError = false;
	    			$scope.modifyData = {
		    			studentID: userInfo.id
		    		};
	    		}
	    	
	    //ending controller
	    }]);
}())