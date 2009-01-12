/*
Tooltips (c)2006 Matthias.Platzer@knallgrau.at, MIT-sytle LICENSE
depends on script.acolo.us (http://script.aculo.us).
v0.3 23.01.2006
*/

window.tooltips = [];
TooltipFactory = Class.create();
TooltipFactory.prototype = {
	initialize: function(selector, options) {
		this.selector = selector;
		this.elements = [];
		this.tooltips = [];
		this.options = options||{};
	},

	areSupported: function() {
		return (document.getElementById!=null);
	},

	activate: function(elements, options) {
		this.elements = document.getElementsBySelector(this.selector);
		Object.extend(this.options, options||{});
		for (var i=0; i<this.elements.length; i++){
			var tooltip = new Tooltip(this.elements[i], this);
			if (tooltip) {
				this.tooltips.push(tooltip);
			}
		}
	},

	activateOnLoad: function(options) {
		if (this.areSupported()) {
			Object.extend(this.options, options||{});
			Event.observe(window, "load", this.activate.bind(this));
		}
	}
}

Tooltip = Class.create();
Tooltip.prototype = {
	initialize: function(trigger, options) {
		this.trigger = trigger;
		if (options instanceof TooltipFactory) {
			this.factory = options;
			options = options.options;
		}
		this.setOptions(options);
		if (this.options.getPopUp) {
			this.popUp = this.options.getPopUp.bind(this)(event);
		} else if (!this.popUp && this.options.popUp) {
			this.popUp = $(this.options.popUp);
		} else if (this.trigger.href && this.trigger.href.indexOf("#") != -1) {
			this.popUp = $(this.trigger.href.split("#").pop());
		}
		if (!this.popUp && this.trigger.id) this.popUp = $(this.trigger.id + "PopUp");
		if (!this.popUp && this.trigger.title) {
			this.popUp = document.createElement("div");
			document.body.appendChild(this.popUp);
			this.popUp.className = this.options.popUpClassName;
			this.popUp.innerHTML = this.trigger.title;
			this.trigger.removeAttribute("title");
		}
		if (!this.popUp) return;

		window.tooltips.push(this);
		this.runningEffect = null;
		if (this.options.openingEvent==this.options.closingEvent) {
			Event.observe(this.trigger, this.options.openingEvent||'click', this.toggle.bind(this));
		} else {
			Event.observe(this.trigger, this.options.openingEvent||'mouseover', this.open.bind(this));
			Event.observe(this.trigger, this.options.closingEvent||'mouseout', this.close.bind(this));
		}
		if (this.options.onInitialize) this.options.onInitialize.bind(this)();
	},

	setOptions: function(options) {
		this.options = {
			queue: { 
				position: "end", 
				scope: "tooltip"+window.tooltips.length, 
				limit: 1
			}
		};
		Object.extend(this.options, Tooltips.options);
		Object.extend(this.options, options || {});
		if (this.setInitialPosition) this.setInitialPosition = this.options.setInitialPosition.bind(this);
		if (this.getPosition) this.getPosition = this.options.getPosition.bind(this);
		// opera can't handle opacity effects
		if (window.opera && this.options.effect.toLowerCase() == "appear") {
			this.options.duration = 0;
		}
	},

	open: function(event) {
		var duration = this.options.duration;
		if (this.runningEffect) {
			var queue = Effect.Queues.get(this.options.queue.scope);
			duration = Math.min(new Date().getTime() - this.runningEffect.startOn, duration);
			queue.remove(this.runningEffect);
		}
		if (this.options.setPosition) this.options.setPosition.bind(this)(event);
		this.runningEffect = Effect[Effect.PAIRS[this.options.effect][0]](this.popUp, Object.extend(Object.extend(
			Object.extend({}, this.options), {duration: duration}), this.options.open||{}
		));
		if (this.options.onOpen) this.options.onOpen.bind(this)(event);
	},

	close: function(event) {		
		var duration = this.options.duration;
		if (this.runningEffect) {
			var queue = Effect.Queues.get(this.options.queue.scope);
			duration = Math.min(new Date().getTime() - this.runningEffect.startOn, duration);
			queue.remove(this.runningEffect);
		}
		this.runningEffect = Effect[Effect.PAIRS[this.options.effect][1]](this.popUp, Object.extend(Object.extend(
			Object.extend({}, this.options), {duration: duration}), this.options.close||{}
		));
		if (this.options.onClose) this.options.onClose.bind(this)(event);
	},

	toggle: function(event) {
		Element.visible(this.popUp) ? this.close(event) : this.open(event);
	}
};

