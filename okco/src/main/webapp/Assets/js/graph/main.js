//
//  main.js
//
//  A project template for using arbor.js
//


/*
	Outros exemplos:
	http://nooshu.com/visual-thesaurus-using-arbor-js
	http://www.joeloughton.com/blog/testing/netmapjs/examples/karen/karen-detail.html
*/

(function($){

  Renderer = function(canvas){
    var canvas = $(canvas).get(0)
    var ctx = canvas.getContext("2d");
    var gfx = arbor.Graphics(canvas)
    var particleSystem = null
	var classHash;

	    var that = {
      init:function(system){
        particleSystem = system
        particleSystem.screenSize(canvas.width, canvas.height) 
        particleSystem.screenPadding(100)
		
		classHash = getHash();
		
        that.initMouseHandling()
      },

      redraw:function(){
        if (!particleSystem) return

        gfx.clear() // convenience ƒ: clears the whole canvas rect

        // draw the nodes & save their bounds for edge drawing
        var nodeBoxes = {}
        particleSystem.eachNode(function(node, pt){
          // node: {mass:#, p:{x,y}, name:"", data:{}}
          // pt:   {x:#, y:#}  node position in screen coords		 
		
		  var w = 18;
		  var space = pt.y+23;
		  var color = node.data.color;
		  //draw the objects	
		  if (node.data.shape=='dot'){
            gfx.oval(pt.x-w/2, pt.y-w/2, w,w, {fill:color});
	    	nodeBoxes[node.name] = [pt.x-w/2, pt.y-w/2, w,w];
          }else if (node.data.shape=='default'){
             gfx.rect(pt.x-w/2, pt.y-10, w,w, 0, {fill:color})
            nodeBoxes[node.name] = [pt.x-w/2, pt.y-11, w, w]
          }else if(node.data.shape==undefined){
            gfx.rect(pt.x-w/2, pt.y-10, w,w, 0, {fill:color})
            nodeBoxes[node.name] = [pt.x-w/2, pt.y-11, w, w]
		  }else{
			var img=document.getElementById(node.data.shape);
			ctx.fillStyle = "white";		
			w += 9;
			gfx.rect((pt.x-w/2)+5, pt.y-14, w,w, 0, {fill:ctx.fillStyle})
	        nodeBoxes[node.name] = [(pt.x-w/2)+5, pt.y-14, w, w]
			ctx.drawImage(img,pt.x-w/2,pt.y-10);	
		 	space+=8;
          }
       	 ctx.fillStyle = "black";
         ctx.font = '13px sans-serif';
         ctx.fillText (node.name, pt.x-((node.name.length/3)*7), space);
        })    			

        // draw the edges
        particleSystem.eachEdge(function(edge, pt1, pt2){
          // edge: {source:Node, target:Node, length:#, data:{}}
          // pt1:  {x:#, y:#}  source position in screen coords
          // pt2:  {x:#, y:#}  target position in screen coords

          var weight = edge.data.weight
          var color = edge.data.color

          if (!color || (""+color).match(/^[ \t]*$/)) color = null

          // find the start point
          var tail = intersect_line_box(pt1, pt2, nodeBoxes[edge.source.name])
          var head = intersect_line_box(tail, pt2, nodeBoxes[edge.target.name])

          ctx.save() 
            ctx.beginPath()
            ctx.lineWidth = (!isNaN(weight)) ? parseFloat(weight) : 2
            ctx.strokeStyle = (color) ? color : "#585858"
            ctx.fillStyle = null

            ctx.moveTo(tail.x, tail.y)
			ctx.lineTo(head.x, head.y);	
            ctx.stroke()
	
			//For labeled edge
			ctx.fillStyle = "red";
		    ctx.font = 'bold 13px sans-serif';
			
			//ctx.fillText (edge.data.name, ((pt1.x + pt2.x) / 2)-((edge.data.name.length/3)*10), ((pt1.y + pt2.y) / 2)-5);			
			
			//to put newline in filltext we need to put one-up-one
			var nameSize = ctx.measureText(edge.data.name);
			var x = ((pt1.x + pt2.x) / 2)-((edge.data.name.length/3)*2);
			var y = ((pt1.y + pt2.y) / 2);
			var lineHeight = nameSize.width ;
			var lines = edge.data.name.split(",");
			for (var i = 0; i < lines.length; ++i) {
				ctx.fillText(lines[i], x, y-((lines.length)/2-i)*10);				
			}
			
			
			

          ctx.restore()

          // draw an arrowhead if this is a -> style edge
            ctx.save()
              // move to the head position of the edge we just drew
              var wt = !isNaN(weight) ? parseFloat(weight) : 2
              var arrowLength = 6 + wt
              var arrowWidth = 2 + wt
              ctx.fillStyle = (color) ? color : "#585858"
              ctx.translate(head.x, head.y);
              ctx.rotate(Math.atan2(head.y - tail.y, head.x - tail.x));

              // delete some of the edge that's already there (so the point isn't hidden)
              ctx.clearRect(-arrowLength/2,-wt/2, arrowLength/2,wt)

              // draw the chevron
              ctx.beginPath();
              ctx.moveTo(-arrowLength, arrowWidth);
              ctx.lineTo(0, 0);
              ctx.lineTo(-arrowLength, -arrowWidth);
              ctx.lineTo(-arrowLength * 0.8, -0);
              ctx.closePath();
              ctx.fill();
            ctx.restore()
			
			
			if(edge.data.inverse=="true"){
			head = intersect_line_box(pt1, pt2, nodeBoxes[edge.source.name])
			tail = intersect_line_box(tail, pt2, nodeBoxes[edge.target.name])
			  ctx.save()
              // move to the head position of the edge we just drew
              var wt = !isNaN(weight) ? parseFloat(weight) : 2
              var arrowLength = 6 + wt
              var arrowWidth = 2 + wt
              ctx.fillStyle = (color) ? color : "#585858"
              ctx.translate(head.x, head.y);
              ctx.rotate(Math.atan2(head.y - tail.y, head.x - tail.x));

              // delete some of the edge that's already there (so the point isn't hidden)
              ctx.clearRect(-arrowLength/2,-wt/2, arrowLength/2,wt)

              // draw the chevron
              ctx.beginPath();
              ctx.moveTo(-arrowLength, arrowWidth);
              ctx.lineTo(0, 0);
              ctx.lineTo(-arrowLength, -arrowWidth);
              ctx.lineTo(-arrowLength * 0.8, -0);
              ctx.closePath();
              ctx.fill();
            ctx.restore()
			}
        })

      },
      initMouseHandling:function(){
        // no-nonsense drag and drop (thanks springy.js)
        selected = null;
        nearest = null;
        var dragged = null;
        var oldmass = 1

        // set up a handler object that will initially listen for mousedowns then
        // for moves and mouseups while dragging
        var handler = {
          clicked:function(e){
            var pos = $(canvas).offset();
            _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
            selected = nearest = dragged = particleSystem.nearest(_mouseP);

            if (dragged.node !== null){
				dragged.node.fixed = true
				handler.showClasses(dragged.node.name);
			}

            $(canvas).bind('mousemove', handler.dragged)
            $(window).bind('mouseup', handler.dropped)

            return false
          },
          dragged:function(e){
            var old_nearest = nearest && nearest.node._id
            var pos = $(canvas).offset();
            var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)

            if (!nearest) return
            if (dragged !== null && dragged.node !== null){
              var p = particleSystem.fromScreen(s)
              dragged.node.p = p
            }

            return false
          },

          dropped:function(e){
            if (dragged===null || dragged.node===undefined) return
            if (dragged.node !== null) dragged.node.fixed = false
			
			dragged.node.tempMass = 1000
            dragged = null
            selected = null
            $(canvas).unbind('mousemove', handler.dragged)
            $(window).unbind('mouseup', handler.dropped)
            _mouseP = null	
				
            return false
          },
		  
		  showClasses:function(nodeName){		 
			if(classHash[nodeName] == null)
				$('#currentNode').html("Not defined type");
			else	
				$('#currentNode').html(classHash[nodeName]);
		  }
        }
        $(canvas).mousedown(handler.clicked);
      }
    }

    // helpers for figuring out where to draw arrows (thanks springy.js)
    var intersect_line_line = function(p1, p2, p3, p4)
    {
      var denom = ((p4.y - p3.y)*(p2.x - p1.x) - (p4.x - p3.x)*(p2.y - p1.y));
      if (denom === 0) return false // lines are parallel
      var ua = ((p4.x - p3.x)*(p1.y - p3.y) - (p4.y - p3.y)*(p1.x - p3.x)) / denom;
      var ub = ((p2.x - p1.x)*(p1.y - p3.y) - (p2.y - p1.y)*(p1.x - p3.x)) / denom;

      if (ua < 0 || ua > 1 || ub < 0 || ub > 1)  return false
      return arbor.Point(p1.x + ua * (p2.x - p1.x), p1.y + ua * (p2.y - p1.y));
    }

    var intersect_line_box = function(p1, p2, boxTuple)
    {
      var p3 = {x:boxTuple[0], y:boxTuple[1]},
          w = boxTuple[2],
          h = boxTuple[3]

      var tl = {x: p3.x, y: p3.y};
      var tr = {x: p3.x + w, y: p3.y};
      var bl = {x: p3.x, y: p3.y + h};
      var br = {x: p3.x + w, y: p3.y + h};

      return intersect_line_line(p1, p2, tl, tr) ||
            intersect_line_line(p1, p2, tr, br) ||
            intersect_line_line(p1, p2, br, bl) ||
            intersect_line_line(p1, p2, bl, tl) ||
            false
    }

    return that
  }

	
	
  })(this.jQuery);
  
  function startArbor(canvasName){		
		var sys = arbor.ParticleSystem(1300, 10000, 0.99) // create the system with sensible repulsion/stiffness/friction
		sys.parameters({gravity:false}) // use center-gravity to make the graph settle nicely (ymmv)
		sys.renderer = Renderer(canvasName) // our newly created renderer will have its .init() method called shortly by sys...	
		return sys;
 } 