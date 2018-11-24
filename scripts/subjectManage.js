/* 功能：课程管理控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators.subjectManage", []);
	
		/* 功能：课程管理控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块，SweetAlert----弹框对象
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("subjectManageCtrl", ["$scope", "netStorage", "DTOptionsBuilder", "netConnector", "SweetAlert",  function($scope, netStorage, DTOptionsBuilder, netConnector, SweetAlert) {
	    	$scope.dtOptions = DTOptionsBuilder.newOptions()
								.withDOM('<"html5buttons"B>Tfgtip')/*table布局*/
								.withButtons([/*table导出按钮*/
									{
										extend: 'excel',
										title: '课程管理'
									},
									{
										extend: 'copy',
										title: '课程管理'
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
	    		netConnector.get("/loadAllCoursesAction.do")
	    					.then(function(res) {
	    						$scope.tableDatas = res.data.courses;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    	}
	    	
	    	/*****************************************打开添加框******************************************/
			/* 功能:打开添加框，初始化表单信息
			 * 思路：初始化表单对象，再打开弹框
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.addSubject = {};
	    	$scope.submitted = false;
	    	$scope.addCourse = function() {
	    		$scope.addSubject = {
	    			courseTeacher: userInfo.name
	    		};
	    		$scope.submitted = false;
	    		$("#addCourse").modal("show");
	    	};
	    	
	    	/*****************************************保存添加课程信息******************************************/
			/* 功能:保存课程信息
			 * 思路：询问是否添加课程信息，确认后发送到后台保存数据
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.saveAdd = function() {
	    		$scope.submitted = true;
	    		if($scope.addSubjectForm.$valid) {
	    			SweetAlert.swal({
	    				title: "你确认添加吗？",
	    				text: "你将添加这门课程",
	    				type: "warning",
	    				showCancelButton: true,
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        closeOnConfirm: false,
                        closeOnCancel: true
	    				
	    			},
	    			function(isConfirm) {
	    				if(isConfirm) {
	    					var sendAddData = {
	    						courseName: $scope.addSubject.courseName,
	    						attribute: $scope.addSubject.courseAttribute,
	    						credit: $scope.addSubject.courseCredit,
	    						studySemester: $scope.addSubject.majorTerm,
	    						classTime: $scope.addSubject.courseTime,
	    						location: $scope.addSubject.classroom,
	    						classWeeks: $scope.addSubject.classWeeks,
	    						teaName: $scope.addSubject.courseTeacher,
	    						limitNum: $scope.addSubject.coursePopulation,
	    						majorScope: $scope.addSubject.majorScope
	    					};
	    					console.log("sendAddData", sendAddData);
	    					netConnector.post("/addCourseAction.do", sendAddData)
	    								.then(function(res) {
	    									var isAdd = res.data.addResult;
	    									if(isAdd === "success") {
	    										$("#addCourse").modal("hide");
	    										swal("添加成功", "", "success");
	    										init();
	    									} else{
	    										swal("添加失败", "", "error");
	    									}
	    								})
	    								.catch(function(err) {
	    									swal("添加失败", "", "error");
	    									console.error(err);
	    								});
	    				}
	    			}
	    			);
	    		}
	    	};
	    	/*****************************************取消添加******************************************/
	    	$scope.cancelAdd = function() {
	    		$("#addCourse").modal("hide");
	    		$scope.submitted = false;
	    	};
	    	
	    	/*****************************************查看明细******************************************/
			/* 功能:查看已选学生信息明细
			 * 思路：通过ID向后台获取对应的数据
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.readDetail = function(item) {
	    		$("#readDetail").modal("show");
	    		$scope.deleteCourse = item;
	    		var sendData = {
	    			courseId: item.courseId
	    		};
	    		netConnector.post("/loadSelectedStuAction.do", sendData)
	    					.then(function(res) {
	    						$scope.detailTableDatas = res.data.selectedStuTable;
	    					})
	    					.catch(function(err) {
	    						console.error(err);
	    					});
	    	};
	    	
	    	/*****************************************删除已选课程的学生******************************************/
			/* 功能:删除课程已选的学生
			 * 思路：通过学生和课程ID向后台发送删除信息，减少http请求，前端页面原数据查找剔除
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.deleteSomePerson = function(item) {
	    		var sendDeleteData = {
	    			stuId: item.stuId,
	    			courseId: $scope.deleteCourse.courseId
	    		};
	    		netConnector.post("/deleteSelectedStuAction.do", sendDeleteData)
	    					.then(function(res) {
	    						if(res.data.deleteResult === "success") {
	    							swal("删除成功！", "", "success");
	    							$scope.detailTableDatas.forEach(function(val, index, arr) {
		    							if(item.stuId === val.stuId) {
		    								arr.splice(index, 1);
		    							}
	    							});
	    							init();
	    						} else {
	    							swal("删除失败！", "", "error");
	    						}
	    					})
	    					.catch(function(err) {
	    						swal("删除失败！", "", "error");
	    						console.error(err);
	    					});
	    	};
	    	
	    	/*****************************************初始化修改表单信息******************************************/
			/* 功能:初始化修改表单信息
			 * 思路：从当前记录获取数据，放置表单上
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.modify = function(item) {
	    		$("#modifyCourse").modal("show");
	    		$scope.submitted = false;
	    		
	    		$scope.modifySubject = {
	    			courseName: item.courseName,
					courseAttribute: item.attribute,
					courseCredit: item.credit,
					majorTerm: item.studySemester,
					courseTime: item.classTime,
					classWeeks: item.classWeeks,
					classroom: item.location,
					courseTeacher: item.teaName,
					coursePopulation: item.limitNum,
					majorScope: item.majorScope,
					courseId: item.courseId
	    		};
	    	};
	    	
	    	/*****************************************保存修改课程******************************************/
			/* 功能:保存修改课程信息
			 * 思路：询问是否修改，确认后将修改后的信息发送到后台保存，根据返回的信息提示是否保存成功
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.saveModify = function() {
	    		$scope.submitted = true;
	    		
	    		if($scope.modifySubjectForm.$valid) {
	    			SweetAlert.swal({
	    				title: "你确认修改吗？",
	    				text: "你将修改这门课程",
	    				type: "warning",
	    				showCancelButton: true,
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        closeOnConfirm: false,
                        closeOnCancel: true
	    				
	    			},
	    			function(isConfirm) {
	    				if(isConfirm) {
	    					var sendModifyData = {
	    						courseName: $scope.modifySubject.courseName,
	    						attribute: $scope.modifySubject.courseAttribute,
	    						credit: $scope.modifySubject.courseCredit,
	    						studySemester: $scope.modifySubject.majorTerm,
	    						classWeeks: $scope.modifySubject.classWeeks,
	    						classTime: $scope.modifySubject.courseTime,
	    						location: $scope.modifySubject.classroom,
	    						name: $scope.modifySubject.courseTeacher,
	    						limitNum: $scope.modifySubject.coursePopulation,
	    						majorScope: $scope.modifySubject.majorScope,
	    						courseId: $scope.modifySubject.courseId
	    					};
	    					console.log("sendAddData", sendModifyData);
	    					netConnector.post("/modifyCourseAction.do", sendModifyData)
	    								.then(function(res) {
	    									var isModify = res.data.modifyResult; 
	    									if(isModify === "success") {
	    										$("#modifyCourse").modal("hide");
	    										swal("修改成功", "", "success");
	    										init();
	    									} else{
	    										swal("抱歉，您没有权限修改！", "", "error");
	    									}
	    								})
	    								.catch(function(err) {
	    									swal("修改失败", "", "error");
	    									console.error(err);
	    								});
	    				}
	    			}
	    			);
	    		}
	    		
	    		
	    	};
	    	/*****************************************取消修改******************************************/
	    	$scope.cancelSave = function() {
	    		$("#modifyCourse").modal("hide");
	    	};
	    	
	    	/*****************************************删除课程******************************************/
			/* 功能:删除课程
			 * 思路：根据当前删除记录的课程id删除数据
			 * 参数：item----当前操作的记录
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
	    	$scope.deleteItem = function(item) {
	    		var sendDeleteData = {
	    			courseId: item.courseId
	    		};
	    		netConnector.post("/deleteCourseAction.do", sendDeleteData)
	    					.then(function(res) {
	    						if(res.data.deleteResult === "success") {
	    							swal("删除成功！","", "success");
	    							$scope.tableDatas.forEach(function(val, index, arr) {
						    			if(item.courseId === val.courseId) {
						    				arr.splice(index, 1);
						    			}
						    		});
	    						} else {
	    							swal("删除失败！","", "error");
	    						}
	    					})
	    					.catch(function(err) {
	    						swal("删除失败！","", "error");
	    					});
	    	};
	    	
	    //ending controller
	    }]);
}())