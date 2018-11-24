
/* 功能：登录界面控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("login", []);
		
		/* 功能：首页功能控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, $state----路由服务, netConnector----自定义前后端交互服务, netStorage----自定义缓存服务
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("loginCtrl",["$scope", "$state", "netConnector", "netStorage", function($scope, $state, netConnector, netStorage) {
	    	/*手动控制标志*/
	    	$scope.submitted = false;
	    	/*登录表单信息对象*/
	    	$scope.userInfomation = {};
	    	
	    	/*******************************************************登录按钮*******************************************************/
	    	/* 功能：点击登录
			 * 思路：访客填写登录表单信息，双向绑定获取数据，前端登录信息验证通过后发送到后台进行用户信息验证，合法就进入主页，否则停留在登录界面
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-04
			 */
	    	$scope.login = function(){
	    		/*页面表单验证判断标志*/
	    		$scope.submitted = true;
	    		$scope.notFound = false;
	    		$scope.errorPassword = false;
	    		/*前端表单验证通过后才向后台发送登录请求*/
	    		if(!($scope.loginForm.$invalid)) {
	    			/*发送到后台的登录信息*/
	    			var sendFormData = {
	    				username: $scope.userInfomation.username || null,
	    				password: $scope.userInfomation.password || null,
	    				userType: $scope.userInfomation.userType || null
	    			};
	    			/*请求后台进行验证*/
	    			netConnector.post("/loginAction.do", sendFormData)
	    						.then(function(res) {
	    							console.log(res);
	    							/*验证成功后跳至主页，缓存当前用户信息*/
	    							if(res.data.state === "success") {
	    								var userInfo = res.data.personInfo;
	    								netStorage.setItem("userInfo", userInfo);
	    								$state.go("navigators.home");
	    							} else if(res.data.state === "error") {/*密码错误，页面提示密码错误*/
	    								$scope.errorPassword = true;
	    							} else {/*用户名不存在，页面提示用户不存在*/
	    								$scope.notFound = true;
	    							}
	    						})
	    						.catch(function(err) {/*捕捉请求错误信息*/
	    							console.log(err)
	    						});
	    			
	    		} 
	    	};
	    	
	    	/*******************************************************重置按钮*******************************************************/
	    	/* 功能：点击重置
			 * 思路：将页面的表单数据置空，验证标志复原
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-04
			 */
	    	$scope.reset = function() {
	    		$scope.userInfomation = {};
	    		$scope.submitted = false;
	    		$scope.errorPassword = false;
	    		$scope.notFound = false;
	    	}
	    	
	    }])
}())
