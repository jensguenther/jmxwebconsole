steal(
		'jquery/controller',
		'jwc/topic',
		'jwc/models/api/v1/models/mbean_attribute_list_model.js')
	.then('jwc/widgets/tree_view', function($){
		
// ----------------------------------------------------------------------------------------------------------
// private
// ----------------------------------------------------------------------------------------------------------

const TYPE_JMXCONNECTION = 'jmxConnection';
const TYPE_DOMAIN = 'domain';
const TYPE_FOLDER = 'folder';
const TYPE_MBEAN = 'mBean';
const TYPE_MBEAN_ATTRIBUTE_LIST = 'mBeanAttributeList';
const TYPE_MBEAN_ATTRIBUTE = 'mBeanAttribute';

/**
 * Returns the given JMXConnectionModel as JSTree data representation.
 *  
 * @param jmxConnection the api.v1.models.JMXConnection to get the JSTreeData for, must be not null
 */
function getJSTreeData(jmxConnection){
	return {
		data : {
			attr : {
				id : jmxConnection.id
			},
			title : jmxConnection.host + ':' + jmxConnection.port
		},
		metadata : {
			type : TYPE_JMXCONNECTION,
			model : jmxConnection,
			url : '/api/v1/jmxconnection/'+jmxConnection.id+'/domain'
		},
		state : 'closed'
	};
}

/**
 * Enhances the given domainNodes metadata attributes and adjusts the class attributes.
 * 
 * @param jstree the jstree instance
 * @param jmxConnection the domainNodes jmxConnection
 * @param domainNodes the domain nodes to enhance
 */
function enhanceDomainNodes(jstree, jmxConnection, domainNodes){
	$.each(domainNodes, function(i, node){
		$(node).data({ 
			type : TYPE_DOMAIN,
			model : {
				id : jmxConnection.id + '/domain/' + jstree.get_text(node)
			},
			url : '/api/v1/jmxconnection/'+jmxConnection.id + '/domain/' + jstree.get_text(node)
		});
		$(node).addClass("jstree-closed").removeClass("jstree-leaf");
	});
}

function enhanceMBeanAttributeNodes(jstree, mBeanAttributeListModel, mBeanAttributeNodes){
	$.each(mBeanAttributeNodes, function(i, node){
		// add metadata information to the node
		$(node).data({
			type : TYPE_MBEAN_ATTRIBUTE,
			model : mBeanAttributeListModel.getMBeanAttributeModel(jstree.get_text(node))
		});
	});
}

// ----------------------------------------------------------------------------------------------------------
// controller
// ----------------------------------------------------------------------------------------------------------

/**
 * @class jwc.widgets.jmx.JMXTreeView
 * 
 * A JsTree based JMX Connection / Domain / MBean tree which retrieves its information from a REST api.
 * 
 * 
 * Options
 * 
 * When initializing JMXTreeView one can provide JsTree options to configure the tree. However, this class
 * comes with sensible defaults.
 * 
 * 
 * Drag & Drop
 * 
 * By default each node is drop-able on elements with a css class 'jmxModelDropTarget'. Additionally, that
 * element must have two functions:
 * 	
 * 	drop_check(model)
 *  drop_finish(model)
 * 
 * During a dnd session both methods will be used.
 *  
 * Why don't use $.Drag & $.Drop? Tried that already but something breaks the drag move. I couldn't find out 
 * what it is by now. 
 * 
 * 	 
 * Events
 * 
 * The JMXTreeView sends following events over the same named topics:
 * - jmxConnection.selected.jmxTree - JMXConnection
 * - domain.selected.jmxTree - Domain 
 * - folder.selected.jmxTree - Folder
 * - mBean.selected.jmxTree - MBean
 * - mBeanAttributeModel.selected.jmxTree - MBeanAttributeModel
 * - mBeanAttributeListModel.selected.jmxTree - MBeanAttributeListModel
 */
jwc.widgets.TreeView('jwc.widgets.jmx.JMXTreeView',
/** @Static */
{
	defaults : {
		core : {
			animation : 0
		},
		themes : {
			theme	: 'classic',
			dots	: true,
			icons	: true
		},
		json_data : {
			data : [], 

			ajax : {
				url : function(nodeArr){
					return $(nodeArr[0]).data('url');
				}
			},
			progressive_render : true
		},
		dnd : {
			drag_target : false,	// disables dragging foreign nodes into this tree
			drop_target : '.jmxModelDropTarget', // css class for drop targets. DO NOT CHANGE!
			drop_check : function(data){
				const dropTarget = data.r.closest('.jmxModelDropTarget')[0];
				if(	dropTarget.drop_check && $.isFunction(dropTarget.drop_check)
					&& dropTarget.drop_finish && $.isFunction(dropTarget.drop_finish)){
					return dropTarget.drop_check($(data.o).data('model'));
				} else {
					return false;
				}
			},
			drop_finish : function(data){
				const dropTarget = data.r.closest('.jmxModelDropTarget')[0];
				if(	dropTarget.drop_finish && $.isFunction(dropTarget.drop_finish)){
					dropTarget.drop_finish($(data.o).data('model'));
				}
			}
		},
		crrm : {
			move : {
				check_move : function(m){return false;} // disable moving within the tree
			}
		},
		plugins : [ 'ui', 'json_data', 'sort', 'themes', 'dnd', 'crrm' ]
	}
},
/** @Prototype */
{
	/**
	 * Constructor.
	 */
	init : function(){
		this._super();
		
		this._initEventHandling();
	},
	
	
	/**
	 * @hide
	 * INTERNAL FUNCTION
	 * 
	 * Initializes internal event propagation.
	 */
	_initEventHandling : function(){
		// internal event handling ---------------------------------------------------------------------------
		Topic.subscribe('load_node.jstree', this.proxy(this._handleLoadNode));
		Topic.subscribe('select_node.jstree', this.proxy(this._handleSelectNode));
	},
	
	/**
	 * @hide
	 * INTERNAL FUNCTION
	 * 
	 * Gets called after a node was loaded. Enhances the loaded children of that node to hold necessary data
	 * for loading the children's data from the server.
	 */
	_handleLoadNode : function(msg){
		const nodeType = $(msg.data.rslt.obj).data('type');
		const model = $(msg.data.rslt.obj).data('model');
		const nodeChildren = msg.data.inst._get_children(msg.data.rslt.obj);
		const jstree = msg.data.inst;
		
		switch (nodeType){
		case TYPE_JMXCONNECTION :
			enhanceDomainNodes(jstree, model, nodeChildren);
			break;
		case TYPE_MBEAN_ATTRIBUTE_LIST : 
			// if it was already loaded the MBeanAttributeListModel is stored within the model
			if(!model._mBeanAttributeListModel){
				// not yet loaded, prepare success function closure
				function success(mBeanAttributListModel){
					// store loaded data
					model._mBeanAttributeListModel = mBeanAttributListModel;
					// update the tree nodes
					enhanceMBeanAttributeNodes(jstree, model._mBeanAttributeListModel, nodeChildren);
				}
				
				jwc.models.api.v1.MBeanAttributeListModel.findOne({
					connId : model.connId,
					mBeanId : model.mBeanId
				}, success);
			} else {
				// MBeanAttributeListModel already loaded, enhance
				enhanceMBeanAttributeNodes(jstree, model._mBeanAttributeListModel, nodeChildren);
			}
			break;
		}
	},
	
	/**
	 * @hide
	 * INTERNAL FUNCTION
	 * 
	 * Gets called whenever a node on the tree view was selected.
	 * 
	 * @param msg.e the event object
	 * @param msg.data the jstree event data object
	 */
	_handleSelectNode : function(msg){
		const nodeType = msg.data.rslt.obj.data("type");
		const model = msg.data.rslt.obj.data("model");
		
		switch (nodeType){
		case TYPE_JMXCONNECTION :
			Topic.publish('jmxConnection.selected.jmxTree', model);
			break;
		case TYPE_DOMAIN : 
			Topic.publish('domain.selected.jmxTree', model);
			break;
		case TYPE_FOLDER : 
			Topic.publish('folder.selected.jmxTree', model);
			break;
		case TYPE_MBEAN :
			Topic.publish('mBean.selected.jmxTree', model);
			break;
		case TYPE_MBEAN_ATTRIBUTE_LIST :
			// if it was already loaded the MBeanAttributeListModel is stored within the model
			var loadedMBeanAttributeListModel = model._mBeanAttributeListModel;
			if(!loadedMBeanAttributeListModel){
				// not yet loaded, prepare success function closure
				function success(mBeanAttributListModel){
					// store loaded data
					model._mBeanAttributeListModel = mBeanAttributListModel;
					Topic.publish('mBeanAttributeListModel.selected.jmxTree', mBeanAttributListModel)
				}
				
				jwc.models.api.v1.MBeanAttributeListModel.findOne({
					connId : model.connId,
					mBeanId : model.mBeanId
				}, success);
			} else {
				// already loaded
				Topic.publish('mBeanAttributeListModel.selected.jmxTree', loadedMBeanAttributeListModel)
			}
			break;
		case TYPE_MBEAN_ATTRIBUTE :
			Topic.publish('mBeanAttributeModel.selected.jmxTree', model)
			break;
		}
	},
	
	/**
	 * Adds another api.v1.models.JMXConnectionModel to the tree.
	 */
	addJMXConnection : function(jmxConnection){
		this.element.jstree("create_node", this.element, "inside", getJSTreeData(jmxConnection));
	}
})		
});
