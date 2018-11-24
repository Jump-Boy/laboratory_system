/* 功能：角色管理控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators.roleManager", []);
	
		/* 功能：角色管理控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("roleManagerCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector) {
	    $scope.dtOptions = DTOptionsBuilder.newOptions()
				.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
				.withButtons([/*table导出按钮*/
					{
						extend: 'excel',
						title: '角色管理'
					},
					{
						extend: 'copy',
						title: '角色管理'
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
	    		//请求后台数据
	    		netConnector.get("/loadAllRolesAction.do")
	    					.then(function(res) {
	    						$scope.tableDatas = res.data.roles;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    		
	    	}
	    	
	    	/*****************************************打开添加单个角色******************************************/
			/* 功能:打开添加角色
			 * 思路：打开添加弹框，表单置于空对象
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.submitted = false;
	    	$scope.addRole = function() {
	    		$scope.submitted = false;
	    		$("#addRole").modal("show");
	    		$scope.addRoleData = {};
	    	};
	    	
	    	/*****************************************保存添加单个角色******************************************/
			/* 功能：保存添加单个角色
			 * 思路：页面表单信息验证通过后，将添加的信息发送到后台，对保存结果进行反馈提示
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.addRoleSave = function() {
	    		/*自定义表单验证标志*/
	    		$scope.submitted = true;
	    		/*表单验证成功后执行*/
	    		if($scope.addRoleForm.$valid) {
	    			/*添加角色信息*/
	    			var sendAddData = {
		    			name: $scope.addRoleData.name,
		    			id: $scope.addRoleData.id,
		    			userType: $scope.addRoleData.userType
		    		};
		    		
		    		netConnector.post("/addRoleAction.do", sendAddData)
		    					.then(function(res) {
		    						console.log("resAdd", res);
		    						var isAdd = res.data.addResult;
		    						if(isAdd === "success") {
		    							$scope.addRoleData = {};
		    							$("#addRole").modal("hide");
		    							swal("添加成功！", "", "success");
		    							/*添加成功后更新页面角色信息*/
		    							init();
		    						} else{
		    							swal("添加失败！", "", "error");
		    						}
		    						
		    					})
		    					.catch(function(err) {
		    						swal("添加失败！", "", "error");
		    					});
	    		}
	    		
	    	};
	    	
	    	
	    	/*****************************************重置角色密码******************************************/
			/* 功能：重置角色密码
			 * 思路：获取相应的角色信息，对用户的密码进行重置
			 * 参数：role----对应的角色
			 * 作者：liao
			 * 修改时间：2018-05-23
			 */
			$scope.resetRole = function(role) {
				 swal({
                        title: "您确定重置角色密码信息吗?",
                        text: "您将重置角色密码为角色学号或编号!",
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
                    			id: role.id,
                    			userType: role.userType
                    		};
                    		netConnector.post("/resetPasswordAction.do", sendData)
                    					.then(function(res) {
                    						/*修改成功*/
	                    					var resetSign = res.data.resetResult;
	                    					if( resetSign === "success"){
	                    						swal("重置成功", "", "success");
	                    					} else{
	                    						swal("重置失败", "系统出错", "error");
	                    					}
                    					})
                    					.catch(function(err) {
                    						swal("重置失败", "系统出错", "error");
                    						console.error(err);
                    					});
                    	}
                    });
			};

	    	/*****************************************删除角色******************************************/
			/* 功能：删除角色
			 * 思路：删除角色，对删除结果进行反馈提示，并更新页面信息
			 * 参数：role----当前角色记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.deleteRole = function(role) {
	    		var deleteRole = {
	    			id: role.id,
	    			userType: role.userType
	    		};
	    		netConnector.post("/deleteRoleAction.do", deleteRole)
	    					.then(function(res) {
	    						console.log(res);
	    						var isDelete = res.data.deleteResult;
	    						if(isDelete === "success") {
	    							init();
	    							swal("删除成功！", "", "success");
	    							
	    						} else {
	    							swal("删除失败！", "", "error");
	    						}
	    					})
	    					.catch(function(err) {
	    						swal("删除失败！", "", "error");
	    						console.error(err);
	    					});
	    	};
	    	
	    	/*****************************************批量添加角色******************************************/
	    	
	    	/*打开批量添加*/
	    	$scope.addRoles = function() {
	    		$('#addRoles').modal("show");
	    	};
	    	
	    	/*****************************************保存批量添加信息******************************************/
			/* 功能：保存批量添加信息
			 * 思路：将控件获取的Excel数据发送到后台进行保存，根据保存结果进行页面信息反馈
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.addFile = function() {
	    		//中有当上传的文件可以转化为数组数据的时候才可以发送到后端
	    		if($scope.excelData.length !== 0) {
	    			netConnector.post("/addRolesAction.do", {data: $scope.excelData})
	    						.then(function(res) {
	    							console.log('res', res);
	    							if(res.data.addResult === "success") {
	    								swal("上传成功", "", "success");
	    								$('#addRoles').modal("hide");
	    								/*添加成功后，更新页面角色信息*/
	    								init();
	    							} else {
	    								swal("上传失败！", "", "error");
	    							}
	    							
	    						})
	    						.catch(function(err) {
	    							swal("上传失败", "", "error");
	    							console.log(err);
	    						});
	    		} else {
	    			swal("请确认上传的文件格式（xlsx，xls）", "", "error");
	    		}
	    	};
	    	/*****************************************批量上传Excel表格信息******************************************/
			/* 功能：将Excel信息转化为JSON格式数据
			 * 思路：判断是否为Excel文件，使用js文件读取对象读取Excel表格数据，利用xlsx插件将文件转化为JSON数据，送到 保存批量添加信息 函数
			 * 参数：e----当前触发事件对象
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	document.getElementById("addFileData").addEventListener("change", function(e) {
	    		  /*输入控件的文件对象*/
				  var fileObj = e.target.files[0];
				  /*上传的文件是excel表格才进行数据的提取*/
				  if(fileObj.name.split(".")[1] === "xlsx" || fileObj.name.split(".")[1] === "xls"){
				  		  var rABS = true;
					  	  var reader = new FileReader();
						  reader.onload = function(e) {
						  		/*文件内容*/
							    var data = e.target.result;
							    /*当文件不是二进制的时，就以8进制的形式进行存储*/
							    if(!rABS){
							    	data = new Uint8Array(data);
							    } 
							    var workbook = XLSX.read(data, {type: rABS ? 'binary' : 'array'});
							    /*将文件内容转化为JSON*/
							    var arrayData = XLSX.utils.sheet_to_json(workbook.Sheets.Sheet1);
							    $scope.excelData = arrayData;
							    console.log(arrayData);
						  };
						  /*读取文件*/
						  if(rABS){
						  	/*二进制*/
						  	reader.readAsBinaryString(fileObj);
						  } else {
						  	reader.readAsArrayBuffer(fileObj);
						  }
				  } else {/*不是Excel文件，发送空数据*/
				  	      $scope.excelData = [];
				  }
	    	});
	    	
	    //ending controller
	    }]);
}())