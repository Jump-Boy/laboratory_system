/* 功能：个人信息控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators.personInfo", []);
	
		/* 功能：个人信息控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netStorage----自定义缓存服务，netConnector----自定义前后端交互服务, DTOptionsBuilder----table插件模块
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("personInfoCtrl", ["$scope", "netStorage","DTOptionsBuilder", "netConnector", function($scope, netStorage, DTOptionsBuilder, netConnector){
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
		    	$scope.readInfo = {
		    		name: userInfo.name
		    	};
		    	init();
		    	function init() {
		    		netConnector.get("/loadPsInfoAction.do")
		    					.then(function(res) {
		    						$scope.readInfo ={
						    			name: res.data.name || '' ,
						    			sex: res.data.sex || '' ,
						    			studentID: res.data.studentID || '' ,
						    			class: res.data.className || '' ,
						    			major: res.data.major || '' ,
						    			educationLevel: res.data.educationLevel || '' ,
						    			province: res.data.province || '' ,
						    			tele: res.data.telephone || '' ,
						    			picURL: res.data.picURL || '' 
						    		};
		    						//$scope.toggleSign = !($scope.toggleSign);
		    					})
		    					.catch(function(err) {
		    						console.error(err);
		    						$scope.toggleSign =　false;
		    					});
		    	}
				
				/*编辑区域的图片预览元素对象*/
				var writeInfoPic = document.getElementById('previewPic');
				
				/*****************************************切换到编辑区域******************************************/
		    	/* 功能：点击编辑图标,切换到个人信息编辑区域
				 * 思路：利用自定义切换标志进行页面视图的切换,当切换的时候将展示区域的信息保留到编辑区域,$scope.toggleSign =　false代表当前页面是展示查看个人信息页面
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
				$scope.toggleSign =　false;
				$scope.toggle = function() {
					$scope.toggleSign = !($scope.toggleSign);
					/*将编辑信息单独封装成为一个对象*/
					$scope.writeInfo = {
			    		name: $scope.readInfo.name,
			    		sex: $scope.readInfo.sex,
						studentID: $scope.readInfo.studentID,
						class: $scope.readInfo.class,
						major: $scope.readInfo.major, 
						educationLevel: $scope.readInfo.educationLevel,
						province: $scope.readInfo.province,
						tele: $scope.readInfo.tele, 
						picURL: $scope.readInfo.picURL
			    	};
			    	/*将当前的图片放置到编辑图片预览元素上*/
			    	writeInfoPic.src = $scope.writeInfo.picURL;
				};
	    		
	    	
		    	 //保存修改
		    /*   $scope.writeInfo = {
		    		name: $scope.readInfo.name,
		    		sex: $scope.readInfo.sex,
					studentID: $scope.readInfo.studentID,
					class: $scope.readInfo.class,
					major: $scope.readInfo.major, 
					educationLevel: $scope.readInfo.educationLevel,
					province: $scope.readInfo.province,
					tele: $scope.readInfo.tele,
					picURL: $scope.readInfo.picURL
		    	};*/
		    	
		    	/*****************************************图片上传及预览******************************************/
		    	/* 功能：完成图片文件的上传以及预览
				 * 思路：使用原生js DOM操作和文件操作对象,允许小于1.5M的图片上传,
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
				
		    	$scope.uploadPic = function() {
		    		console.log("图片上传了");
		    		/*图片上传控件元素对象*/
		    		var picObjFile = document.getElementById("uploadPic").files[0];
		    		/*小于1.5M*/
		    		if(picObjFile.size <= 1572864) {
		    			var reader = new FileReader();
			    		reader.readAsDataURL(picObjFile);
			    		reader.addEventListener("load", function(event) {
			    			writeInfoPic.src = reader.result;
			    			console.log("图片地址", reader.result);
			    		});
		    		} else {
		    			alert("图片大小不能超过1.5M");
		    		}
		    		
		    	};
		    	var picObj = document.getElementById("uploadPic");
		    	picObj.addEventListener("change", $scope.uploadPic);
		    	/*****************************************编辑个人信息保存******************************************/
		    	/* 功能：编辑个人信息后进行数据的保存
				 * 思路：将当前的编辑后的信息发送到后端
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
		    	$scope.save = function() {
		    		/*深拷贝编辑区域信息作为最新的数据发送到后端*/
		    		var sendWriteData = {
			    		name: $scope.writeInfo.name,
			    		sex: $scope.writeInfo.sex,
						studentID: $scope.writeInfo.studentID,
						className: $scope.writeInfo.class,
						major: $scope.writeInfo.major, 
						educationLevel: $scope.writeInfo.educationLevel,
						province: $scope.writeInfo.province,
						tele: $scope.writeInfo.tele,
						picURL: writeInfoPic.src
			    	};
		    		console.log("class", sendWriteData);
		    		netConnector.post("/modifyPsInfoAction.do", sendWriteData)
		    					.then(function(res) {
		    						if(res.data.modifyResult) {
		    							/*为了减少http请求,将当前编辑区域的信息合并到信息展示区域去*/
		    							angular.extend($scope.readInfo, sendWriteData);
		    							swal("信息修改成功!", "", "success");
		    							/*编辑成功后就切换到信息展示区域*/
		    							$scope.toggleSign = !($scope.toggleSign);
		    						} else {
		    							swal("信息修改失败!", "", "error");
		    						}
		    					})
		    					.catch(function(err) {
		    						console.error(err);
		    						swal("信息修改失败!", "", "error");
		    					});
		    		
		    	};
		    	
		    	/*****************************************编辑个人信息取消保存******************************************/
		    	/* 功能：取消保存
				 * 思路：取消保存,直接切换到信息展示区域
				 * 参数：null
				 * 作者：liao
				 * 修改时间：2018-05-08
				 */
		    	$scope.cancel = function() {
		    		$scope.toggleSign = !($scope.toggleSign);
		    	};
		    //ending controller
	    }]);
}());