/* default dom id (particles-js) */
//particlesJS();

/* config dom id */
//particlesJS('dom-id');

/* config dom id (optional) + config particles params */
//由于angular与jquery的加载先后顺序的问题（jq较为早加载），所以延迟一段时间让angular先执行完再进行执行，否则视图还没有进行跳转渲染
 setTimeout(function(){
		particlesJS('particles-js', {
		  particles: {
		    color: '#48D1CC',
		    shape: 'circle', // "circle", "edge" or "triangle"
		    opacity: 1,
		    size: 4,
		    size_random: true,
		    nb: 150,
		    line_linked: {
		      enable_auto: true,
		      distance: 100,
		      color: '#fff',
		      opacity: 0.8,
		      width: 1,
		      condensed_mode: {
		        enable: false,
		        rotateX: 600,
		        rotateY: 600
		      }
		    },
		    anim: {
		      enable: true,
		      speed: 1
		    }
		  },
		  interactivity: {
		    enable: true,
		    mouse: {
		      distance: 300
		    },
		    detect_on: 'canvas', // "canvas" or "window"
		    mode: 'grab',
		    line_linked: {
		      opacity: .5
		    },
		    events: {
		      onclick: {
		        enable: true,
		        mode: 'push', // "push" or "remove"
		        nb: 4
		      }
		    }
		  },
		  /* Retina Display Support */
		  retina_detect: true
		});
}, 250)