////// IMPLEMENTATIONS of TooltipFactory ///////
/// extend this as you need it

// element hover tooltips
Tooltips = new TooltipFactory(".tooltipTrigger", {
	popUpClassName: "tooltip",
	offsetLeft: 20,
	offsetTop: 10,
	effect: "appear",
	duration: 0.7,
	openingEvent: "mouseover",
	closingEvent: "mouseout",
	onInitialize: function() {
		this.popUp.style.position = "absolute";
		this.popUp.style.left = "0";
		this.popUp.style.top = "0";
		Element.hide(this.popUp);
	},
	onOpen: function(event) {
		var x = Event.pointerX(event) + this.options.offsetLeft;
		var triggerY = Position.cumulativeOffset(this.trigger)[1];
		var y = triggerY + this.trigger.offsetHeight + this.options.offsetTop;
		Position.prepare();
		var popUp = Element.getDimensions(this.popUp);
		if (x + popUp.width + this.options.offsetLeft > (Position.deltaX+Position.visibleWidth)) 
			x = Math.max( this.options.offsetLeft, ((Position.deltaX+Position.visibleWidth) - popUp.width - this.options.offsetLeft));
		if (y + popUp.height + this.options.offsetTop > (Position.deltaY+Position.visibleHeight)) {
			y = triggerY - popUp.height - this.options.offsetTop;
		}
		this.popUp.style.left = x+"px";
		this.popUp.style.top = y+"px";
	}
});


// button/link info: appears after klicking something
ActionHints = new TooltipFactory(".actionHintTrigger", {
	popUpClassName: "tooltip",
	offsetLeft: 20,
	offsetTop: 20,
	effect: "appear",
	openingEvent: "click",
	closingEvent: "mouseout",
	open: {
		duration: 0.0
	},
	close: {
		delay: 1.0,
		duration: 1.0
	},
	onInitialize: function() {
		this.popUp.style.position = "absolute";
		this.popUp.style.left = "0";
		this.popUp.style.top = "0";
		Element.hide(this.popUp);
	},
	onOpen: function(event) {
		var triggerPos = Position.cumulativeOffset(this.trigger);
		var x = triggerPos[0] + this.options.offsetLeft;
		var y = triggerPos[1] - this.options.offsetTop - this.trigger.offsetHeight;
		Position.prepare();
		var popUp = Element.getDimensions(this.popUp);
		if (x + popUp.width + this.options.offsetLeft > (Position.deltaX+Position.visibleWidth)) 
			x = Math.max( this.options.offsetLeft, ((Position.deltaX+Position.visibleWidth) - popUp.width - this.options.offsetLeft));
		this.popUp.style.left = x+"px";
		this.popUp.style.top = y+"px";
	}
});

// trigger for showing/hidding a section in your page
SectionTriggers = new TooltipFactory(".sectionTrigger", {
	effect: "appear",
	openingEvent: "click",
	closingEvent: "click",
	text: {
		show: "show",
		hide: "hide"
	},
	duration: 1.0,
	onInitialize: function() {
		// Element.hide(this.popUp);
	},
	onOpen: function(event) {
		this.trigger.innerHTML = this.trigger.getAttribute("hide")||this.options.text.hide;
		Event.stop(event);
	},
	onClose: function(event) {
		this.trigger.innerHTML = this.trigger.getAttribute("show")||this.options.text.show;
		Event.stop(event);
	}
});
